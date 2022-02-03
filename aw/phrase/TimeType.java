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
// TimeType.java : 26jan2022 CPM
// hard logic to recognize time references in text

package aw.phrase;

public class TimeType {

	private static final String[] zone = {
		"PST","PDT",
		"MST","MDT",
		"CST","CDT",
		"EST","EDT",
		"UMT","GMT"
	};
	
	private static final String h = "012";

	public static int match (
	
		char[] s,
		int    o
		
	) {
		if (!Character.isDigit(s[o]))
			return 0;
		char x = s[o];
		int os = o;
		o++;
		
		int to = o;
		
		if (s[o] == 0)
			return 0;
		
		if (Character.isDigit(s[o])) {
			if (h.indexOf(x) < 0)
				return 0;
			if (x == '2') {
				if (s[o] > '4')
					return 0;
			}
			o++;
		}

		if (s[o] == 0)
			return 0;
		
		if (s[o] == ':') {
			o++;
			if (!timeFraction(s,o))
				return 0;
			o += 2;
			if (s[o] == ':') {
				o++;
				if (!timeFraction(s,o))
					return 0;
				o += 2;
			}
		}

		int nn;
		
		if ((nn = CharArrayWithTypes.trim(s,o)) > 0) {
		
			int so = o + nn;
			int m = s.length - so;
			
			CharArrayWithTypes.set(s,so,4);

			if (m < 2 || !Character.isLetter(s[so]))
				;
			else if (CharArrayWithTypes.match("AM") ||
					 CharArrayWithTypes.match("PM")) {

				int sso = so + 2;
				if (CharArrayWithTypes.end(s,sso))
					o = sso;
					
			}
			else if (m < 4)
				;
			else if (CharArrayWithTypes.match("A.M.") ||
					 CharArrayWithTypes.match("P.M.")) {

				int sso = so + 4;
				if (CharArrayWithTypes.end(s,sso))
					o = sso;
					
			}
			
		}

		if ((nn = CharArrayWithTypes.trim(s,o)) > 0) {
		
			int so = o + nn;
			if (s.length >= so + 2 && Character.isLetter(s[so])) {
				CharArrayWithTypes.set(s,so,3);
				int i = 0;
				for (; i < zone.length; i++)
					if (CharArrayWithTypes.match(zone[i]))
						break;
						
				if (i < zone.length) {
					int sso = so + 3;
					if (CharArrayWithTypes.end(s,sso))
						o = sso;
				}
			}
		}

		int n = o - os;
		if (Character.isWhitespace(s[o]))
			if (s[o-1] == '.')
				--n;
		
		return (n > 3) ? n : 0;
	}
	
	private static boolean timeFraction (
		char[] s,
		int    o
	) {
		char x = s[o++];
		if (x < '0' || x > '5')
			return false;
		else
			return Character.isDigit(s[o]);
	}
	
}
