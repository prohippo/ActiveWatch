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
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISI:wNG IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// -----------------------------------------------------------------------------
// Dtop.java : 07sep2023 CPM
// get highest matches among top items of clusters

package test;

import aw.*;
import object.GappedProfileList;

public class Dtop {

	private final static String sigma = "\u03C3";

	private static class Rec {
		int   bn; // batch number
		int   in; // index number
		int   pn; // profile number
		float sg; // significance
		Rec (Item itm, int pno) {
			bn = itm.batch();
			in = itm.index();
			pn = pno;
			sg = (float) itm.score();
		}
	}

	private static Rec[] reco;       // for aorting top items in by match score 
	private static int   recon  = 0; // how many top items

	public static void main (String[] a) {
		
		int ntop = 12;   // default count of top items to sacw

		if (a.length > 0)
			ntop = Integer.parseInt(a[0]);
		reco = new Rec[ntop+1];

		try {

			// get top items for each clusster

			Map map = new Map();
			int lim = map.limit();
			for (int i = 1; i < lim; i++) {

				if (!map.activeType(i))
					continue;
				GappedProfileList ls = new GappedProfileList(i);
 				int nit = ls.getCount();
				if (nit == 0) continue;
				Item[] it = ls.getList();
 
				Item top = it[0];
 
				Rec r = new Rec(top,i); // unpack its contents

				// sort top items

				int j;
				for (j = recon; j > 0; --j) {
					Rec or = reco[j-1];
					if (or.sg >= r.sg)
						break;
					reco[j] = or;
				}	
				reco[j] = r;
				if (recon < ntop)
					recon++;
			}

			// print sorted results

			for (int jk = 0; jk < recon; jk++) {
				Rec r = reco[jk];
				System.out.printf(" %2d::%-5d in %3d %6.2f",r.bn,r.in,r.pn,r.sg);
				System.out.println(sigma);
			}

		} catch (AWException e) {
			e.printStackTrace();
		}
		System.out.println("DONE");
	}

}
