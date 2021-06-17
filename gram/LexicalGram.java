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
// AW file LexicalGram.java : 09Dec97 CPM
// basic n-gram extraction

package gram;

import aw.Letter;

public class LexicalGram {

	public static short get (
	
		TokenBuffer tb
		
	) {
		byte a,c;     // encoded first chars
		int  jab;     // seed index of letter pair
		int   to;     // token index
		short  g = 0; // next n-gram index to return

		to = tb.fwrd;
		for (int lm = tb.end - tb.fwrd; lm > 1; --lm) {
			if (to >= tb.rvrs)
				break;
 
			// check that (coded) leading
			// character is alphabetic

			a = tb.buffer[to++];
			if (a < Letter.NA && a >= 0) {
 
				if (lm >= 3 && tb.nor <= 3) {
 
					// seed for alphabetic 3-gram?
					jab = find(a,tb.buffer[to]);
					if (jab >= 0) {
						c = tb.buffer[to+1];
						if (c < Letter.NA) {
							g = (short) (Gram.IB3 + jab*Letter.NA + c);
							tb.fwrd = to;
							tb.nor = 3;
							break;
						}
					}
				}
			}

			if (tb.nor != 2)
				--tb.nor;
				
			else {
 
				// alphanumeric 2-gram as last resort
				g = (short) (Gram.IB2 + a*Letter.NAN + tb.buffer[to]);
				tb.fwrd = to;
				break;
			}
		}
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
	
		byte a, // encoded letters
		byte b
		
	) {
 
		int m,n;
		int seed;

		if (b >= Letter.NA || b < 0)
			return -1; 
		seed = (short)a*Letter.NA + b;
		n = seed>>3;
		m = seed&07;

		return ((Gram.t3lx[n]&bi[m]) != 0) ? Gram.t3lb[n] + (nb[Gram.t3lx[n]&nd[m]]) : -1;
	}

}