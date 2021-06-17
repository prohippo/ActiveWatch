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
// AW File Pattern.java : 30Jul01 CPM
// string pattern matching class

package match;

import aw.*;
import aw.phrase.CharArray;

public class Pattern {

	private static class Component {

		byte   type;   // component type
		byte   length; // component length
		String string; // component characters

		// encodings for type
				
		static final byte LIT= 0; // literal string     type
		static final byte CLS= 1; // character class    type
		static final byte NOT= 2; // character complement
		static final byte REP= 3; // span of characters
		static final byte NON= 4; // span of complement
		static final byte SKN= 5; // skip characters    type
		static final byte SKP= 6; // match anything     type
		static final byte ABC= 7; // match alphabetic   type
		static final byte DIG= 8; // match digit        type
		static final byte SPC= 9; // match space        type
		static final byte TBP=10; // tab position
		static final byte NLN=11; // new line
		static final byte MRK=12; // mark position of match

		// pattern characters
		
		static final char LBKT= '[';  // delimit a class of chars
		static final char RBKT= ']';  //
		static final char TILD= '~';  // take complement of class
		static final char RSTR= '*';  // pattern for 0 or more arbitrary characters
		static final char ALPH= '@';  //         for alphabetic char
		static final char NUMR= '#';  //         for numeric    char
		static final char WILD= '?';  //         for any char except \n
		static final char SPCS= '_';  //         for space or tab char
		static final char CARE= '^';  //         for tab position
		static final char NLIN= '/';  //         for end of line
		static final char BKQU= '`';  //         to mark match position
		static final char AMPR= '&';  // will match 1 or more of the next class
		static final char ESCP= '\\'; // will escape any character after it

		// standard character classes
		
		public static final String MSPACES = "\t \r\b";
		public static final String SPACE   = "\t ";	
		public static final String ALPHA   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		public static final String DIGIT   = "0123456789";
		
	}

	private static final int M = 24; // default component limit
	
	private boolean anchored;   // save for quick reference
	
	private Component[] p; // built pattern
	
	public Pattern (
		String s  // pattern string
	) throws AWException {
		this(s,M);
	}
		
	public Pattern (
		String s, // pattern string
		int    m  // component limit
	) throws AWException {
	
		int n;        // component count
		boolean span; // flag for repetition of class wildcard
		boolean negn; //      for negation
		StringBuffer b = new StringBuffer(); // for saving pattern literals

		Component[] pat = new Component[m];  // temporary array
		for (int i = 0; i < m; i++)
			pat[i] = new Component();

		s = s.trim();

		// default pattern to start with
		
		pat[0].type = Component.LIT;
		if (s.length() == 0) {
			pat[0].string = "";
			p = new Component[1];
			p[0] = pat[0];
			return;
		}

		span = negn = false;

		// convert string to pattern structure
		
		for (int j = n = 0; j < s.length(); j++) {
		
			char ch = s.charAt(j);
			byte t  = -1;

			if (ch == Component.NLIN)
				t = Component.NLN;
			else if (ch == Component.CARE)
				t = Component.TBP;
			else if (ch == Component.RSTR)
				t = Component.SKP;

			if (t >= 0) {

				// match arbitrary substring
				
				if (b.length() > 0) {
					pat[n  ].length = (byte) b.length();
					pat[n++].string = b.toString().toUpperCase();
					b.setLength(0);
				}
				pat[n  ].type   = t;
				pat[n++].length = (byte) ((t != Component.SKP) ? 1 : 0);
				pat[n  ].type = Component.LIT;
				
			}

			else if (ch == Component.TILD)
				negn = true;
				
			else if (ch == Component.LBKT) {

				// handle a class wildcard
				
				if (b.length() > 0) {
					pat[n  ].length = (byte) b.length();
					pat[n++].string = b.toString().toUpperCase();
					b.setLength(0);
				}
				pat[n].type = span ? (negn ? Component.NON : Component.REP) :
									 (negn ? Component.NOT : Component.CLS);
				j++;
				while (j < s.length() && s.charAt(j) != Component.RBKT)
					b.append(s.charAt(j++));

				if (b.length() == 0)
					throw new AWException("empty pattern subset");

				if (negn)
					b.append('\n');

				pat[n  ].length = (byte)(negn ? 0 : 1);
				pat[n++].string = b.toString().toUpperCase();
				b.setLength(0);

				pat[n].type = Component.LIT;
				span = negn = false;
				
			}
			else {
			
				if (ch == Component.ALPH)
					t = Component.ABC;
				else if (ch == Component.NUMR)
					t = Component.DIG;
				else if (ch == Component.SPCS)
					t = Component.SPC;
				else if (ch == Component.WILD)
					t = Component.SKN;
				else if (ch == Component.BKQU)
					t = Component.MRK;

				if (t >= 0) {

					// aggregate single wildcards if they are the same
					
					if (b.length() == 0 && n > 0 && pat[n-1].type == t)
						pat[n-1].length++;
					else {
						if (b.length() > 0) {
							pat[n  ].length = (byte)b.length();
							pat[n++].string = b.toString().toUpperCase();
							b.setLength(0);
						}
						pat[n  ].type   = t;
						pat[n++].length = (byte)((t == Component.MRK) ? 0 : 1);
						pat[n].type = Component.LIT;
					}
					
				}
				else {
				
					if (ch == Component.AMPR) {
					
						// check for possible class repetition
						
						if (s.charAt(j+1) == Component.LBKT ||
							s.charAt(j+1) == Component.TILD && s.charAt(j+2) == Component.LBKT)
							span = true;
						else
							b.append(ch);
							
					}
					else if (ch == Component.ESCP) {

						// handle escaped characters
						
						j++;
						if (j >= s.length())
							throw new AWException("bad pattern");

						if (s.charAt(j) != '0')
							b.append(s.charAt(j));
						else {
							j++;
							int k = 0;
							for (int i = 0; i < 3; i++) {
								if (s.charAt(j) < '0' || s.charAt(j) > '7') break;
								k <<= 3;
								k += s.charAt(j++) - '0';
							}
							b.append(k);
						}
						
					}
					else
						b.append(s.charAt(j));
						
				}
				
			}
			if (n == m)
				throw new AWException("pattern overflow");
				
		}

		// take care of any final literal
		
		if (b.length() > 0) {
			pat[n  ].length = (byte) b.length();
			pat[n++].string = b.toString().toUpperCase();
		}

		if (n == 0)
			throw new AWException("empty pattern");

		// make component array of proper size
		
		p = new Component[n];
		System.arraycopy(pat,0,p,0,n);
		
		anchored = (p[0].type != Component.SKP);
			
	}
	
