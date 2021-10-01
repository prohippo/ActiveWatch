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
// AW file TableCode.java : 30aug2021 CPM
// for building external AW literal index tables

package aw.table;

import aw.Letter;
import aw.AWException;

public class TableCode {

	// for consistency with token sorting, digits are mapped to lower case letters,
	// letters are mapped to upper case, apostrophe and period are mapped high, and
	// everything else is dropped

	// convert string for proper sorting

	public static String forSorting (

		String r

	) {
		StringBuffer s = new StringBuffer(r.length());

		for (int i = 0; i < r.length(); i++) {
			char x = encode(r.charAt(i));
			if (x > 0)
				s.append(x);
		}
		return s.toString();
	}

	// encode characters for proper sorting of literals

        public static char encode (
                char c
        ) {
		if (Character.isDigit(c))
			c = (char)(c - '0' + 'a');
		else if (Character.isLetter(c))
			c = Character.toUpperCase(c);
		else if (c == Letter.APO)
			c = '{';
		else if (c == Letter.DOT)
			c = '}';
		else
			c = '\u0000';
		return c;
	}

	// map back encoding to store literals in table

	public static int forStoring (

		String r,
		char[] b,
		int   nb

	) throws AWException {
		for (int i = 0; i < r.length(); i++)
			b[nb++] = recode(r.charAt(i));
		return nb;
	}

	// map characters back after sorting

	public static char recode (
		char c
	) throws AWException {
		if (Character.isLowerCase(c))
			c -= 'a' - '0';
		else if (Character.isUpperCase(c))
			c = Character.toLowerCase(c);
		else if (c == '{')
			c = Letter.APO;
		else if (c == '}')
			c = Letter.DOT;
		return c;
	}

	// recode() is NOT inverse of encode()!!

}
