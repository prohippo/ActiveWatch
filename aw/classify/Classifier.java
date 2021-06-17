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
// AW file Classifier.java : 16May00 CPM
// classification by cluster profile

package aw.classify;

import aw.*;
import object.*;
import java.io.*;

// access to file information about vector

class ClassifyIndexVector extends IndexVector {

	public ClassifyIndexVector (
		int bn,
		int ss
	) throws AWException {
		super(bn,ss);
	}

	public final int offset ( ) { return vo.offset; }
	
	public final int length ( ) { return vc.vl; }

}

// compares sequence of messages against
// current profiles, records matches on
// lists and writes new residual list

public class Classifier extends ClassifierBase {

	Sequence seq; // items to classify
	Residual rsd; // unclassified items

	public Classifier (
		char    type,  // which profiles to employ
		boolean logic  // whether to have logical dependence
	) throws AWException {
	
		super(type,logic);
		seq = new Sequence("sequence");
		try {
			rsd = new Residual();
		} catch (AWException e) {
			System.out.println("no residual file found");
			rsd = new Residual(0);
		}
		
	}
	
	// classify all items in specified sequence
		
	private int nvo=0; // file offset to end of vector
	private int cvo=0; // ... to start of vector
	private int svo=0; // saved offset
	
	public void run (
	
	) throws AWException {
	
		int lbn; // last batch
		int lks; // last subsegment  matched
		int ihs; // item matches
		int shs; // subsegment matches
		int nbs; // bytes to read in run
		int nom; // number of matches
		int ssi; // subsegment index in item
		int j,k;
		String x;

		Run run;
		System.out.println("classifying " + seq.nmssg + " subsegments");

		ihs = shs =  0;
		lbn = lks = -1;
		ssi =  0;
		for (int i = 0; i < seq.nmrun; i++) {
			
			// initialize residual list recording

			run = seq.r[i];				
			rsd.reset(run);
			nbs = run.rln;
			cvo = nvo = run.rfo;
			
			int ks;
				
			for (k = ks = run.ssn, j = 0; j < run.nss; k++, j++) {

				// match each item not yet examined

				ssi = scan(run.bbn,k);
				if (ssi == 1) {
				
					// remember first subsegment in item
					
					svo = cvo;
					ks = k;
					
				}

				nom = countHits();
				if (nom > 0) {
 
					// item was added to some match list,
					// breaking residuals sequence

					shs++;

					if (lbn != run.bbn || lks != ks) {
					
						// count only one subsegment hit per item
						
						lbn = run.bbn;
						lks = ks;
						ihs++;
						
					}
					
					System.out.print("**** item");
					x = " " + run.bbn;
					if (x.length() < 3)
						System.out.print(" ");
					System.out.print(x);
					x = "::"  + k;
					System.out.print(x);
					for (int m = x.length(); m < 4; m++)
						System.out.print(" ");
					System.out.print(" in " + nom + " list");
					x = (nom == 1) ? "" : "s";
					System.out.println(x);
					
					cvo = svo;
					rsd.record(ks,ssi,cvo,nvo);
					
				}
				else if (lbn == run.bbn && k - ssi + 1 == lks) {

					// unmatched item is not residual
					// if preceding subsegment in same
					// document was matched

					rsd.record(ks,ssi,cvo,nvo);
					
				}
			}

			// write out run from last match to end

			cvo = nvo;
			nvo = 0;
			rsd.record(ks + ssi,0,cvo,nvo);

		}
						
		// save matches and clean up
		
		recordAllHits(true);
			
		rsd.close();
		
		if (iv != null)
			iv.close();
			
		System.out.println(ihs + " items matched against " + nop + " active profiles");
		System.out.println(rsd.nmssg + " residual subsegments out of " + seq.nmssg);
		
	}
	
	ClassifyIndexVector iv;
	
	// scan all profiles of specified type against
	// the next index vector in a (static) buffer
	
	private int scan (

		int bno,  // data base batch number
		int ssg   // text subsegment in batch

	) throws AWException {
	
		int sx;     // subsegment index
		int nm;     // number of matches
		int sg;     // significance score
		short[] ml; // match list
		short[] ms; // score list
		Topic tp;
		Item  it;
		ProfileList ls;

		iv = new ClassifyIndexVector(bno,ssg);
		sx = iv.subsegmentIndex();

		// compute new vector offsets

		cvo = nvo;
		nvo += iv.length();

		// get matches with profiles

		nm = matchProfiles(iv);
		ml = getTopics();
		ms = getScores();

		// record matches in topic lists

		for (int i = 0; i < nm; i++) {
			tp = top[ml[i]];
			if (tp.tlog.tmp == 0) {
				if (tp.nit == NLI) {
					System.out.println("save list");
					ls = new ProfileList(tp.its,tp.nit);
					ls.save(tp.pno,true);
					tp.nit = 0;
				}
				tp.its[tp.nit] = new Item(bno,ssg,(double)(ms[i])/SCALE);
				tp.nit++;
			}
		}
		
		return sx; // relative subsegment number for vector
	}
	
}
