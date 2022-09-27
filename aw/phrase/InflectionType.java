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
// InflectionType.java : 06jun2022 CPM
// use inflectional ending to infer syntax type
// (does NOT put atom into root form!)

package aw.phrase;

import aw.Letter;
import stem.Token;
import stem.Inflex;

public class InflectionType {

	private static Token o = new Token();

	public static boolean match (
	
		char[] a,
		SyntaxSpec x,
		SyntaxSpec prev
		
	) {
		int ln = a.length;
		String as = new String(a);
//		System.out.println("inflex: " + as;
		if (ln == 0)
			return false;
			
		char end = a[ln-1];
		if (!Character.isLetter(end))
			return false;
		end = Character.toLowerCase(end);

		o.set(a,ln); // must convert chars to token for stemming
		
		Inflex.inflex(o);

//		System.out.println("deinflection= " + o);
		
		if (as.equals(o.toString()))
			return false;

		x.modifiers |= Syntax.inflectedFeature;

		if (end == 's') {

			char[] ax = new char[o.length()];
//			System.out.println("length= " + ax.length);
			for (int i = 0; i < ax.length; i++) {
				ax[i] = Letter.toChar(o.array[i]);
			}
//			System.out.println("look up deinflected= " + (new String(ax)));
			if (WordType.match(ax,x) ||
				EndingType.match(ax,x))
				;
			else if (prev.type == Syntax.adverbType ||
				prev.type == Syntax.auxiliaryType)
				x.type = Syntax.verbType;
			else if (prev.type == Syntax.determinerType ||
				prev.type == Syntax.adjectiveType   ||
				prev.type == Syntax.prepositionType ||
				prev.type == Syntax.unknownType)
				x.type = Syntax.nounType;
		}
		else {
			x.type = Syntax.verbType;
		}
		x.modifiers |= Syntax.breakFeature;
//		System.out.println("typing= " + x);
		return true;
	}
	
}
