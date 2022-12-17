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
// AW file Summarizer.java : 09dec2022 CPM
// derive n-gram profiles from cluster seeds

package aw.summarize;

import aw.*;
import object.*;
import java.io.*;

public class Summarizer {

	private ProfileMap  map;
	private LinkMapping lkm;
	private Control     ctl;

	// initialization

	public Summarizer (

	) throws AWException {

		map = new ProfileMap();
		lkm = new LinkMapping();
		ctl = new Control();

		map.update(); // turn former NEW indications
	}

	private static final int  MX = 32; // maximum cluster seed size
	private static final byte TYPE = (byte)(Map.bN | Map.bA | Map.bD);

	// set up sequence file

	public void run (

		float thr, // minimum significance for match
		int   mlp, // minimum n-gram probability as multiple of least
		int   lpm  //

	) throws AWException {

		int   mm; // link index number
		int nitm; // item count
		Item[] its = new Item[MX]; // items of cluster seed
		int [] vw  = new int[MX];  // vector weights

		Member cm = null; // cluster member record

		Attribute   at = new Attribute();
		ProfileList ls = new ProfileList(null,0);
		ClusterProfile cp = null;

		ls.reset(); // to avoid problems after clearing files

		String x; // for formatting
		short pn; // profile number
		int   lp; // profile length
		int   nn;
		int   no = 0;

		long  tm = System.currentTimeMillis();

		int left = lkm.countLinkIndex(); 
		System.out.println(left + " items originally to be clustered");

		try {

			for (;;) {		
				cm = new Member();
				mm = cm.index;

				// collect items in cluster

				System.out.print("\ncollect");

				for (nitm = 0; mm > 0; nitm++) {
					if (nitm == MX)
						throw new AWException("cluster overflow");

					its[nitm] = lkm.fromLinkIndex(mm);

					if (its[nitm].bn == Control.BADBatch)
						throw new AWException("bad item for index " + mm);

					vw[nitm] = cm.strength;
					--left;

					try {
						cm = new Member();
						mm = cm.index;
					} catch (EOFException e) {
						mm = -1;
					}
				}

				// show cluster seed members

				System.out.print(" cluster:");
				for (int j = 0; j < nitm; j++) {
					if (j%9 == 0)
						System.out.println("");
					System.out.print(" ");
					x = " " + its[j].bn;
					if (x.length() == 1)
						System.out.print(" ");
					System.out.print(x);
					x = "::" + its[j].xn;
					System.out.print(x);
					for (int k = x.length(); k < 6; k++)
						System.out.print(" ");
				}
				System.out.print("\n\n");

				// make profile from accumulated vectors

				System.out.println("make profile from " + nitm + " items");
				cp = new ClusterProfile(nitm,its,vw,mlp,lpm);
				lp = cp.length;

				// check for thin profile

				if (lp < lpm) {
					System.out.println("-- too thin to keep: " + lp + " < " + lpm);
					continue;
				}

				// allocate new slot for cluster

				pn = map.allocate(TYPE);
				if (pn < 0)
					continue;

				at.stm.genum = (short) ctl.totb;
				System.out.println("allocate as number " + pn);
				no++;

				// write out empty cluster list

				ls.save(pn,false);
 
				// set hit thresholds

				if ((nn = lp/10) == 0)
					nn++;
				if (nn > 10)
					nn = 10;
				cp.profile.nhth = (short) nn;
				cp.profile.sgth = thr;
				cp.save(pn);
 
				// write cluster attributes
				// plus null keys

				at.stm.cdate = tm;
				at.stm.mdate = at.stm.rdate = 0;
				at.stm.nrold = at.stm.nrnew = 0;
				at.save(pn);

			}

		} catch (EOFException e) {

		} catch (IOException e) {
			throw new AWException(e);
		}

		System.out.println("\n" + no + " new profiles");

		try {
			map.save();
			if (cm != null) {
				cm.close();
				cp.close();
				at.close();
			}
		} catch (IOException e) {
			throw new AWException("cannot write map");
		}
	}

}
