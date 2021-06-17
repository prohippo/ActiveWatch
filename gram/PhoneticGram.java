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
// AW file PhoneticGram.java : 22Oct97 CPM
// basic n-gram extraction

package gram;

import aw.Letter;

public class PhoneticGram {

	public static short get (
		TokenBuffer tb
	) {
		byte n;
		int i,j,k;
		int sum;
		int inc;
		int last;

		i = 0;		
		if ((n = tb.buffer[i++]) >= Letter.NA)
			return 0;

		// index with consonant categories

		last = Gram.Ceq[n];

		if (last == 0) {
			sum = 0;
			inc = 1;
			j = 0;
		}
		else {
			sum = last - 1;
			inc = 0;
			j = 1;
		}

		while ((n = tb.buffer[i++]) < Letter.NA) {

			// check for non-consonant
 
 			if ((k = Gram.Ceq[n]) == 0)
				last = 0;

			// check for repeated consonant class
			
			else if (k != last && j < Gram.PHL) {
				last = k;
				sum = Gram.nC*sum + --k;
				if (++j == Gram.PHL) break;
			}
		}

		// compute index number

		return (short) ((j > 0) ? Gram.Bxs[j] + (sum<<1) + inc : Gram.B0s);
	}
}
