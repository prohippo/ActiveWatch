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
// TokenBuffer.java : 19aug2021 CPM
// auxiliary data structure for n-gram index extraction

package gram;

import aw.Letter;
import stem.TokenForm;

/**
 * char array for sequential n-gram extraction
 */

public class TokenBuffer {

	public static final char NX =  0x0; // sentinel for buffer
	public static final int  LN =  256; // basic allocation for buffer

	// for direct use by n-gram indexing classes only!

	char[] buffer = new char[LN];       // actual buffer
	int start,end; // where token lies in buffer
	int fwrd,rvrs; // current extraction extents
	int next;      // where to begin scanning for next n-gram

       /**
	* create empty buffer for n-gram analysis
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
		buffer[k++] = NX;

		// copy token as chars to buffer, less punctuation

		start = next = k;
		byte[] ta = token.toArray();
		for (int i = 0; i < token.length(); i++) {
			byte x = ta[i];
			if (Letter.alphanumeric(x))
				buffer[k++] = Letter.from[x];
		}

		// set limits

		end = k;
		fwrd = start;
		rvrs = end;

		// put in sentinel

		buffer[end] = NX;
	}

       /**
	* how many chars left in buffer
	* @return character count
	*/

	public final int  left ( ) { return rvrs - fwrd; }

       /**
	* is buffer exhausted from forward extraction?
	* @return boolean
	*/

	public final boolean exhausted ( ) { return (fwrd == end); } 

       /**
	* take next char from buffer and advance pointer
	* @return char
	*/

	public final char next ( ) { return buffer[fwrd++]; }

       /**
	* just look at next char in buffer without advancing pointer
	* @return char
	*/

	public final char peek ( ) { return buffer[fwrd]; }

       /**
	* just look at char after next in buffer without advancing pointer
	* @return char
	*/

	public final char peekAfter ( ) { return buffer[fwrd+1]; }

       /**
	* begin next pass of n-gram extraction
	* @return next starting position in buffer
	*/

	public final int goNext () { return next++; }

       /**
	* restart buffer
	*/

	public final void restart ( ) { next = fwrd = start; rvrs = end; }

       /**
	* get string from range of chars in buffer
	* @param ib starting index
	* @paran le length
	* @return String
	*/

	public final String toString (
		int ib,
		int le 
	) {
		if (le <= 0 || ib + le > end)
			return "";
		else
			return new String(buffer,ib,le);
	}



       /**
	* get print representation of current buffer
	* @return String
	*/

	public final String toString (

	) {
		String lss = toString(start,fwrd-start);
		String mss = toString(fwrd,rvrs-fwrd);
		String rss = toString(rvrs,end-rvrs);
		return start + " f=" + fwrd + ", r=" + rvrs + " " + lss + "|" + mss + "|" + rss;
	}

}
