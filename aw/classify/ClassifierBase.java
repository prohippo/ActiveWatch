// Copyright (c) 2021, C. P. Mah
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//   Redistributions of source code must retain the above copyright notice, this
//   list of conditions and the following disclaimer.
//
//   Redistributions in binary form must reproduce the above copyright notice,
//   this list of conditions and the following disclaimer in the documentation
//   and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// -----------------------------------------------------------------------------
// AW file ClassifierBase.java : 06Aug01 CPM
// classification by cluster profile

package aw.classify;

import aw.*;
import object.*;
import java.io.*;
import java.util.Date;

public class ClassifierBase {

	public static final int SCALE = 128;       // for fixed-point scores
	public static final int NLI   = Topic.NLI; // buffer size for hits

	private Map map; // what profiles are available
	
	protected Topic[] top; // topics to match against

	protected short[] actpns; // profile number mapping
	protected int        nop; // number of profiles
	
	private ControlChart chrt;
	private Alarm alrm;

	// initialization
		
	public ClassifierBase (
	
		char    type,  // which profiles to employ
		boolean logic  // whether to have logical dependence
	
	) throws AWException {
	
		int  i,n;
		int  k,kn;
		int  sgn;
		byte b=0;    // topic type
		
		map = new Map();
		top = new Topic[Map.MXSP];

		// what topics to match
		
		switch (Character.toLowerCase(type)) {
case 'n':	b = Map.bN;
			break;
case 'u':	b = Map.bU;
			break;
case 'a':	b = Map.bD;
		}
		
		// fill topics
		
		Topic tp = null;
		TruncatedAttribute at;
	
		actpns = new short[Map.MXSP+1];

		for (nop = 0, i = 1; i <= Map.MXSP; i++) {
		
			if (!map.activeType(i))
				continue;
			if (!map.typeOf(i,b))
				continue;

			// define topic for active profile of specified type
			
			tp = top[nop++] = new Topic();

			tp.pno = (short) i;

			if (logic)
				tp.tlog = new Logic(i);
			else
				tp.tlog = new Logic();
					
			tp.tpro = new ProfileWithSimilarity(i);
			
			// encode logic for active profiles

			kn = tp.tlog.ncj + tp.tlog.nds;

			for (int j = 0; j < kn; j++) {
				n = tp.tlog.depend[j];
				if (n >= 0)
					sgn = 1;
				else {
					sgn = -1;
					n = -n;
				}

				if (actpns[n] > 0)
					n = sgn*actpns[n];
				else if (sgn > 0)
					n = nop; // impossible match
				else
					n = 0;
				tp.tlog.depend[j] = (short) n;
			}

			// load attributes

			at = new TruncatedAttribute(i);
			tp.tstm = at.stamp();
			tp.tstm.nrold += tp.tstm.nrnew;
			tp.tstm.nrnew = 0;

			actpns[i] = (short) nop;
			
		}
		
		// free up objects no longer needed
		
		if (tp != null) {
			tp.tpro.close();
			tp.tlog.close();
		}
		
		// set up for no process control
		
		alrm = new Alarm();
		chrt = new ControlChart();
			
	}
	
	// turn on statistical process control
	
	public void setProcessControl (
		ControlChart chrt,
		Alarm alrm
	) {
		this.chrt = chrt;
		this.alrm = alrm;
	}

	// clear current profiles
	
	public void clear (
	
	) throws AWException {
		nop = 0;
		for (int i = 0; i < actpns.length; i++)
			actpns[i] = 0;
		chrt.save();
		
	}
	
	static final int MX = 2*Parameter.MXV;

	private short   mk; // profile hit count
	private short[] pmr = new short[Map.MXSP]; // profile hit list
	private short[] psm = new short[Map.MXSP]; // similarity scores
	private short[] ptp = new short[Map.MXSP]; // topic numbers

	// compare vector against all profiles
	
	public int matchProfiles (

		IndexVector vector

	) {
		boolean mtch;
		boolean cods;

		// decompress vector for computations
		
		byte[] fv = vector.expand();
		int fln = vector.storedSum();

		// scan all selected profiles

		for (short p = mk = 0; p < nop; p++) {

			Topic tp = top[p];

			// check pre-conditions for next profile

			int n = tp.tlog.ncj + tp.tlog.nds;
			cods = mtch = true;
			for (int i = 0; i < n; i++) {
				if (i == tp.tlog.ncj)
					cods = false;

				int k = tp.tlog.depend[i];
				if (k < 0) {
					k = -k;
					for (int j = 0; j < mk; j++) {
						mtch = (pmr[j] != k);
						if (!mtch)
							break;
						if (pmr[j] > k)
							break;
					}
				}
				else if (k > 0) {
					mtch = false;
					for (int j = 0; j < mk; j++) {
						mtch = (pmr[j] == k);
						if (mtch)
							break;
						if (pmr[j] > k)
							break;
					}
				}
				if (mtch != cods)
					break;
			}

			// compute match with profile

			float xs = tp.tpro.match(fv,fln);
			
			if (tp.tlog.nil == 0) {
				try {
					int ml = chrt.mark(xs,tp.pno);
					if (ml > 0)
						alrm.trigger(p,ml);
				} catch (AWException e) {
					System.err.println("chart failure");
				}
			}

			// check whether all match conditions
			// are satisfied for profile

			if (tp.tpro.hitsMet())
				if (tp.tpro.noMisses() || tp.tpro.significanceMet()) {

					// check profile dependence and postfilter
					
					if (!mtch || !tp.tpro.filtersMet())
						continue;
 
 					Date now = new Date();
					tp.tstm.nrnew++;
					tp.tstm.mdate = now.getTime();
					float th = tp.tpro.sgth;
					ptp[mk  ] = p;
					pmr[mk  ] = top[p].pno;
					psm[mk++] = (xs < th) ? 0 : (short)(xs*SCALE);
				}
				
		}
		return mk;
	}

	// miscellaneous methods to access profile hits
	
	public final int  countHits ( ) { return mk; }

	public final void resetHits ( int n ) { mk = (short) n; }
	
	public final short[] getTopics ( ) { return ptp; }

	public final short[] getHits   ( ) { return pmr; }

	public final short[] getScores ( ) { return psm; }

	// save hits to a file
	
	public void recordAllHits (
	
		boolean all
		
	) throws AWException{
	
		Topic tp;
		ProfileList ls;
		TruncatedAttribute at = null;

		for (int i = 0; i < nop; i++) {
			tp = top[i];
			at = new TruncatedAttribute(tp.tstm);
			at.save(tp.pno);

			if (all) {
				ls = new ProfileList(tp.its,tp.nit);
				ls.save(tp.pno,true);
			}
		}
		
		if (at != null)
			at.close();

		chrt.save();
		
	}
	
}
