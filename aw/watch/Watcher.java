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
// AW file Watcher.java : 20nov2021 CPM
// find statistical standouts in AW sequence of items

package aw.watch;

import aw.*;
import object.*;
import java.io.*;

// scan sequenced vectors for low-probability indices

public class Watcher {

	private Probabilities pbs;
	private int  nsel;
	private Rec[] sel;
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

	public int run (
		int    ml,     // minimum vector length
		int    mx,     // maximum number to select
		double maxsum  // maximum sum of probabilities for selection
	) throws AWException { 

		// allocate selection array

		nsel = 0;
		sel  = new Rec[mx+1];

		for (int i = 0; i <= mx; i++)
			sel[i] = new Rec();

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

					double smp = 0;
					int    lvc = 0;
					while (siv.next()) {
						int tu[] = siv.getVectorTuple();
//						System.out.print(String.format("%d|%d\n",tu[0],tu[1]));
						smp += pbs.array[tu[0]];
						lvc += tu[1];
					}

					if (lvc < ml || smp > maxsum) continue;

//					System.out.print(String.format("lvc=%d smp=%10.8f\n",lvc,smp));
					ncnd += 1; 

					// select mx lowest sums

					Rec r = sel[nsel];
					int k = nsel;
					for (; k > 0; --k) {
						if (sel[k-1].smp <= smp)
							break;
						sel[k] = sel[k-1];
					}
					r.bn = bn;
					r.sn = sn;
					r.len = (short)lvc;
					r.smp = (float)smp;
					sel[k] = r;
					if (nsel < mx) nsel++;

				}

				// go to next vector

				siv.skip();
				sn += 1;
			}
  
		}
		System.out.print(String.format("%d segments selected from %d candidates\n",nsel,ncnd));

		return nsel;
	}

	// show selected standouts in standard output

	public void showSelection (

	) {
		String fmt = "%3d) %2d::%-5d %10.8f (%4d)\n";
		for (int k = 0; k < nsel; k++) {
			Rec r = sel[k];
			System.out.print(String.format(fmt,k+1,r.bn,r.sn,r.smp,r.len));
		}
	}

}
