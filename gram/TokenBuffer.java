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
// TokenBuffer.java : 06oct2021 CPM
// auxiliary buffer for n-gram index extraction

package gram;

import aw.Letter;
import stem.TokenForm;

/**
 * char array for sequential n-gram extraction
 */

public class TokenBuffer {

	public static final char NX =  0x0;   // sentinel for buffer
	public static final int  LN =  256;   // basic allocation for buffer

	// for direct use by n-gram indexing classes only!

	public  char[] buffer = new char[LN]; // actual buffer
	public  int start,end; // where token lies in buffer
	public  int fwrd,rvrs; // current extraction extents
	private int posn = 0;  // remember where last n-gram index started
	private int maxn = 0;  // maximum n-gram length

       /**
	* create empty buffer for n-gram analysis
	* @param mxn maximaum lexical n-gram length
	*/

	public TokenBuffer (
		int mxn
	) {
		maxn = mxn;
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

		start = k;
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
		posn = 0;

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
	* reposition in buffer after successful n-gram extraction
	* n = last lexical n-gram length
	* @return new starting position in buffer
	*/

	public final int reposition ( int n ) {
		if (n > 0)
			posn = fwrd - n + 1;
		if (posn == 0)
			posn = 1;
		return posn;
	}

       /**
	* get saved position of last n-gram extraction
	* @return position
	*/

	public final int position ( ) { return posn; }

       /**
	* restart buffer
	*/

	public final void restart ( ) { posn = 0; fwrd = start; rvrs = end; }

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
