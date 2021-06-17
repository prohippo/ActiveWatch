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
// TokenBuffer.java : 01Aug02 CPM
// auxiliary data structure to facilitate n-gram indexing

package gram;

import aw.Letter;
import stem.TokenForm;

/**
 * for copying a token to permit manipulation of characters without affecting original token
 */

public class TokenBuffer {

	private static final byte NX = -128; // sentinel for buffer
        private static final int  LN =  256; // basic allocation for buffer (must be BIG)
	
	// for direct use by n-gram indexing classes only!
	
	byte[] buffer = new byte[2*LN]; // actual buffer
	int start,end; // where token lies in buffer
	int fwrd,rvrs; // current extraction limits
	int nor;       // minimum size n-gram to look for

       /**
        * create empty buffer to hold token for indexing by n-grams
        */

	public TokenBuffer (
	) {
	}
			
       /**
        * create buffer from token to index by n-grams
        * @param token to index
        */

	public TokenBuffer (
		TokenForm token
	) {
		set(token);
	}
	
       /**
        * fill buffer from token for indexing by n-grams
        * @param token to index
        */

	public void set (
		TokenForm token
	) {
		int  k = 0;

		// copy token to buffer, less punctuation
				
		start = LN;
                byte[] ta = token.toArray();
		for (int i = 0; i < token.length(); i++) {
			byte x = ta[i];
			if (x < Letter.NAN)
				buffer[start+(k++)] = x;
		}

		// set limits
				
		end = start + k;
		fwrd = start;
		rvrs = end;
		nor = 2;
		
		// put in sentinels
		
		buffer[start-1] = buffer[end] = NX;
	}
        
       /**
        * how many chars left in buffer
        * @return character count
        */
        
        public final int  left ( ) { return rvrs - fwrd; }

       /**
        * take next character from buffer and advance pointer
        * @return encoded character
        */
        
        public final byte next ( ) { return buffer[fwrd++]; }
        
       /**
        * just look at next character in buffer without advancing pointer
        * @return encoded character
        */
        
        public final byte peek ( ) { return buffer[fwrd]; }
        
        /**
         * just look at character after next in buffer without advancing pointer
         * @return encoded character
         */
        
        public final byte peekAfter ( ) { return buffer[fwrd+1]; }
        
       /**
        * restart buffer
        */
        
        public final void restart ( ) { fwrd = start; rvrs = end; }
        
}