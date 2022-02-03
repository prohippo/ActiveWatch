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
// Rewriter.java : 25jan2022 CPM
// normalize certain non-standard renditions of names

package aw.phrase;

import aw.CharArray;
import java.io.*;

public class Rewriter {

	public  static final String file = "rewrites";
	
	private static final int H =100;
	private static final int M = 64; // maximum rule count
	private static final int N = 32; // first characters for rules

	private static final char EMPTY = (char) Parsing.Empty;
	private static final char DELIM = (char) -32768;
	
	private short   count = 0; // rule table itself
	private short[] index = new short[M+1];
	private char[]  rules;
	
	private String  leads;

	// initialize by loading equivalences
	
	public Rewriter (
	
	) {
		StringBuffer sb = new StringBuffer(H);
		StringBuffer ld = new StringBuffer( );
		String r;
			
		try {
		
			BufferedReader in = new BufferedReader(new FileReader(file));
			while ((r = in.readLine()) != null) {
				if (count == M)
					break;
				int m = r.length();
				if (m == 0)
					continue;
				char[] cb = new char[2*m+2];
				r.getChars(0,m,cb,0);
				int k = norm(cb,m);
				sb.append(cb,0,k + 1);
				ld.append(cb[0]);
				index[++count] = (short) sb.length();
			}
			in.close();
			int n = sb.length();
			rules = new char[n];
			sb.getChars(0,n,rules,0);
			leads = ld.toString();
			
		} catch (IOException e) {
		}
	}

	// convert equivalence to standard internal form
	
	private int norm (
		char[] r,
		int   lr
	) {
		int s,t;
		
		int ss = -1;

		for (s = t = 0; t < lr;) {
		
			switch (r[t]) {
			
	case '\\':
				t++;
				char c = r[t];
				if (c == '-')
					r[t] = EMPTY;
				else if (c == '0')  {
					int k = 0;
					t++;
					for (int i = 0; i < 3; i++) {
						if (!Character.isDigit(r[t]) || r[t] > '7')
							break;
						k <<= 3;
						k += r[t++] - '0';
					}
					r[--t] = (char) k;
				}
					
				break;
	case '/':
				r[t] = '\n';
				break;
	case '=':
				if (ss < 0) {
					r[t] = DELIM;
					ss = s;
				}
				break;
				
			}
			r[s++] = r[t++];

		}
		if (ss < 0) {
			ss = s;
			r[s++] = DELIM;
		}
		int n = 2*ss + 1;
		if (s > n)
			s = n;
		else
			while (s < n)
				r[s++] = EMPTY;
		r[s] = 0;

		return s;
	}

	// replace everything in a char array by their equivalences
			
	void rewrite (
		CharArray r
	) {
		if (count == 0)
			return;
		int lr = r.length();
		for (int ir = 0;; ir++) {
			for (; ir < lr; ir++)
				if (leads.indexOf(r.charAt(ir)) >= 0)
					break;
			if (ir == lr)
				break;
				
			for (int i = 0; i < count; i++) {
				int t = index[i];
				int n = 0;
				for (; ir + n < lr; n++)
					if (r.charAt(ir+n) != rules[t+n])
						break;
				if (rules[t+n] == DELIM) {
					t += n + 1;
					for (int k = 0; k < n; k++)
						r.setCharAt(ir+k,rules[t+k]);
					break;
				}
			}
		}
	}
	
}