	//////// (do these allocations once only for any real-time application!) ////////
	
	private static CharArray st = new CharArray(); // current string position
	private static CharArray ss = new CharArray(); // saved   string position for wildcard *
	private static CharArray sw = new CharArray(); // start of match after initial skip
	
	public final int match (
		CharArray s // string to match against pattern
	) {
		int   k,n;  // component indices
		boolean r;  // whether pattern still matches

		int    sk;  // next pattern index after wildcard
		char   sc;  // character for wildcard match alignment
		
		int  mk=0;  // remember match position

		int np = p.length;
		st.assign(s);
		sw.assign(s);
		ss.assignNull();
		sc = '\0';
		r = true;
		
		for (sk = k = 0; k < np; ) {

			// enough characters left to match pattern?
			
			Component pk = p[k];
			int length = pk.length;
			if (st.length() < length) {
				r = false;
				break;
			}
			
			char cs = Character.toUpperCase(st.charAt(0));

			// try to match next component of pattern against string
			
			switch (pk.type) {

case Component.LIT:
				st.set(length);
				r = st.match(pk.string);
				break;

case Component.CLS:
case Component.NOT:
				r = (pk.string.indexOf(cs) >= 0) == (pk.type != Component.NOT);
				break;

case Component.ABC:
				r = st.span(length,Component.ALPHA);
				break;

case Component.DIG:
				r = st.span(length,Component.DIGIT);
				break;

case Component.SPC:
				r = st.span(length,Component.SPACE);
				break;

case Component.NLN:
				for (n = 0; Component.MSPACES.indexOf(st.charAt(n)) >= 0; n++);
				if (r = (st.charAt(n) == '\n'))
					length = n + 1;
				break;

case Component.REP:
				for (n = 0; pk.string.indexOf(st.charAt(n)) >= 0; n++);
				if (r = (n > 0))
					length = n;
				break;

case Component.NON:
				for (n = 0; pk.string.indexOf(st.charAt(n)) < 0; n++);
				r = true;
				length = n;
				break;

case Component.MRK:
				mk = st.position();
				break;
				
case Component.SKN:
case Component.SKP:
				int ln = st.length();
				
				// if definite number of chars to skip, just check string length

				if (length > 0)
					if (ln < length || cs == '\r' || cs == '\n') {
						r = false;
						break;
					}
					else {
						r = true;
						break;
					}

				// on indefinite skip, get minimum chars still needed to match
				
				int nr = 0;
				for (int i = k + 1; i < np; i++)
					nr += p[i].length;

				// get starting char to find after skip
				
				sc = '\0';
				if (nr == 0)
					st.assignNull();
				else {
					if (nr > ln) {
						r = false;
						break;
					}
					if (k < np - 1 && p[k+1].type == Component.LIT)
						sc = p[k+1].string.charAt(0);
				}
				
				// if match still possible, back up to skip and continue from there
				
				if (st.length() == ln - nr)
					sk = 0;
				else {
					ss.assign(st);
					sk = k + 1;
				}
				length = 0;
				r = true;
				break;

case Component.TBP:
				for (n = 0; Component.SPACE.indexOf(st.charAt(n)) >= 0; n++);
				if (n == 0)
					r = false;
				else if (n > 1 || cs == '\t') {
					r = true; length = n;
				}
				else
					r = false;
				break;

default:
				return -1;
			}

			// move to next component on successful match;
			// otherwise realign pattern on last wildcard
			
			if (r) {
				st.skip(length);
				k++;
			}
			else if (sk == 0)
				break;
			else {
			
				// match failed, look for preceding wildcard *
				
				if (ss.length() == 0)
					break;
				
				// if * was matched, back up and shift scan
				
				st.assign(ss);
				st.skip(1);
				if (sc != '\0') {
					n = st.indexOfIgnoringCase(sc);
					if (n < 0)
						break;
					st.skip(n);
				}

				// save state for next match failure, if any
				
				k = sk;
				if (st.length() == 0)
					sk = 0;
				else
					ss.assign(st);

				// for match offset if pattern starts with indefinite wildcard
				
				if (!anchored)
					sw.assign(st);
			}
		}

		// return match offset >= 0 if match was successful
		
		if (k != np || st.length() > 0)
			return -1;
		else
			return (mk > 0) ? mk : (np > 1) ? (s.length() - sw.length()) : 0;
	}
	
}
