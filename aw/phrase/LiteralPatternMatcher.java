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
// LiteralPatternMatcher.java : 25jan2022 CPM
// literal patterns with matching method

package aw.phrase;

import aw.CharArray;
import java.io.*;

public class LiteralPatternMatcher extends LiteralPattern {

	private static final String vowel = "aeiou";
	
	// initialize patterns from file
	
	public LiteralPatternMatcher (
		DataInputStream is
	) throws IOException {
		super();
		load(is);
	}
	
	// compare string against a set of patterns
	
	public boolean match (
		CharArray  s,
		int        n,
		SyntaxSpec x
	) {
		boolean matchFailed;
		int ln = s.length();
		
		int start = index[n];
		int end   = index[n+1];

		while (start < end) {

			int j  = 0;             // string  index
			int k  = offset[start]; // pattern index
			int kl = offset[start+1] - 1;
			
			matchFailed = false;

			int ps = -1; // for start of saved match
			int pe = -1; // for end
			int ll = -1;

			// comparison loop
			
			char cp = 0;

			while (k < kl && !matchFailed) {
			
				lcp = cp;
				cp  = pattern[k];
				char cs = s.charAt(j);

				switch (cp) {
				
case LiteralPattern.ESCP: // match an escaped special character

					if (cs == pattern[++k])
						j++;
					else
						matchFailed = true;
					break;
					
case LiteralPattern.UPPR: // match a single uppercase alphabetic character

					if (Character.isUpperCase(cs))
						j++;
					else
						matchFailed = true;
					break;
					
case LiteralPattern.CNSN: // match a single consonant alphabetic character

					if (Character.isLetterOrDigit(cs) && vowel.indexOf(cs) < 0)
						j++;
					else
						matchFailed = true;
					break;
					
case LiteralPattern.ALPH: // match a single alphabetic character					

					if (Character.isLetter(cs))
						j++;
					else
						matchFailed = true;
					break;
					
case LiteralPattern.NUMR: // match a single numeric character
				
					if (Character.isDigit(cs))
						j++;
					else
						matchFailed = true;
					break;
					
case LiteralPattern.WILD: // match any alphanumeric character

					if (Character.isLetterOrDigit(cs))
						j++;
					else
						matchFailed = true;
					break;
					
case LiteralPattern.RSTR: // match an arbitrary number of non-space characters

					n = skip(s,j,k+1,kl);
					if (n < 0)
						matchFailed = true;
					else
						j += n;
					break;
					
case LiteralPattern.SPCS: // a single pattern space will match multiple spaces in text

					for (n = 0; s.charAt(j) == ' '; n++)
						j++;
					while (s.charAt(j) == LiteralPattern.DEL)
						j++;
					if (s.charAt(j) == '\r') { // for DOS, MAC, and Unix!
						j++; n++;
					}
					if (s.charAt(j) == '\n') {
						j++; n++;
					}
					while (s.charAt(j) == LiteralPattern.DEL)
						j++;

					if (n == 0)
						matchFailed = true;
					break;
					
case LiteralPattern.LBKT: // start of saved match

					ps = j;
					pe = -1;
					break;
					
case LiteralPattern.RBKT: // end   of saved match

					if (ps >= 0)
						pe = j;
					break;
					
case LiteralPattern.INPT: // look for another instance of saved match

					if (pe < 0)
						matchFailed = true;
					else {
						int i = 0;
						int m = pe - ps;
						for (; i < m; i++)
							if (s.charAt(ps+i) != s.charAt(j+i))
								break;
						if (i == m)
							j += m;
						else
							matchFailed = true;
					}
					break;
					
case LiteralPattern.LIMT: // set parsing limit here, but continue matching

					ll = j;
					break;
					
default:			// straight comparison with special case rule
					
					if (cs == cp)
						j++;
					else if (Character.toLowerCase(cs) == cp)
						j++;
					else
						matchFailed = true;
				}
				k++;
			}

			// if matches completely up to a nonalphanumeric character,
			// return with a match count
		
			if (   !matchFailed
				&& !Character.isLetterOrDigit(s.charAt(j))
				&& s.charAt(j) != '&'
				&& s.charAt(j) != '\'') {
				byte v = (byte)((x.modifiers) & (Syntax.capitalFeature | Syntax.moreFeature));
				x.copy(syntax[start]); // complete override
				x.modifiers |= v;      // but restore these bits
				if (ll >= 0)
					j = ll;
				s.skip(j);
				return true;
			}
			start++;
		}
	
		return false;
	}

	// scan alphanumeric sequence in pattern
	
	private static final char ALPH = LiteralPattern.ALPH;
	private static final char NUMR = LiteralPattern.NUMR;
	
	private char lcp;
	
	private int skip (
		CharArray s,
		int j,
		int k,
		int kl
	 ) {
	 	int jk = j;
		int jl = s.length();
	 	switch (lcp) {
case ALPH:
			for (; jk < jl; jk++)
				if (!Character.isLetter(s.charAt(jk)))
					break;
			break;
case NUMR:
			for (; jk < jl; jk++)
				if (!Character.isDigit(s.charAt(jk)))
					break;
			break;
default:
		 	char trm = (k < kl - 1) ? pattern[k+1] : 0;
		 	if (trm == LiteralPattern.LBKT || trm == LiteralPattern.RBKT)
		 		trm  = (k < kl - 2) ? pattern[k+2] : 0;
		 	
			for (; jk < jl; jk++)
				if (s.charAt(jk) == trm || !Character.isLetterOrDigit(s.charAt(jk)))
					break;
	 	}
		return jk - j;
	}

}
