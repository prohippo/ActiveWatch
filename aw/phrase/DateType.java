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
// DateType.java : 033Jul04 CPM
// hard-logic to recognize dates in text

package aw.phrase;

public class DateType {

	private static final String[] mo = {
		"JANUARY", "FEBRUARY", "MARCH"    , "APRIL"  , "MAY"     , "JUNE"    ,
		"JULY"   , "AUGUST"  , "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
	};
	
	private static final String dy = "0123";
	
	private static int yr0,yr1; // for year
	private static int mo0,mo1; // for month
	private static int dy0,dy1; // for date

	public static int match (
	
		char[] s,
		int    o
		
	) {
		int os = o;
		int k = aMonth(s,o);
		if (k > 0) {
			o += k;
			k = CharArrayWithTypes.trim(s,o);
			if (k == 0)
				return 0;
			o += k;
			k = aDay(s,o);
			if (k == 0)
				return 0;
			o += k;
			int m = (s[o] == ',') ? 1 : 0;
			
			int om = o + m;
			om += CharArrayWithTypes.trim(s,om);
			int mm = aYear(s,om);
			if (mm > 0)
				o = om + mm;
			return o - os;
		}
		else {
			int lk = aDay(s,o);
			if (lk > 0) {
				o += lk;
				k = CharArrayWithTypes.trim(s,o);
				if (k > 0) {
					o += k;
					if (lk > 2 &&
						Character.toLowerCase(s[o  ]) == 'o' &&
						Character.toLowerCase(s[o+1]) == 'f' &&
						s[o+2] == ' ')
							o += 3;
					k = aMonth(s,o);
					if (k > 0) {
						o += k;
						k = CharArrayWithTypes.trim(s,o);
						if (k == 0)
							return o - os;
						int no = o + k;
						k = aYear(s,no);
						if (k > 0)
							return no - os + k;
						else
							return o - os;
					}
				}
			}
		}
		return 0;
	}
	
	// parse a month name
	
	private static int aMonth ( char[] s, int o ) {
		int k = 3;
		int i = 0;
		if (s.length <= k + o)
			return 0;
		CharArrayWithTypes.set(s,o,9);
		for (; i < mo.length; i++)
			if (CharArrayWithTypes.match(mo[i],k))
				break;
		if (i == mo.length)
			return 0;
			
		int m = i + 1;
		if (m > 9) {
			mo0 = '1';
			m -= 10;
		}
		mo1 = (char)('0' + m);
		
		o += k;
		char x = s[o];
		if (x == '.')
			return k + 1;
		if (Character.isWhitespace(x))
			return k;
			
		String ms = mo[i];
		int n = ms.length();
		while (k < n)
			if (ms.charAt(k++) != Character.toUpperCase(s[o++])) {
			    char c = s[--o];
			    if (Character.isLetterOrDigit(c))
				    return 0;
				else
				    return (c !='.') ? --k : k;
			}
		return n;
	}

	// parse a day number
	
	private static int aDay ( char[] s, int o ) {
		int  k;

		if (s[o] == 0)
			return 0;
		char x = s[o++];
		
		if (Character.isDigit(x)) {
			if (s[o] == 0) {
			    dy0 = x;
				return 1;
			}
			else if (!Character.isDigit(s[o]))
				k = 1;
			else {
				if (dy.indexOf(x) < 0)
					return 0;
				char y = x;
				x = s[o];
				o++;
				if (y == '3' && x > '1')
					return 0;
				dy0 = y;
				k = 0;
				int de = s.length - o - 1;
				if (de > 0) {
					char z = s[o];
					if (Character.isDigit(z))
						return 0;
					if (!Character.isLetter(z))
						if (z == '.' && de > 1 && Character.isDigit(s[o+1]))
							return 0;
				}
				k += 2;
			}
			dy1 = x;

			if (s[o] == 0 || !Character.isLetter(s[o]))
				return k;
				
			CharArrayWithTypes.set(s,o,2);
				
			switch (x) {
	case '1':
				if (!CharArrayWithTypes.match("ST"))
					return 0;
				break;
	case '2':
				if (!CharArrayWithTypes.match("ND"))
					return 0;
				break;
	case '3':
				if (!CharArrayWithTypes.match("RD"))
					return 0;
				break;
	default:
				if (!CharArrayWithTypes.match("TH"))
					return 0;
			}
			o += 2;
			if (s[o] == 0)
				return k + 2;
			else if (Character.isDigit(s[o]))
				return 0;
			else
				return k + 2;
		}
		return 0;
	}

	// parse a year
	
	private static int aYear ( char[] s, int o ) {
		int ln = s.length - o - 1;
		if (ln <= 0)
			return 0;
			
		int n = 0;
		for (; n < ln; n++)
			if (!Character.isDigit(s[n+o]))
				break;
		if (n != 2 && n != 4)
			return 0;
		
		yr0 = s[o+n-2];
		yr1 = s[o+n-1];
		int to = o + n;
		CharArrayWithTypes.set(s,to,5);
		if (n < ln && s[to] == ' ') {
			to++;
			int m = s.length - to - 1;
			if (m < 2 || n == 2)
				;
			else if (CharArrayWithTypes.match("AD") ||
				CharArrayWithTypes.match("BC") ||
				CharArrayWithTypes.match("CE")  ) {
				if (ln > to + 2 || !Character.isLetterOrDigit(s[to+2]))
					return n + 3;
			}
			else if (m < 3)
				;
			else if (CharArrayWithTypes.match("BCE")) {
				if (ln > to + 6 || !Character.isLetterOrDigit(s[to+6]))
					return n + 7;
			}
			else if (m < 4)
				;
			else if (CharArrayWithTypes.match("A.D.") ||
				CharArrayWithTypes.match("B.C.") ||
				CharArrayWithTypes.match("C.E.")  ) {
				if (ln > to + 4 || !Character.isLetterOrDigit(s[to+4]))
					return n + 5;
			}
			else if (m < 6)
				;
			else if (CharArrayWithTypes.match("B.C.E.")) {
				if (ln > to + 6 || !Character.isLetterOrDigit(s[to+6]))
					return n + 7;
			}
		}
		if (n == 4)
			if (s[o] != '1' && s[o] != '2')
				return 0;
			
		if (!Character.isLetter(s[o + n]))
			return n;
			
		return 0;
	}

}
