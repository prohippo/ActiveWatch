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
// AW file LinedString.java : 02Sep99 CPM
// break a string into lines for cleaner display

package aw;

public class LinedString {

	private static final int L = 78; // character count for line break
	
	StringBuffer t = new StringBuffer(); // for output lines

	// compose output lines
		
	public LinedString (
		String s,
		int   ll
	) {
		int k;

		if (ll <= 0)
			ll = L;
		s = s.trim();
		while (s.length() >= ll) {
			int ks = 0;
			for (k = 0; k < ll; k++) {
				char c = s.charAt(k);
				if (c == ' ')
					ks = k + 1;
				else if (c == '\n') {
					t.append(s.substring(0,k));
					s = s.substring(k + 1);
					break;
				}
				else if (c == '\r') {
					int ke = k + 1;
					if (ke < s.length() && s.charAt(ke) == '\n')
						ke++;
					t.append(s.substring(0,k));
					s = s.substring(ke);
					break;
				}
			}
			if (k == ll) {
				if (ks == 0)
					ks = ll;
				t.append(s.substring(0,ks));
				s = s.substring(ks);
			}
			t.append('\n');
		}
		t.append(s);
	}

	// get output lines
		
	public String toString (
	) {
		return t.toString();
	}

}