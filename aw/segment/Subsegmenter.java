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
// AW File Subsegmenter.java : 04aug2021 CPM
// for subsegmentation of long text items for analysis

package aw.segment;

import aw.*;
import java.io.*;

public class Subsegmenter {

	static final int M  =  80; // nominal line length
	static final int Nsx= 255; // maximum division count for entire item
	
	Subsegment[] sx;   // array of divisions
	int      sxp,sxl;  // indices into array

	public Subsegmenter (
		Lines  lx, // line index for text to divide up
		int upper, // upper limit on subsegment length
		int lower  // lower limit
	) throws AWException {
	
		int bl,ll,pl;
		int mxn;
		int mln,mpl; // shortest line

		int nl = lx.countAll();        // total line count in subsegment
		if (nl == 0)
			return;
		int[] larry = lx.getCharX(0);  // full line index for subsegment

		start();
		sxp = sxl = 0;
		
		int n = 0;
		for (bl = 0; bl < nl; n++) {

			// get next chunk of text up to limit

			for (ll = bl + 1; ll <= nl; ll++)
				if (larry[ll] - larry[bl] > upper)
					break;

			if (ll > nl)

				// if within limit, set break at end of last line
				
				ll = nl;

			else {

				// otherwise, break at shortest line preceding
				// without taking too little text 
				
				mln = M; mpl = -1;
				for (pl = ll; --pl > bl;) {
					if (larry[pl] - larry[bl] < lower)
						break;
					int ln = larry[pl] - larry[pl-1];
					if (mln > ln) {
						mln = ln; mpl = pl;
					}
				}
				if (mpl >= 0)
					ll = mpl;
					
			}

			if (n == Nsx)
				throw new aw.AWException("subsegment overflow");

			// fill next subsegment record

			sx[n].so = larry[bl];  // offset in chars, not bytes!!

			int k = larry[ll] - larry[bl];
			sx[n].ln = (short) k;  // length in chars!!

			bl = ll;
			
		}
		sxl = n;
		return;
		
	}
	
	// initialize array
	
	private void start (
	) {
	
		if (sx != null)
			return;
		sx = new Subsegment[Nsx];
		for (int i = 0; i < Nsx; i++) {
			sx[i] = new Subsegment();
			sx[i].sn = (short)(i + 1);
		}
		
	}
	
	// access methods

	public final Subsegment get ( ) { return (sxp == sxl) ? null : sx[sxp++]; }

	public final Subsegment[] getAll ( ) { return sx; }

	public final int count ( ) { return sxl; }

}
