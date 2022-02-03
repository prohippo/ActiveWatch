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
// NumberType.java : 23Apr99 CPM
// test for number formats

package aw.phrase;

public class NumberType {

	private static final String emb = ",./-:";
	
	// recognize numerical token
	
	public static boolean match (
	
		char[] a,
		int   ln,
		SyntaxSpec x
		
	) {
		int nd = 0, no = 0;

		for (int i = 0; i < ln; i++) {
			char c = a[i];
			if (Character.isLetter(c))
				return false;
			if (!Character.isDigit(c)) {
				if (emb.indexOf(c) < 0)
					return false;
				else if (c == '.')
					nd++;
				else
					no++;
			}
		}
		
		if (no + nd == ln)
			return false;
		x.type = Syntax.numberType;
		if (no > 0 || nd > 1)
			x.modifiers |= Syntax.moreFeature;
		return true;
	}
	
}