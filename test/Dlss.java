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
// Dlss.java : 12Aug98 CPM
// dump lists

package test;

import aw.*;
import object.*;
import java.io.*;

public class Dlss {
	
	static public void main ( String[] a ) {
		Map m = new Map();
		ProfileList ls;
		Attribute   at;
		
		double  th = (a.length > 0) ? Float.valueOf(a[0]).floatValue() : 0;
		boolean sh = (a.length > 1);
		
		ItemTally tally = new ItemTally();
		int total = 0;
		System.out.println("dumping cluster lists");
		for (int i = 1; i <= Map.MXSP; i++) {
			if (!m.defined(i))
				continue;
			try {
				ls = new ProfileList(i);
				at = new Attribute(i);
				int n = ls.getCount();
				System.out.println("** cluster " + i + ": " + n + " items");
				if (!sh)
					System.out.println(new LinedString(new String(at.kys,0),64));
				Item[] it = ls.getList();
				for (int j = 0; j < n; j++) {
					double ss = it[j].score();
					if (ss < th)
						continue;
					if (!sh)
						print(j+1,it[j].bn,it[j].xn,ss);
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
		if (sh || th > 0)
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
					print(k++,bn,sn+n,0);
			}
		}
	}
	
	private static void print (
		int  k,
		int bn,
		int xn,
		double score
	) {
		System.out.print(Format.it(k,3) + ") ");
		Subsegment ss;
		try {
			ss = new Subsegment(bn,xn);
		} catch (IOException e) {
			System.err.println("cannot get subsegment: " + e);
				return;
		}
		TextItem ti = new TextItem(bn,ss.it);
		String hdr = ti.getFullText();
		System.out.print(Format.it(TextItem.getLine(hdr),40));
		System.out.print("| " + bn + ":" + ss.it);
		System.out.print(" = " + Format.it(score,6,2));
		System.out.println("  (::" + xn + " =" + ss.sn + ")");
	}
	
}