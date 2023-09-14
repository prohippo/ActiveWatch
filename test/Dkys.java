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
// Dkys.java : 04sep2023 CPM
// get descriptive key counts over all profiles

package test;

import aw.*;
import java.util.Hashtable;
import java.util.Enumeration;

public class Dkys {

	private static final int K = 8000; // nominal Hashtable size

	private static class Rec {
		String key;
		int    count;
		Rec () {}
		Rec (String str,int cnt) {
			key   = str;
			count = cnt;
		}
	}

	private static Hashtable<String,Integer> ht = new Hashtable<String,Integer>();

	private static Rec[] reco = new Rec[K]; // for aorting keys in descending order of frequency
	private static int recon  = 0;          // how many sorted keys

	public static void main (String[] a) {
		int min = 2;
		if (a.length > 0)
			min =Integer.parseInt(a[0]);

		try {

			// count up descriptive keys for all active profiles

			Map map = new Map();
			int lim = map.limit();
			for (int i = 1; i < lim; i++) {

				if (!map.activeType(i))
					continue;
				Attribute attr = new Attribute(i);
				String keys = attr.keys();
				String[] ka = keys.split(" ",0);
				int kl = ka.length;
				for (int k = 0; k < kl; k++) {
					String ks = ka[k];
					if (ks.charAt(0) == '|')
						ks = ks.substring(1);
					if (!ht.containsKey(ks)) 
						ht.put(ks,1);
					else {
						int count = ht.get(ks).intValue();
						ht.put(ks,count+1);
					}
				}
			}

			// sort keys and values from hash table

			Enumeration<String> ken = ht.keys();
			while (ken.hasMoreElements()) {
				String ks = ken.nextElement();
				int ct = ht.get(ks).intValue();
				if (ct < min) continue;
				Rec r = new Rec(ks,ct);

				int j;
				for (j = recon; j > 0; --j) {
					Rec or = reco[j-1];
					if (or.count >= r.count)
						break;
					reco[j] = or;
				}	
				reco[j] = r;
				if (j < K)
					recon++;
			}

			// print results

			for (int jk = 0; jk < recon; jk++) {
				Rec r = reco[jk];
				System.out.printf(" %-10.10s %3d\n",r.key,r.count);
			}

		} catch (AWException e) {
			e.printStackTrace();
		}
		System.out.println("DONE");
	}

}
