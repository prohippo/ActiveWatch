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
// NameType.java : 24Apr99 CPM
// recognize acronyms as names

package aw.phrase;

import aw.Letter;

public class NameType {

	// recognize a token of 2 to 5 characters with the first two
	// capitalized preceded by a token without its second letter
	// capitalized or containing no vowels or all vowels

	public static boolean match (

		char[] a,
		int    n,
		SyntaxSpec x, // for current atom
		SyntaxSpec p  // for previous

	) {
		if ( n < 2 || n > 5)
			return false;

		if ((x.modifiers & Syntax.moreFeature) == 0) {
			if ((p.modifiers & Syntax.capitalFeature) == 0 ||
				Character.isLetter(a[1]))
				return false;
		}
		else if (p.type != Syntax.numberType &&
			(p.modifiers & Syntax.moreFeature) == 0) {
			x.type = Syntax.nameType;
			return true;
		}

		int k = 0;
		for (int i = 0; i < n; i++) {
			int b = Letter.toByte(a[i]);
			if (b >= 0 && Letter.vws[b])
				k++;
		}
		if (k == 0 || k == n) {
			x.type = Syntax.nameType;
			return true;
		}

		return false;
	}

}
