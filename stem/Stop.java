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
// AW file Stop.java : 08nov2022 CPM
// stopword support

package stem;

import aw.*;
import java.io.*;

public class Stop extends StopBase {

	// initialize
	
	protected Stop (
	
	) {
	
		super();

	}
	
	// load stop table

	public Stop (
	
		DataInputStream in
		
	) throws AWException {
	
		this();
		load(in);
		
	}

	// returns a non-zero index number if
	// token is in stop table; otherwise 0

	public int stop (
	
		Token token // word, number, or name
		
	) {
		int i,n;
		int leng;     // word length
		int offset;   // character separation in table blocks
		int lo,hi,md; // binary search indices
		int p;        // table index

//		System.out.println("= " + token);

		// obtain initial bounds of search for words
		// of length leng in table and get separation
		// between characters of individual words

		if ((leng = token.length()) > MXSW)
			leng = MXSW;
		if (leng == 0)
			return index[MXSW+1];

//		System.out.println("length to match= " + leng);

		if ((n = index[leng+1] - index[leng]) == 0)
			return 0;

		// separation between chars of each stop entry in this block

		offset = n/leng;

		//// for debugging
		//
//		int k = index[leng];
//		System.out.println(n + " chars to check, starting at " + k);
//		for (int j = 0; j < n; j++, k++) {
//			if (j%offset == 0) System.out.println();
//			System.out.print(Letter.toChar(table[k]));
//		}
//		System.out.println();
		//
		////

		// binary search of table

		lo = index[leng];
		hi = lo + offset - 1;

		while (lo <= hi) {

//			System.out.println("lo= " + lo + ", hi= " + hi);

			// get next probe
			p = md = lo + ((hi - lo) >> 1);
			for (i = 0; i < leng; i++) {

//				System.out.print  (i + ") check " + Letter.toChar(token.array[i]) + ":");
//				System.out.println(Letter.toChar(table[p]) + " (" + table[p] + ") @ " + p);
				if (token.array[i] != table[p])
					break;
				else
					p += offset;
			}

			// check for match up to maximum comparison
			if (i == leng)
				return md - index[leng] + count[leng];
			else if (token.array[i] < table[p])
				hi = md - 1;
			else
				lo = md + 1;
		}
//		System.out.println("** no match");
		return 0;
	}

}
