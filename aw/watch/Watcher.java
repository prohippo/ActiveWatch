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
// AW file Watcher.java : 08sep2023 CPM
// find statistical standouts in AW residual items

package aw.watch;

import aw.*;
import object.*;
import java.io.*;

// select vectors with low- or high-probability indices

public class Watcher {

	private Probabilities pbs;
	private int  nselL;  // low-probability
	private Rec[] selL;
	private int  nselH;  // high-
	private Rec[] selH;
	private int  nssg;
	private Sequence seq;

	// initialize for given sequence

	public Watcher (
		String seqfile
	) throws AWException {

		// load all index probabilities

		pbs = new Probabilities();

		// get sequence of segments to scan

		seq = new Sequence(seqfile);
		nssg = seq.nmssg;
		if (nssg == 0)
			throw new AWException("empty sequence");

	}

	// selection record for standouts

	private class Rec {
		short bn;  // batch number
		short sn;  // segment index
		short len; // vector length
		float smp; // sum of probabilities
	}

	// get m text segments with lowest sums of probablities

	public void run (
		int    ml,     // minimum vector length
		int    nlo,    // low -probability count
		int    nhi     // high-
	) throws AWException { 

		// allocate selection array

		nselL = 0;              // initialize empty sorting arrays
		selL  = new Rec[nlo+1]; //
		nselH = 0;              //
		selH  = new Rec[nhi+1]; //

		// scan each run in sequence

		int ncnd = 0;

		for (int i = 0; i < seq.nmrun; i++) {

			// get subsegment vectors of each run

			short bn = seq.r[i].bbn;
			short sn = seq.r[i].ssn;
			IndexVectors siv = new IndexVectors(bn,seq.r[i].rfo,seq.r[i].rln);

			for (int j = 0; j < seq.r[i].nss; j++) {

				// look only at first subsegment of any item

				int ssx = siv.subsegmentIndex();

				if (ssx == 1) {

//					System.out.print(String.format("-------- sn=%d\n",sn));

					// compute sum of probabilities

					int    lvc = 0;
					double smp = 0;
					while (siv.next()) {
						int tu[] = siv.getVectorTuple();
//						System.out.print(String.format("%d|%d\n",tu[0],tu[1]));
						smp += pbs.array[tu[0]];
						lvc += tu[1];
					}

					if (lvc < ml) continue;

//					System.out.print(String.format("lvc=%d smp=%10.8f\n",lvc,smp));
					ncnd += 1; 

					Rec nr = new Rec();  // new record for sorting
					nr.bn = bn;          //
					nr.sn = sn;          //
					nr.len = (short)lvc; //
					nr.smp = (float)smp; //

					int k; // shared sorting index

					// select lowest sums

					k = nselL;
					for (; k > 0; --k) {
						if (selL[k-1].smp <= smp)
							break;
						selL[k] = selL[k-1];
					}
					selL[k] = nr;
					if (nselL < nlo) nselL++;

					// select highest sums

					k = nselH;
					for (; k > 0; --k) {
						if (selH[k-1].smp >= smp)
							break;
						selH[k] = selH[k-1];
					}
					selH[k] = nr;
					if (nselH < nhi) nselH++;

				}

				// go to next vector

				siv.skip();
				sn += 1;
			}
  
		}

	}

	// show selected standouts in standard output

	public void showSelection (

	) {

		System.out.printf("low probabilities=%d, high probabilities=%d\n",nselL,nselH);
		int k;
		String fmt = "%3d) %2d::%-5d %8.6f (%4d)\n";
		for (k = 0; k < nselL; k++) {
			Rec r = selL[k];
			System.out.print(String.format(fmt,k+1,r.bn,r.sn,r.smp,r.len));
		}
		if (nselL > 0 && nselH > 0)
			System.out.println("      ...");
		for (k = nselH - 1; k >= 0; --k) {
			Rec r = selH[k];
			System.out.print(String.format(fmt,nselL+nselH-k,r.bn,r.sn,r.smp,r.len));
		}

	}

}

