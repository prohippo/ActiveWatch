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
// Dlss.java : 04aug2023 CPM
// dump AW profile match lists with selection of information

package test;

import aw.*;
import object.*;
import java.io.*;

public class Dlss {

	private final static String sigma = "\u03C3";

	static private int N = 10;

	static public void main ( String[] a ) {
		Map m = new Map();  // map of current profiles
		ProfileList ls;     // profile records
		Attribute   at;     // associated profile attributes

		char   mod = (a.length > 0) ? Character.toUpperCase(a[0].charAt(0)) : 'N';
		double thr = (a.length > 1) ? Float.valueOf(a[1]).floatValue() : 3.0;

		// A = full dump
		// K = keys only
		// N = no full text
		// S = short listing of items

		ItemTally tally = new ItemTally();
		int total = 0;

		System.out.println("dumping profile match lists");
		for (int i = 1; i <= Map.MXSP; i++) {
			if (!m.defined(i))
				continue;
			try {
				ls = new ProfileList(i);
				at = new Attribute(i);
				int n = ls.getCount();
				System.out.println("** profile " + i + ": " + n + " items");
				System.out.println(new LinedString(new String(at.kys),64));

				if (mod == 'K') continue;

				Item[] it = ls.getList();
				if (mod == 'S' && n > N)
					n = N;
				for (int j = 0; j < n; j++) {
					double ss = it[j].score();
					if (ss < thr) continue;
					print(j+1,it[j].bn,it[j].xn,ss,(mod == 'A'));
					tally.mark(it[j]);
					total++;
				}
			} catch (AWException e) {
				e.printStackTrace();
				return;
			}
		}

		System.out.println();
		int un = tally.uniqueCount();
		double r = (100.*total)/un;
		System.out.println(un + " unique items listed");
		System.out.println("redundancy = " + Format.it(r-100,5,1) + " percent");
		if (mod != 'A' || thr > 0)
			return;

		System.out.println();
		System.out.println("dumping residuals");
		Residual rs;
		try {
			rs = new Residual();
		} catch (AWException e) {
			System.err.println("cannot get them");
			return;
		}
		int k = 1;
		for (int i = 0; i < rs.nmrun; i++) {
			int bn = rs.r[i].bbn;
			int sn = rs.r[i].ssn;
			int nn = rs.r[i].nss;
			for (int n = 0; n < nn; n++) {
				Subsegment ss;
				try {
					ss = new Subsegment(bn,sn+n);
				} catch (IOException e) {
					System.err.println(e);
					return;
				}
				if (ss.sn == 1)
					print(k++,bn,sn+n,0,(mod == 'A'));
			}
		}
	}

	private static void print (
		int  k,
		int bn,
		int xn,
		double score,
		boolean showText
	) {
		System.out.print(Format.it(k,3) + ") ");
		Subsegment ss;
		try {
			ss = new Subsegment(bn,xn);
		} catch (IOException e) {
			System.err.println("cannot get subsegments: " + e);
			return;
		}
		System.out.print("| " + bn + "::" + ss.it);
		System.out.print(" = " + Format.it(score,6,2) + sigma);
		System.out.println("  (::" + xn + " =" + ss.sn + ")");
		if (showText) {
			TextItem ti = new TextItem(bn,ss.it);
			String t = ti.getFullText();
			System.out.println("---- ---- ---- ----");
			System.out.println(Format.it(TextItem.getLine(t),40));
			System.out.println("==== ==== ==== ====");
		}
	}

}
