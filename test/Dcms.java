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
// Dcms.java : 04oct2023 CPM
// show all cluster memberships for given item

package test;

import aw.*;
import object.*;
import java.io.*;

public class Dcms {

	static String fm = "%3d) in %3d @%3d : %5.2f\u03C3\n";

	static public void main ( String[] a ) {
		Map m = new Map();    // map of current profiles
		GappedProfileList ls; // profile records

		if (a.length == 0) {
			System.out.println("usage: X segmentID");
			System.exit(0);
		}
		String as = a[0];
		int nc = as.indexOf(':');
		if (nc < 0 || (nc + 1) == as.length() || as.charAt(nc+1) != ':') {
			System.err.print("invalid text segmentID");
			System.exit(1);
		}
		int bn = Integer.valueOf(as.substring(0,nc));
		int xn = Integer.valueOf(as.substring(nc+2));

		int tot = 0;  // how cluster assignments

		System.out.println("showing profile match list entries for " + a[0]);
		for (int i = 1; i <= Map.MXSP; i++) {
			if (!m.defined(i))
				continue;
			try {
				ls = new GappedProfileList(i);
				Item[] it = ls.getList();
				int lm = it.length;
				for (int j = 0; j < lm; j++) {
					if (it[j].bn == bn && it[j].xn == xn) {
						tot++;
						double ss = it[j].score();
						System.out.printf(fm,tot,i,j,ss);
						break;
					}
				}
			} catch (AWException e) {
				e.printStackTrace();
				System.exit(1);;
			}
		}
		System.out.print("cluster matches = ");
		System.out.println(tot);
	}
}
