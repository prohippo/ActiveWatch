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
// AW file Format.java : 10Jul99 CPM
// simple data formatting still useful even with JDK 1.1

package aw;

public class Format {

	// left-justified string with blank fill
	
	public static String it (
		String s,
		int    w
	) {
		int ls = s.length();
		if (ls > w)
			return s.substring(0,w);
		else if (ls < w) {
			StringBuffer b = new StringBuffer(s);
			for (int k = ls; k < w; k++)
				b.append(' ');
			return b.toString();
		}
		else
			return s;
	}

	// right-justified integer with specified fill
	
	public static String it (
		int  n,
		int  w,
		char x
	) {
		String s = Integer.toString(n);
		int ls = s.length();
		if (ls >= w)
			return s;
		else {
			StringBuffer b = new StringBuffer();
			if (x == '0')
				if (s.charAt(0) == '-') {
					b.append('-');
					s = s.substring(1);
				}
			for (int k = ls; k < w; k++)
				b.append(x);
			return b.toString() + s;
		}
	}

	// right-justified integer with default blank fill
	
	public static String it (
		int  n,
		int  w
	) {
		return it(n,w,' ');
	}
	
	private static int[] power = {
		1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000
	};

	// right-justified floating point with specified fractional precision
	
	public static String it (
		double f,
		int    w,
		int    p
	) {
		boolean sign = false;
		
		StringBuffer b = new StringBuffer(w);
		
		if (p > power.length - 1)
			p = power.length - 1;
		if (p > w - 1)
			p = w - 1;

		if (f < 0) {
			f = -f;
			sign = true;
		}			
		int is = (int) Math.floor(f);
		
		String t = Integer.toString(is);
		int nm = t.length() + ((sign) ? 1 : 0);
		int lm = w - p - 1;
		if (nm > lm)
			p -= nm - lm;
		if (p < 0)
			p = 0;
		
		int fs = (int) Math.floor((f - is)*power[p]*2);
		int ru = fs%2;
		fs = (fs >> 1) + ru;
		if (fs == power[p]) {
			fs = 0;
			String xt = Integer.toString(is+1);
			if (xt.length() > t.length())
				nm++;
			t = xt;
		}
		while (nm++ < lm)
			b.append(' ');
			
		if (sign)
			b.append('-');
		b.append(t);
		
		int ls = b.length();
		
		if (ls < w) {
			b.append('.');
			t = (p > 0) ? Integer.toString(fs) : "";
			nm = t.length();
			lm = w - ls - 1;
			while (nm++ < lm)
				b.append('0');
			b.append(t);
		}
		return b.toString();
	}
	
	// special for byte values in hexadecimal
	
	private static final String code = "0123456789abcdef";
	
	public static String hex (
	
		int x
		
	) {
		int l = (x & 0x0F);
		x >>= 4;
		int u = (x & 0x0F);
		char[] b = new char[2];
		b[0] = code.charAt(u);
		b[1] = code.charAt(l);
		return new String(b);
	}
	
}

