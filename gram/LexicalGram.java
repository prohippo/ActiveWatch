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
// AW file LexicalGram.java : 05jan2022 CPM
// basic n-gram extraction

package gram;

import aw.Letter;

public class LexicalGram {

	public static int MXn = 5; // max n for built-in lexical n-grams

//	limit n in n-grams

	public static void set ( int n ) {
		if (n < 5 && n > 1)
			MXn = n;
	}

//	get next n-gram index from token buffer

	public static short get (

		TokenBuffer tb,
		GramMap     gm

	) {
		char c,d;     // first chars
		int   to;     // token index
		int    n;     // the n in the next n-gram to look for
		short  g = 0; // next n-gram index to return

		to = tb.reposition(0);
//		System.out.println("resume with to= " + to);
//		System.out.println(tb);

		while (to <= tb.fwrd && to < tb.rvrs) {

			// check that leading char is alphabetic

			c = tb.buffer[to++];
//			System.out.println("lexical to=" + to + ", c= [" + c + "]");
//			System.out.println(tb);
			if (Character.isLetter(c)) {

				// look for alphabetic 5-gram at current buffer position

				if  (MXn >= 5 && tb.end - to >= 4 && tb.fwrd - to < 4) {
					String gs = new String(tb.buffer,to-1,5);
					int    gg = gm.encode5g(gs);
					if (gg >= 0) {
						tb.fwrd = to + 4;
						g = (short)(gg + Gram.IB5);
						to = tb.reposition(5);
						break;
					}
				}

				// look for alphabetic 4-gram

//				System.out.println("4-grams");
//				System.out.println(tb);
				if  (MXn >= 4 && tb.end - to >= 3 && tb.fwrd - to < 3) {
					String gs = new String(tb.buffer,to-1,4);
					int    gg = gm.encode4g(gs);
//					System.out.println(gs + "=" + gg);
					if (gg >= 0) {
						tb.fwrd = to + 3;
						g = (short)(gg + Gram.IB4);
						to = tb.reposition(4);
						break;
					}
				}

				// look for alphabetic 3-gram

//				System.out.println(tb);
 
				if  (MXn >= 3 && tb.end - to >= 2 && tb.fwrd - to < 2) {
//					System.out.println("3-grams, to= " + to);
 
					// seed for alphabetic 3-gram?
					int jcb = find(c,tb.buffer[to]);
					if (jcb >= 0) {
						d = tb.buffer[to+1];
						if (Character.isLetter(d)) {
							int k = Letter.toByte(d);
							g = (short)(Gram.IB3 + jcb*Letter.NA + k);
							tb.fwrd = to + 2;
							to = tb.reposition(3);
//							System.out.println(tb);
							break;
						}
					}
				}
			}

			// try for alphanumeric 2-gram

//			System.out.println("to=" + to + ", fwrd=" + tb.fwrd);
//			System.out.println(tb);

			if (to < tb.end && to >= tb.fwrd) {
//				System.out.println("2-grams");

				int kc = Letter.toByte(c);
				int kd = Letter.toByte(tb.buffer[to++]);
				g = (short)(Gram.IB2 + kc*Letter.NAN + kd);
				tb.fwrd = to;
//				System.out.println("fwrd= " + tb.fwrd);
//				System.out.print("before to= " + to);
				to = tb.reposition(2);
//				System.out.print(" after to= " + to);
//				System.out.println("");
//				System.out.println(tb);
				break;
			}
		}

//		System.out.println("return " + g + ", posn=" + tb.position());
		return g;

	}

	// search for an alphabetic 2-gram in an external
	// bit table given 2 letters and returns the count
	// of bits preceding if found, -1 otherwise
	//
	// an n-gram is in a table when its bit is on.
	// (there are NA*NA alphabetic 2-grams altogether)
	//
	// the table consists of two parts:
	// (1) a bit vector representing table entries,
	//     referred to as t3lx
	// (2) a count of the bits turned on in a table
	//     preceding each character position in the
	//     bit-vector array, referred as t3lb
 
	private static final byte bi[] = {1,2,4,8,16,32,64,-128};
	private static final byte nd[] = {0,1,3,7,15,31,63, 127};
 
	private static final byte nb[] = {
		0,1,1,2,1,2,2,3,1,2,2,3,2,3,3,4,
		1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,
		1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,
		2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
		1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,
		2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
		2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
		3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7
	};

	public static int find (

		char ax, // letters as Unicode chars
		char bx  //

	) {
 
		int m,n;
		int seed;

		byte a = Letter.toByte(ax);
		byte b = Letter.toByte(bx);

		if (b >= Letter.NA || b < 0)
			return -1; 
		seed = (short)a*Letter.NA + b;
		n = seed>>3;
		m = seed&07;

		return ((Gram.t3lx[n]&bi[m]) != 0) ? Gram.t3lb[n] + (nb[Gram.t3lx[n]&nd[m]]) : -1;
	}

}
