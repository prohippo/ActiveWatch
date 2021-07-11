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
// AW File Lines.java : 03Jul2021 CPM
// line indexing class for text

package aw.segment;

import aw.CharArray;

public class Lines {

	static final char LF = '\n';
	static final int  M  = 4000; // maximum line count

	int   bas; // last text position
	int   bln; // beginning of lines
	int   nln; // current line count
	int[] lnx; // relative line offsets
	
	Inputs in; // specially buffered text source for reference only

	// constructor with default line count
	
	public Lines (
		Inputs stream // text source
	) {
		this(stream,M);
	}
	
	// constructor
	
	public Lines (
		Inputs stream, // text source
		int    n       // maximum line count
	) {
		lnx = new int[n+1];
		in = stream;
	}

	// add a chunk of buffered text for line indexing
	
	private static CharArray ss = new CharArray();
		
	public int record (
		CharArray s, // text to add for indexing
		int      lm  // line length for wrapping
	) {
		if (s == null)
			return 0;
		ss.assign(s);

		// get current position in input with no side effects
		
		if (nln == 0)
			bas = in.position(-1);
			
		// trim any trailing linefeed
		
		int ll = ss.length();
		if (ll > 0 && ss.charAt(ll-1) == LF)
			--ll;

		// how many characters actually covered by line
		
		int no = in.nextPosition() - bas;

		int k,n;
		for (k = 0; ll > 0; k++) {

			// avoid index array overflow
			
			if (nln == lnx.length - 1)
				return k;
	
			// text too long for one line?

			if (ll <= lm)

				// if not, take rest of text as one line
				
				break;

			else {

				// otherwise, break off a line at a reasonable place
				
				for (n = lm; n > 0; --n)
					if (Character.isWhitespace(ss.charAt(n)))
						break;

				if (n > 0)
					n++;
				else
					for (n = lm; n > 0; --n)
						if (!Character.isLetterOrDigit(ss.charAt(n)))
							break;
				
				// if no reasonable place, break arbitrarily
				
				if (n == 0)
					n = lm;
			}

			// record relative offset for the next line

			int o = lnx[nln] + n;
			lnx[++nln] = o;
			ll -= n;
			ss.skip(n);
		}
		
		if (nln < lnx.length - 1) {
			lnx[++nln] = no; // offset to end of current line
			k++;
		}
		
		return k; // number of lines added to index
	}
	
	// reset a line index to reflect a text segment
	
	public boolean register (
		int start, // where to set new segment
		int ln     // segment length
	) {
		int i,n;

		// check for valid segment within line index

		if (start < bas)
			return false;
			
		int o = start - bas;

		// mark start of text segment in line index

		for (i = 0; i <= nln; i++)
			if (lnx[i] > o)
				break;
		if (--i < 0)
			return false;
		lnx[i] = o;
		bln = i;

		// mark end of text segment in line index

		o += ln;
		for (; i <= nln; i++)
			if (lnx[i] >= o)
				break;
		if (lnx[i] > o)
			lnx[i] = o;
		if (nln > i)
			nln = i;
		return true;
	}

	// zero out line index
	
	public final void reset (
	) {
		bln = nln = lnx[0] = 0;
	}

	// get a copy of line index for text body only
	
	public final int[] get (
	
	) {
		int m = nln - bln + 1;
		int[] nlnx = new int[m];
		System.arraycopy(lnx,bln,nlnx,0,m);
		return nlnx;
	}

	// total number of lines
	
	public final int countAll ( ) { return nln; }

	// number of text lines
	
	public final int countText ( ) { return nln - bln; }

	// shift line index forward to define a new starting line
	
	public final int advance (
		int no // where to put the new starting line
	) {
		int k;

		for (k = 0; bln < nln && lnx[bln] < no; k++)
			bln++;

		for (int i = bln, n = lnx[bln]; i <= nln; i++)
			lnx[i] -= n;

		return k;
	}

}
