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
// AW file AnalyzedToken.java : 12sep2022 CPM
// a token with an n-gram score

package object;

import aw.Parameter;
import stem.Token;
import gram.*;

public class AnalyzedToken extends Token {

	private Short[] gm;  // indices for token

	public AnalyzedToken (

		Token token,
		Characterizer indexing

	) {
		length = (short) token.length();
		array  = token.array;

		// get list of n-grams for token

		indexing.set(token);
		gm = indexing.list();
	}

	// compute basic score from token n-grams

	public int score (

		int   thr, // for hit count
		byte[] pw  // profile weights

	) {
		if (gm.length == 0) {
			length = 0;
			return 0;
		}
//		System.out.println("pw= " + pw[0] + " " + pw[1] + "...");
//		System.out.println("thr= " + thr + ", " + gm.length + " indices");
		int sum   = 0; // accumulated weight
		int hitn  = 0; // n-gram hits in stem
		int missn = 0; //        misses

		short gram = -1; // so to count each n-gram only once

		for (int i = 0; i < gm.length; i++) {
			if (gram == gm[i])
				continue;
			gram = gm[i];

			if (pw[gram] == 0)
				missn++;
			else {
				hitn++;
				sum += pw[gram];
			}
		}
//		System.out.println("sum= " + sum);

		// extra conditions for accepting weight

		if ((missn == 0 && hitn > 0) || (hitn >= thr))
			return sum;
		else
			return 0;
	}

	// get the n-gram analysis for this token

	public final Short[] indices ( ) { return gm; }

	public final int count ( ) { return gm.length; } // should not have to do this!

}
