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
// AW File Pattern.java : 07Jul2021 CPM
// string pattern matching class for message formats
// ignoring distinctions between upper and lower case


package match;

import aw.*;
import java.util.*;

public class Pattern {

	private static class Component {

		byte   type;   // component type
		byte   length; // component length
		String string; // component characters

		// encodings for type
				
		static final byte LIT= 0; // literal string     type
		static final byte CLS= 1; // character class    type
		static final byte NOT= 2; // character complement
		static final byte REP= 3; // span of char class
		static final byte NON= 4; // span of class complement
		static final byte SKN= 5; // skip characters    type
		static final byte SKP= 6; // match anything     type
		static final byte ABC= 7; // match alphabetic   type
		static final byte DIG= 8; // match digit        type
		static final byte SPC= 9; // match space        type
		static final byte NLN=10; // new line
		static final byte ASP=11; // span of alphabetic
		static final byte NSP=12; // span of numeric
		static final byte SSP=13; // span of spaces

		// pattern characters
		
		static final char LBKT= '[';  // delimit a class of chars
		static final char RBKT= ']';  //
		static final char TILD= '~';  // take complement of class
		static final char RSTR= '*';  // pattern for 0 or more arbitrary characters
		static final char ALPH= '@';  //         for alphabetic char
		static final char NUMR= '#';  //         for numeric    char
		static final char WILD= '?';  //         for any char except \n
		static final char SPCS= '_';  //         for space or tab char
		static final char NLIN= '/';  //         for end of line
		static final char AMPR= '&';  // will match 1 or more of the next class
		static final char ESCP= '\\'; // will escape any character after it

		// standard character classes
		
		public static final String MSPACES = "\t \r\b";
		public static final String SPACE   = "\t ";	
		public static final String ALPHA   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		public static final String DIGIT   = "0123456789";
		
		public String toString ( ) {
			return String.format("type %2d: %d chars [%s]",type,length,string);
		}
	}

	private boolean anchored; // save for quick reference
	
	private Component[] pat;  // actual built pattern

	private static final int closeLIT (
		ArrayList<Component> p,
		StringBuffer b,
		int n,
		Boolean yes
	) {
		if (b.length() > 0) {
			Component cp = p.get(n);
			cp.length = (byte) b.length();
			cp.string = b.toString().toUpperCase();
			b.setLength(0);
			if (yes) {
				p.add(new Component());
				n++;
			}
		}
		return n;
	}

	public void dump ( ) {
		System.out.println("pattern with " + pat.length + " components");
		for ( Component cp : pat )
			System.out.println(cp);
	}
	
