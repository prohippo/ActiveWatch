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
// LexicalAtom.java : 23feb2022 CPM
// unit of parsing for phrase analysis with associated data type

package aw.phrase;

import aw.phrase.Syntax;
import java.util.Arrays;

public class LexicalAtom {

	public static final int L = 128;

	public int        skip; // offset from current stream location
	public int        span; // actual width within stream
	public int      length; // atom length
	public char[]     atom; // atom as normalized string
	public SyntaxSpec spec; // syntax for atom
	public boolean   stopp; // stop phrase
	public boolean   stops; // stop sentence

	// initialize

	public LexicalAtom (

	) {
		spec = new SyntaxSpec();
		atom = new char[L+1];
	}

	// for debugging

	public final byte modifiers ( ) { return spec.modifiers; }

	// for debugging

	public final byte type ( ) { return spec.type; }

	// for printing

	public String toString ( ) {
		String sa = new String(Arrays.copyOfRange(atom,0,length));
		return sa + "[[" + spec.type + "]] skip= " + skip + ", span= " + span;
	}

	// get syntax information by lookup or inference

	public void getSyntax (

		SyntaxSpec prev

	) {

		// check for a number

		if (NumberType.match(atom,span,spec))
			return;

		// look up the word or its ending for its syntactic type

		if (WordType.match(atom,span,spec)   ||
			EndingType.match(atom,span,spec) ||
			NameType.match(atom,span,spec,prev))
			return;

		// if this fails, remove any inflectional ending and try again

		if (InflectionType.match(atom,span,spec))
			return;

		// if inflected, try lookup again

		if ((spec.modifiers & Syntax.inflectedFeature) == 0)
			;
		else if (WordType.match(atom,span,spec) ||
			EndingType.match(atom,span,spec))
 			return;
		else if (prev.type == Syntax.adverbType ||
			prev.type == Syntax.auxiliaryType) {
			spec.type = Syntax.verbType;
			spec.modifiers |= Syntax.breakFeature;
		}
		else if (prev.type == Syntax.determinerType ||
			prev.type == Syntax.adjectiveType   ||
			prev.type == Syntax.prepositionType ||
			prev.type == Syntax.unknownType)
			spec.type = Syntax.nounType;

	}

}
