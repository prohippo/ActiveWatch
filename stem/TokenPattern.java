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
// TokenPattern.java : 27May00 CPM
// matching of AW tokens

package stem;

import aw.Letter;

public class TokenPattern {

	private static final byte SKP = (byte)(Letter.NAN + 10); // match any substring
	private static final byte SKA = (byte)(Letter.NAN + 11); // ... letter sequence 
	private static final byte SKN = (byte)(Letter.NAN + 12); // ... digit  sequence
	private static final byte LTR = (byte)(Letter.NAN + 13); // ... single letter
	private static final byte DIG = (byte)(Letter.NAN + 14); // ... single digit
	private static final byte ANY = (byte)(Letter.NAN + 15); // ... any letter or digit

	private boolean wild = false;
	
	private byte[] pb; // pattern buffer
	private int  minl; // minimum length

	// encode from string
	
	public TokenPattern (
		String s
	) {
		int n = s.length();
		pb = new byte[n];
		char x = '?';
		minl = n;
		for (int i = 0; i < n; i++) {
			char c = s.charAt(i);
			if (Character.isLetterOrDigit(c))
				pb[i] = Letter.toByte(c);
			else if (c == '*') {
				pb[i] = (x == '@') ? SKA : (x == '#') ? SKN : SKP;
				wild = true;
				--minl;
			}
			else if (c == '@')
				pb[i] = LTR;
			else if (c == '#')
				pb[i] = DIG;
			else if (c == '?')
				pb[i] = ANY;
			else if (c == Letter.DOT || c == Letter.APO)
				pb[i] = Letter.toByte(c);
			else
				pb[i] = -1;
			x = c;
		}
	}
	
	// quick test to see if pattern can match n letters
	
	public final boolean matchable ( int n ) {
		return (n == pb.length || (wild && n >= minl));
	}
	
	// compare token to pattern
	
	public final boolean match (
		Token  tk
	) {
		int  lt = tk.length();
		int  lp = pb.length;
		int  bp = -1; // for backup
		int  bt = -1; //
		byte bb =  0; //

		boolean m = true;
		int p = 0;
		int t = 0;
		for (; t < lt && p < lp; p++, t++) {

			byte x = tk.array[t];
			switch (pb[p]) {
case SKP:
case SKA:
case SKN:
				bp = p;
				bt = t;
				bb = pb[p];
				--t;
				break;
case LTR:
				m = Letter.alphabetic(x);
				break;
case DIG:
				m = Letter.numeric(x);
				break;
case ANY:
				m = !Letter.punctuating(x);
				break;
default:
				m = (pb[p] == x);
			}

			if (!m) {
				if (bp < 0)
					break;
				else {
					byte y = tk.array[bt];
					if (bb == SKA && !Letter.alphabetic(y))
						break;
					if (bb == SKN && !Letter.numeric(y))
						break;
					m = true;
					p = bp;
					t = bt++;
				}
			}
		}
		
		// check for match
		
		if (m) {
			int pm = pb.length - 1;
			if (p != pb.length) {
				if (p == pm) {
					byte x = pb[p];
					return (x == SKP || x == SKA || x == SKN);
				}
			}
			else if (t == lt)
				return true;
			else {
				byte x = pb[pm];
				if (x == SKP)
					return true;
				if (x == SKA) {
					while (t < lt)
						if (!Letter.alphabetic(tk.array[t++]))
							return false;
				}
				else if (x == SKN) {
					while (t < lt)
						if (!Letter.numeric(tk.array[t++]))
							return false;
				}
				else
					return false;
				return true;
			}
		}
		return false;
	}
	
}