	public Pattern (
		String s  // pattern string
	) throws AWException {

		int n;        // component count
		boolean span; // flag for repetition of class wildcard
		boolean negn; //      for negation

		StringBuffer b = new StringBuffer(); // for saving pattern literals

		ArrayList<Component> p = new ArrayList<Component>(); // for accumulating pattern components

		span = negn = false;
		s = s.trim();

		// set up default literal check as first component
		
		Component cp = new Component();
		cp.type = Component.LIT;
		cp.length = 0;
		cp.string = "";
		p.add(cp);

		if (s.length() > 0) {

			// convert string to pattern structure
		
			for (int j = n = 0; j < s.length(); j++) {
		
				char ch = s.charAt(j);
				byte t  = -1;

				if (ch == Component.NLIN)
					t = Component.NLN;
				else if (ch == Component.RSTR)
					t = Component.SKP;

				if (t >= 0) {

					n = closeLIT(p,b,n,true);

					cp = p.get(n++);
					cp.type   = t;
					cp.length = (byte) ((t != Component.SKP) ? 1 : 0);
					cp = new Component();
					cp.type = Component.LIT;
					p.add(cp);

				}

				else if (ch == Component.TILD)
					negn = true;
				
				else if (ch == Component.LBKT) {

					// handle a class wildcard
				
					n = closeLIT(p,b,n,true);

					cp = p.get(n++);
					cp.type = span ? (negn ? Component.NON : Component.REP) :
							 (negn ? Component.NOT : Component.CLS);
					j++;
					while (j < s.length() && s.charAt(j) != Component.RBKT)
						b.append(s.charAt(j++));

					if (b.length() == 0)
						throw new AWException("empty char subset");

					if (negn)
						b.append('\n');

					cp.length = (byte)(negn ? 0 : 1);
					cp.string = b.toString().toUpperCase();
					p.add(new Component());
					b.setLength(0);

					cp = p.get(n);
					cp.type = Component.LIT;

					span = negn = false;

				}
				else {

					if (ch == Component.ALPH)
						t = (span) ? Component.ASP : Component.ABC;
					else if (ch == Component.NUMR)
						t = (span) ? Component.NSP : Component.DIG;
					else if (ch == Component.SPCS)
						t = (span) ? Component.SSP : Component.SPC;
					else if (ch == Component.WILD)
						t = Component.SKN;

					span = negn = false;

					if (t >= 0) {

						// aggregate single wildcards if they are the same

						if (b.length() == 0 && n > 0 && cp.type == t)
							p.get(n-1).length++;
						else {
							n = closeLIT(p,b,n,true);

							cp = p.get(n++);
							cp.type   = t;
							cp.length = (byte)(1);
							cp = new Component();
							cp.type = Component.LIT;
							p.add(cp);
						}

					}
					else {
				
						if (ch == Component.AMPR) {

							n = closeLIT(p,b,n,true);

							// check for possible class repetition

							int slm = s.length();
							char chx = (j < --slm) ? s.charAt(j+1) : '\0';
							char chy = (j < --slm) ? s.charAt(j+2) : '\0';
							if (chx == Component.LBKT ||
							    chx == Component.TILD && chy == Component.LBKT ||
							    chx == Component.ALPH ||
							    chx == Component.NUMR ||
							    chx == Component.SPCS)
								span = true;
							else
								b.append(ch);  // treat as ordinary char

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

			}

			// take care of any final literal

			n = closeLIT(p,b,n,false);

			if (n == 0)
				throw new AWException("empty pattern");

			anchored = (p.get(0).type != Component.SKP);

			// make component array of proper size

			if (p.size() > n)
				p.remove(n);

			pat = p.toArray(new Component[0]);

		}

	}


	//////// for keeping backup information for match length of pattern components ////////

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

		int np = pat.length;
		st.assign(s);
		sw.assign(s);
		ss.assignNull();
		sc = '\0';
		r = true;
		
		for (sk = k = 0; k < np; ) {

			// enough characters left to match pattern?
			
			Component pk = pat[k];
			int length = pk.length;
			if (st.length() < length) {
				r = false;
				break;
			}
			
			char cs = st.charAt(0);

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
				r = (cs == '\n');
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
					nr += pat[i].length;

				// get starting char to find after skip
				
				sc = '\0';
				if (nr == 0)
					st.assignNull();
				else {
					if (nr > ln) {
						r = false;
						break;
					}
					if (k < np - 1 && pat[k+1].type == Component.LIT)
						sc = pat[k+1].string.charAt(0);
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

case Component.ASP:
case Component.NSP:
case Component.SSP:
				String sp = (pk.type == Component.ASP) ? Component.ALPHA :
					    (pk.type == Component.NSP) ? Component.DIGIT : Component.SPACE;
				for (n = 0; sp.indexOf(st.charAt(n)) >= 0; n++);
				if (r = (n > 0))
					length = n;
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
					n = st.indexOf(sc);
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

	// unit test

	private static final String[] tpl = { "/"  , "&@"     , "abc/"   , "*c*"  , "*&[xyz]*" };
	private static final String[] tsl = { "\n" , "ABCDEF" , "aBc\n"  , "abcd" , "abzzyd"   };

	private static void test (
		Pattern tp,
		String  ts
	 ) throws AWException {
		CharArray ca = new CharArray(ts);
		ca.remap();
		System.out.println("ts= [" + ts + "]");
		System.out.println(tp.match(ca));
	}

	public static void main ( String[] as ) {
		try {
			for (int i = 0; i < tpl.length; i++) {
				Pattern tp = new Pattern(tpl[i]);
				System.out.println("tp= " + tpl[i]);
				test(tp,tsl[i]);
			}
			if (as.length > 1) {
				Pattern tp = new Pattern(as[0]);
				System.out.println("tp= " + as[0]);
				for (int j = 1; j < as.length; j++)
					test(tp,as[j]);
			}
                } catch (AWException e) {
                        System.err.println(e);
                }
	}
}
