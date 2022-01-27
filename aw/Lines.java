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
// AW File Lines.java : 04aug2021 CPM
// line indexing for text segmentation and display

package aw;

import aw.CharArray;
import java.io.*;

public class Lines {

	private static final char LF = '\n';
	private static final int  M  = 4000; // maximum line count

	private static final String PUNCb = ".,;:!?-";

	private String ss;  // text being lined

	private int   ncs;  // current char count
	private int   nln;  // current line count
	private int[] lnx;  // relative char offsets to lines 

	private Inputs in;  // buffered text stream source

	// constructor with default line count

	public Lines (
		Inputs stream // text source
	) {
		this(stream,M);
	}

	// constructor for line indexing

	public Lines (
		Inputs stream, // text source
		int    n       // maximum line count
	) {
		lnx = new int[n+1];
		nln = 1;
		ncs = 0;
		in  = stream;
	}

	// add a chunk of buffered text for line indexing

	public int record (
		CharArray s,  // text to add for indexing
		int      lm   // line length for wrapping
	) {
		if (s == null)
			return 0;
		ss = s.toString();
		int ll = ss.length();
		ncs += ll;
//		System.out.println("nln= " + nln);
//		System.out.println(ll + " [" + ss + "]");

		int bs = 0;   // at start of text to line

		lnx[0] = 0;

		int k,n;
		for (k = 0; ll > 0; k++) {

			// check for index array overflow

			if (nln == lnx.length - 1)
				return k;

			// text too long for one display line?

//			System.out.println("k= " + k + ", ll= " + ll + ", lm= " + lm);

			if (ll <= lm) {
//				System.out.println("take rest of text");
				n = ll;
			}
			else {

				// otherwise, break off a line at a reasonable place

				int nbs;

				for (nbs = bs + lm; nbs > bs; --nbs) {
					if (Character.isWhitespace(ss.charAt(nbs))) {
						nbs++;
						break;
					}
					else if (!Character.isLetterOrDigit(ss.charAt(nbs))) {
						if (PUNCb.indexOf(ss.charAt(nbs)) >= 0) nbs++;
						break;
					}
				}

				// if no reasonable place, break arbitrarily

				n = (nbs == bs) ? lm : nbs - bs;
			}

			// any room left in index?

			if (nln == lnx.length)
				break;

			// record relative offset for the next line

//			System.out.println("nln= " + nln + ", bs= " + bs + ", ll= " + ll + ", n= " + n);

			bs += n;
			ll -= n;
			if (nln < lnx.length) {
				lnx[nln] = lnx[nln-1] + n;
				nln++;
			}
//			System.out.println("ll= " + ll + ", bs= " + bs);
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

		int o = start;

		// mark start of text segment in line index

		for (i = 0; i <= nln; i++)
			if (lnx[i] > o)
				break;
		if (--i < 0)
			return false;
		lnx[i] = o;

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
		lnx[0] = 0;
		nln = 1;
		ncs = 0;
	}

	// get a copy of the filled out part of line index array in chars

	public final int[] getCharX (
		int nsk
	) {
		if (nsk >= nln)
			return new int[0];
		int[] nlnx = new int[nln-nsk+1];
		System.arraycopy(lnx,nsk,nlnx,0,nlnx.length);
		return nlnx;
	}

	// total number of lines

	public final int countAll ( ) { return nln - 1; }

	// total number of chars

	public final int textLength ( ) { return ncs; }

	// actual text being lined

	public final String textString ( ) { return ss; }

	public static void main ( String[] as ) {
		String file = (as.length > 0)? as[0] : "text";
		CharArray data;
		try {
			FileInputStream in = new FileInputStream(file);
			Inputs stream = new Inputs(in);
			Lines lx = new Lines(stream);
			int nra = 0;

			while ((data = stream.input()) != null) {
				int nr = lx.record(data,100);
				System.out.println(nr + " line(s) added, with " + nra + " already");
				int[] ax = lx.getCharX(nra);
				System.out.println("index length= " + ax.length + ", skip " + nr);
				System.out.println("@" + lx.countAll() + ", " + lx.textLength() + " chars");
				for (int i = 0; i < ax.length; i++)
					System.out.println("  " + (nra+i) + ": " + ax[i]);
				System.out.println("= [" + data.toString() + "]");
				nra += nr;
			}
			System.out.println("----");
			int[] x = lx.getCharX(0);
			for (int i = 0; i < x.length; i++)
				System.out.println(x[i]);
		} catch (IOException e) {
			System.err.println(e);
		}
	} 

}
