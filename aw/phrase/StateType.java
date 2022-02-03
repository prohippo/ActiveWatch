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
// StateType.java : 26jan2022 CPM
//  hard logic to recognize state references in addresses

package aw.phrase;

public class StateType {

	private static final char Stp = (char) Parsing.Phrase;
	private static final char Del = (char) Parsing.Empty;
	
	private static final boolean T = true, F = false;

	private static final boolean dg[]= { // coded standard USPS abbreviations
		F,F,F,F,F,F,F,F,F,F,T,T,F,F,F,F,F,T,T,F,F,F,F,F,F,T,
		F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,
		T,F,F,F,F,F,F,F,F,F,F,F,T,F,T,F,F,F,F,T,F,F,F,F,F,T,
		F,F,T,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,
		F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,
		F,F,F,F,F,F,F,F,F,F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,F,
		T,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F,F,F,F,
		F,F,F,F,F,F,F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,
		T,F,F,T,F,F,F,F,F,F,F,T,F,T,F,F,F,F,F,F,F,F,F,F,F,F,
		F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,
		F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F,F,F,F,T,F,
		T,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,
		T,F,F,T,T,F,F,F,T,F,F,F,F,T,T,F,F,F,T,T,F,F,F,F,F,F,
		F,F,T,T,T,F,F,T,F,T,F,F,T,F,F,F,F,F,F,F,F,T,F,F,T,F,
		F,F,F,F,F,F,F,T,F,F,T,F,F,F,F,F,F,T,F,F,F,F,F,F,F,F,
		T,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F,F,F,F,F,F,F,
		F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,
		F,F,F,F,F,F,F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,
		F,F,T,T,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,
		F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F,F,F,F,T,F,F,F,T,F,F,
		F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F,F,F,F,F,
		T,F,F,F,F,F,F,F,T,F,F,F,F,F,F,F,F,F,F,T,F,F,F,F,F,F,
		T,F,F,F,F,F,F,F,T,F,F,F,F,F,F,F,F,F,F,F,F,T,F,F,T,F,
		F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,
		F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,
		F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F,F
	};
	
	private static final int M = 26;
	
	// find state reference in string
	
