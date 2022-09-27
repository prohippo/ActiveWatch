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
// AW File Extracter.java : 19sep2022 CPM
// extract lexical segments from a segment of English text

package stem;

import aw.Letter;

public class Extracter {

	private static final String brackets = "(){}[]<>";
	private static final String punctuations = ":;+=|_?";

	protected String s; // source string
	public    int   os; // saved offset in source

	public Extracter (
	
		String text // source of text for extraction
		
	) {
		s = text;
		os = 0;
	}

	// obtain next token, if any
		
	public String get (
	
	) {
		int nn=0;
		int k,n;

		// find next alphanumeric character in text

		int length = s.length();
		for (k = 0; k < length; k++)
			if (Character.isLetterOrDigit(s.charAt(k)))
				break;

		s = s.substring(k);
		length -= k;
		os += k;

		for (k = n = 0; k < length && n < Token.MXW; k++) {

			// accumulate alphanumeric characters
			if (Character.isLetterOrDigit(s.charAt(k))) {
				n++; nn++;
			}

			// break on any space character
			else if (Character.isWhitespace(s.charAt(k)))
				break;

			// characters to ignore if followed or preceded by a digit
			else if (s.charAt(k) == '-' || s.charAt(k) == '/') {
				if (nn > 1 && k < length - 1)
					if (!Character.isDigit(s.charAt(k+1)))
						if (!Character.isDigit(s.charAt(k-1)))
							break;
				nn = 0;
			}

			// apostrophe in possessives and otherwise
			else if (s.charAt(k) == Letter.APO) {

				if (k < length - 1) {
					char ch = s.charAt(k+1);
					if (!Character.isLetterOrDigit(ch))
						break;
						
					// drop -'s only when not followed by alphanumeric
					if (Character.toLowerCase(ch) == 's')
						if (k < length - 2)
							if (Character.isLetterOrDigit(s.charAt(k+2)))
								k += 2;
							else
								break;
						else
							break;
				}	
				n++;
				nn = 0;
			}

			// brackets break unabiguously				
			else if (brackets.indexOf(s.charAt(k)) >= 0)
				break;

			// certain punctuation break unabiguously				
			else if (punctuations.indexOf(s.charAt(k)) >= 0)
				break;

			// skip over single embedded non-alphanumeric chars
			else if (k < length - 1 && Character.isLetterOrDigit(s.charAt(k+1))) {
				if (s.charAt(k) == Letter.DOT)
					n++;
				nn = 0;
			}

			// everything else breaks
			else
				break;
		}
			
		if (n == 0)
			return null;

		// get extracted substring
		
		String t = s.substring(0,k);
		s = s.substring(k);
		os += k;
		
		return t;
	}

	// where extraction will next start from

	public final int getOffset ( ) { return os; }
	
}
