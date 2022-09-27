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
// PhraseElement.java : 26sep2022 CPM
// output for PhraseAnalysis

package aw.phrase;

import aw.phrase.Syntax;

public class PhraseElement {

	public static final int WL = 128;

	public int offset;        // offset in source text segment
	public int length;        // number of chars in phrase element
	public char[] word;       // copied array of chars for element
	public SyntaxSpec syntax; // syntactic description of element
	
	public PhraseElement (    // make empty class instance
	
	) {
		syntax = new SyntaxSpec();
		word = new char[WL+1];
	}
	
	public final int  type ( ) { return syntax.type; }
	public final byte modifiers ( ) { return syntax.modifiers; }
	public final byte semantics ( ) { return syntax.semantics; }
	
	public final String word ( ) {
		int k = length - 1;
		for (; k >= 0; --k) {
			if (Character.isLetterOrDigit(word[k]))
				break;
		}
		length = k + 1;
		return new String(word,0,length);
	}

	public final void clear ( ) {
		offset = length = 0;
		syntax.clear();
		word = new char[WL+1];
	}

}