	public static int match (
	
		char[] s, // string will null terminator
		int    o  // offset in buffer
		
	) {
		int to = o;
		
		// look for preceding comma
		
		int lm = s.length - 1;
		if (lm < to + 4 || s[to] != ',')
			return 0;
		to++;
		to += CharArrayWithTypes.trim(s,to);

		// get next two letters X, Y

		int uo = to;
		char x = s[to++];
		if (!Character.isUpperCase(x))
			return 0;
		char y = s[to];

		// get total length of letter sequence
		
		int k = 0;
		int lk = lm - to;
		for (; k < lk; k++)
			if (!Character.isLetter(s[to+k]))
				break;
		to += k;

		// check for abbreviation like XY. or X.Y.

		if (k < 2) {
			if (k > 0) {
				if (Character.isLowerCase(y)) {
					if (s[to] != '.')
						return 0;
					y = Character.toUpperCase(y);
				}
			}
			else {
				to++;
				if (y != '.')
					return 0;
				if (!Character.isWhitespace(s[to])) {
					if  (!Character.isUpperCase(s[to]))
						return 0;
					y = s[to];
					to++;
					if (s[to] != '.')
						return 0;
					k = 1;
				}
			}
		}

		// for 2-element state names

		CharArrayWithTypes.set(s,uo,5);
		
		if (!Character.isWhitespace(s[to]) && !Character.isLetterOrDigit(s[to]))
			;
		else if (k == 4) {
			if (CharArrayWithTypes.match("NORTH") ||
				CharArrayWithTypes.match("SOUTH")) {
				x = 4;
				uo += 5;
			}
			if (CharArrayWithTypes.match("RHODE")) {
				x = 3;
				uo += 5;
			}
		}
		else if (k == 3) {
			if (CharArrayWithTypes.match("WEST")) {
				x = 2;
				uo += 4;
			}
		}
		else if (k == 2) {
			if (CharArrayWithTypes.match("NEW")) {
				x = 1;
				uo += 3;
			}
		}
		else if (k == 1) {
			if (CharArrayWithTypes.match("NO.") ||
				CharArrayWithTypes.match("SO.")) {
				x = 4;
				uo += 3;
				k = 2; // to get right interpretation
			}
		}
		else if (k == 0) {
			if (x == 'W' && y == '.') {
				x = 2;
				uo += 2;
				k = 2; // to get right interpretation
			}
		}

		// common code for the preceding four logic branches

		if (!Character.isLetter(x)) {
			if (!Character.isWhitespace(s[uo]))
				return 0;
			int nn = CharArrayWithTypes.trim(s,uo);
			uo += nn;
			if (!Character.isUpperCase(s[uo]))
				return 0;
		}

		boolean f = false; // match flag

		if (k == 1) {

			// look up digrams
			
			if (!dg[M*(x - 'A') + (y - 'A')])
				return 0;
				
		}
		
		else {
		
			// match minimal sequences case by case
			
			if (Character.isLetter(x)) 
				uo++;
			CharArrayWithTypes.set(s,uo,3);

			switch (x) {
	case 'A':
				f = (CharArrayWithTypes.match("LA") ||
					 CharArrayWithTypes.match("RK") ||
					 CharArrayWithTypes.match("RIZ"));
				break;
	case 'C':
				f = (CharArrayWithTypes.match("AL") ||
					 CharArrayWithTypes.match("OL") ||
					 CharArrayWithTypes.match("ONN"));
				break;
	case 'D':
				f = (CharArrayWithTypes.match("EL"));
				break;
	case 'F':
				f = (CharArrayWithTypes.match("LOR") ||
					 CharArrayWithTypes.match("LA"));
				break;
	case 'G':
				f = (CharArrayWithTypes.match("EOR"));
				break;
	case 'H':
				f = (CharArrayWithTypes.match("AWA"));
				break;
	case 'I':
				f = (CharArrayWithTypes.match("DA") ||
					 CharArrayWithTypes.match("LL") ||
					 CharArrayWithTypes.match("ND") ||
					 CharArrayWithTypes.match("OWA"));
				break;
	case 'K':
				f = (CharArrayWithTypes.match("AN") ||
					 CharArrayWithTypes.match("EN"));
				break;
	case 'L':
				f = (CharArrayWithTypes.match("OUI"));
				break;
	case 'M':
				f = (CharArrayWithTypes.match("AIN") ||
					 CharArrayWithTypes.match("ARY") ||
					 CharArrayWithTypes.match("ASS") ||
					 CharArrayWithTypes.match("ICH") ||
					 CharArrayWithTypes.match("INN") ||
					 CharArrayWithTypes.match("ISS") ||
					 CharArrayWithTypes.match("ONT"));
	case 'N':
				f = (CharArrayWithTypes.match("EB")  ||
					 CharArrayWithTypes.match("EV"));
				break;
	case 'O':
				f = (CharArrayWithTypes.match("HIO") ||
					 CharArrayWithTypes.match("KL")  ||
					 CharArrayWithTypes.match("RE"));
				break;
	case 'P':
				f = (CharArrayWithTypes.match("ENN"));
				break;
	case 'T':
				f = (CharArrayWithTypes.match("ENN") ||
					 CharArrayWithTypes.match("EX"));
				break;
	case 'U':
				f = (CharArrayWithTypes.match("TAH"));
				break;
	case 'V':
				f = (CharArrayWithTypes.match("ER") ||
					 CharArrayWithTypes.match("IR"));
				break;
	case 'W':
				f = (CharArrayWithTypes.match("ASH") ||
					 CharArrayWithTypes.match("IS")  ||
					 CharArrayWithTypes.match("YO"));
			
	case 1:
				f = (CharArrayWithTypes.match("HAM") ||
					 CharArrayWithTypes.match("JER") ||
					 CharArrayWithTypes.match("MEX") ||
					 CharArrayWithTypes.match("YOR"));
				break;
	case 2:
				f = (CharArrayWithTypes.match("VIR") ||
					 CharArrayWithTypes.match("VA."));
				break;
	case 3:
				f = (CharArrayWithTypes.match("ISL"));
				break;
	case 4:
				f = (CharArrayWithTypes.match("CAR") ||
					 CharArrayWithTypes.match("DAK"));
				break;
			}
			if (!f)
				return 0;

			// on match, skip remaining chars
			
			for (uo += 2; Character.isLetter(s[uo]); uo++);
			to = uo;
		}

		// check context of what follows

		if (s[to] > 0) {
		
			if (Character.isWhitespace(s[to])) {
			
				// space should be followed by possible zipcode
				
				for (uo = to; Character.isWhitespace(s[uo]) || s[uo] == Del; uo++);
				if (!digits(s,uo,5))
					return 0;
				uo += 5;
				if (s[uo] == '-')
					if (digits(s,uo+1,4))
						uo += 5;
					else
						return 0;
				to = uo;
					
			}
			else if (Character.isLetterOrDigit(s[to]))
			
				// should not happen
				
				return 0;
				
			else {
			
				// check first char afterward
				
				if (s[to] == ',')
					s[to] = Del;
				else if (s[to] == '.') {
					to++;
					if (s[to] == 0)
						;
					else if (Character.isWhitespace(s[to]))
						s[uo] = Stp;
					else if (!Character.isLetterOrDigit(s[to]))
						return 0;
				}
					
			}
		}

		return (to - o);
	}
	
	// match a sequence of k digits
	
	private static boolean digits (
		char[] s,
		int   uo,
		int    k
	) {
		for (int j = 0; j < k; j++)
			if (!Character.isDigit(s[uo++]))
				return false;
		return !Character.isLetterOrDigit(s[uo]);
	}

}
