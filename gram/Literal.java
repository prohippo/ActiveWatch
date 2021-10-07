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
// AW file Literal.java : 06oct2021 CPM
// literal n-gram extraction

package gram;

import aw.*;
import java.io.*;

public class Literal extends LiteralBase {

	// load literal table from definition file

	public Literal (

		DataInputStream in

	) throws AWException {
		load(in);
	}

	// if token matches a leading literal
	// pattern; return the match index or -1

	public short forward (

		TokenBuffer tb

	) {
		short answer = -1;
		int ts; // start of comparison in token
		int to; // comparison index for token
		int lo; // literal index
		int i,k;
		char x;

		// check for uncovered span

		ts = tb.fwrd;
		if (ts >= tb.rvrs)
			return -1;

		// end of buffer check

		x = tb.buffer[ts++];
		if (!Character.isLetter(x))
			return -1;
		k = Letter.toByte(x);

		// scan leading literals

		for (i = ldsx[k]; i < ldsx[k+1]; i++) {

			for (to = ts, lo = litx[i];; to++, lo++) {
				x = lita[lo];

				// compare current literal to token

				if (tb.buffer[to] > x)
					break;

				else if (tb.buffer[to] < x) {

					if (Character.isLetter(x) || Character.isDigit(x))
						break;

					// on match, record extent of literal

					int n = to;
					if (n < ts)
						n = ts;
					tb.fwrd = n;
					answer = (short)(Gram.IBL + i);
					break;
				}
			}
		}

		return answer;
	}

	// if token matches a trailing literal
	// pattern; return the match index or -1

	public short reverse (

		TokenBuffer tb

	) {
		short answer=-1;
		int ts; // start of comparison in token
		int to; // comparison index for token
		int lo; // literal index
		int i,k;
		char x;

//		System.out.println(tb + " ****");

		// check for uncovered span

		ts = tb.rvrs;
		if (ts <= tb.fwrd)
			return -1;

		// check for end of buffer

		x = tb.buffer[--ts];
		if (x == TokenBuffer.NX)
			return -1;

		// scan trailing literals

		k = Letter.toByte(x);
		for (i = trsx[k], --ts; i < trsx[k+1]; i++) {

			for (to = ts, lo = litx[i];; --to, lo++) {
				x = lita[lo];
//				System.out.println("x= " + x + ", tb[" + to + "]= " + tb.buffer[to]);

				// compare current literal to token

				if (tb.buffer[to] > x)
					break;
 
				else if (tb.buffer[to] < x) {
					if (Character.isLetter(x))
						break;

					// on match, record start of literal

					tb.rvrs = to + 1;
					answer = (short)(Gram.IBL + i);
//					System.out.println("answer=" + answer);
//					System.out.println(tb);
					break;
				}
			}
		}
		return answer;
	}

}
