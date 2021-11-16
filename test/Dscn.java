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
// Dscn.java : 13nov2021 CPM
// look for n-gram occurrence in index vectors of batch

package test;

import aw.*;
import gram.*;
import object.IndexVector;

class ScanIndexVector extends IndexVector {

	static public int gno = 0;
	static public int gin = 0;
	static public int tno = 0;

	public ScanIndexVector (
		int b,
		int m
	) throws AWException {
		super(b,m);
	}

	public boolean scan (
		int g
	) throws AWException {
		boolean found = false;		
		while (next()) {
			tno += count;
			if (g == gram) {
				gno += count;
				gin++;
				found = true;
			}
		}
		return found;
	}

}

public class Dscn {

	public static void main ( String[] a ) {

		int g = (a.length > 0) ? Integer.parseInt(a[0]) :  0; // n-gram index number
		int b = (a.length > 1) ? Integer.parseInt(a[1]) :  0; // batch number
		int n = (a.length > 2) ? Integer.parseInt(a[2]) : 12; // limit on reporting

		try {			
			GramStart gs = new GramStart();
			LiteralDecoding ld = new LiteralDecoding(gs.table);
			GramDecode d = new GramDecode(gs.map,ld);

			ScanIndexVector sv;

			System.out.print("gram index " + g);
			if (g > 0)
				System.out.println(" = " + d.toString(g));
			else {
				System.out.println("*** bad index number");
				System.exit(1);
			}

			int k = 0;

			for (int m = 0;; m++) {
				try {
					sv = new ScanIndexVector(b,m);
				} catch (AWException e) {
					break;
				}
				if (sv.scan(g)) {
					System.out.println(b + "::" + m);
					k++;
					if (k == n) break;
				}
			}

			System.out.print(ScanIndexVector.gno + " occurrences in ");
			System.out.print(ScanIndexVector.gin + " items out of ");
			System.out.println(ScanIndexVector.tno + " total occurrences");

			double p = ((double)ScanIndexVector.gno)/ScanIndexVector.tno;
			System.out.println("probability= " + Format.it(p,10,8));
		} catch (AWException e) {
			System.out.println(e);
		}
	}

}
