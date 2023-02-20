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
// Dqbe.java : 17feb2023 CPM
// create profile from given items

package test;

import aw.*;
import object.ClusterProfile;
import object.ProfileToUse;
import object.ProfileWithTuning;
import object.IndexVectorCentroid;
import object.IndexVector;
import object.SimpleIndexVector;
import java.io.*;

public class Dqbe {

	private static final int N = 24;        // maximum number of item IDs
	private static final int B =  2;        // how many mandatory args other than item IDs
	private static int[]  vw = new int[N];  // vector weights (will be all = 1)
	private static Item[] it = new Item[N]; // item list

	public static void main ( String[] a ) {

		System.out.println("create profile from a set of items");
		if (a.length == 0)
			return;

		boolean reweight = false;

		int ns = 0;

		if (a[0].charAt(0) == '-') {
			reweight = true;
			ns++;
		}

		int n = a.length;
		if (n < B + 1 - ns || a[0].length() > 2) {
			System.out.println("usage: do DQBE [-r] minCount proLength b::n [...]");
			return;
		}

		int mltp = Integer.parseInt(a[ns+0]); // minimum n-gram count
		int mlng = Integer.parseInt(a[ns+1]); // minimum profile length

		n -= B + ns;
		if (n > N)
			n = N;
		System.out.println("processing " + n + " items");

		try {

			for (int i = 0; i < n; i++) {
				int ip = i + B + ns;
//				System.out.println("ip= " + ip + " + " + a.length);
				String as = a[i+B+ns];
//				System.out.println(i + ") " + as);
				Item itm = Reference.to(as);
				it[i] = itm;
				vw[i] = 1;
			}

			ClusterProfile cp = new ClusterProfile(n,it,vw,mltp,mlng);
			System.out.println(cp.length + " indices");
			ProfileToUse up = new ProfileToUse(cp.profile);

			if (reweight) {
				IndexVector[] vs = new IndexVector[n];
				for (int i = 0; i <  n; i++) {
					int bn = it[i].batch();
					int xn = it[i].index();
					vs[i] = new IndexVector(bn,xn);
				}

				SimpleIndexVector sv = IndexVectorCentroid.from(vs,vw,n);
				ProfileWithTuning tp = new ProfileWithTuning(up);
				tp.reweightFor(sv.expand(),sv.storedSum());
				tp.assignTo(up);
			}

			up.save("profile");

		} catch (AWException e) {
			e.printStackTrace();
		}

	}

}

