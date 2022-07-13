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
// AW File Lines.java : 08jul2022 CPM
// line indexing for text segmentation and display

package aw;

import aw.CharArray;
import java.io.*;

public class Lines {

	private static final char LF = '\n';
	private static final int  M  = 4000; // maximum line count

	private static final String PUNCb = ".,;:!?-";

	private int   skp;  // saved count of skipped lines
	private int   ncs;  // current char count
	private int   nln;  // current line count
	private int[] lnx;  // relative char offsets to lines 

	private StringBuffer sb; // cumulative text being lined

	// constructor with default line count

	public Lines (

	) {
		this(M);
	}

	// constructor for line indexing

	public Lines (
		int    n       // maximum line count
	) {
		if (n <= 0) n = 1;
		lnx = new int[n+1];
		reset();
	}

	// add a chunk of buffered text for line indexing

	public int record (
		CharArray s,  // text to add for indexing
		int      lm   // line length for wrapping
	) {
		if (s == null)
			return 0;
//		System.out.println("s.length= " + s.length());
		String ss = s.getStringLeft();
//		System.out.println("ss= [" + ss + "]");
		sb.append(ss);
		int ll = ss.length();
//		System.out.println("ncs= " + ncs);
		ncs += ll;
//		System.out.println(ll + " [" + sb + "]");

		int bs = 0;   // at start of text to line out

		int k,n;
		for (k = 0; ll > 0; k++) {

			// check for index array overflow

			if (nln == lnx.length - 1)
				return k;

			// text too long for one display line?

//			System.out.println("A: k= " + k + ", ll= " + ll + ", lm= " + lm);

			if (ll <= lm) {
//				System.out.println("take rest of text as line");
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

//			System.out.println("B: nln= " + nln + ", bs= " + bs + ", ll= " + ll + ", n= " + n);

			bs += n;
			ll -= n;
			if (nln < lnx.length) {
				n += lnx[nln++];  // add previous line offset to line length
				lnx[nln] = n;     // it becomes the next line offset
			}
//			System.out.println("C: ll= " + ll + ", bs= " + bs + ", nln= " + nln);
//			System.out.println("n= " + n + ", lnx[nln]= " + lnx[nln]);
//			dump();
		}

		return k; // number of lines added to index
	}

	// save offset for lined text

	public void skipTo (
		int nl
	) {
		skp = nl;
	}

	// get saved count of lines to skip

	public final int skip (
	) {
		return skp;
	}

	// zero out line index

	public final void reset (
	) {
		lnx[0] = 0;
		lnx[1] = 0;
		nln = 0;
		ncs = 0;
		skp = 0;
		sb  = new StringBuffer();
	}

	// get lining for text from input source

	public final int set (
		Inputs in
	) {
		CharArray data;
		int nra = 0;
		while ((data = in.input()) != null) {
			int nr = record(data,100);
//			System.out.println(nr + " line(s) added, with " + nra + " already");
			nra += nr;
		}
		return nra;
	}

	// total number of lines

	public final int countAll ( ) { return nln; }

	// total number of chars in lined text

	public final int textLength ( ) { return ncs; }

	// actual text being lined

	public final String textString ( ) { return sb.toString(); }

	// index of lines in text

	public final int[] textIndex ( ) { return lnx; }

	// get a copy of the filled out part of line index array

	public final int[] textIndex (
		int nsk
	) {
		if (nsk >= nln)
			return new int[0];
		int[] nlnx = new int[nln-nsk+1];
		System.arraycopy(lnx,nsk,nlnx,0,nlnx.length);
		return nlnx;
	}

	public String toString ( ) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nln; i++) {
			sb.append(lnx[i]);
			sb.append(" ");
		}
		sb.append(lnx[nln]);
		return sb.toString();
	}

	////
	//// for debugging
	////

	// diagnostic dump of entire line index

	public final void dump ( ) {
		String ss = sb.toString();
		System.out.println("== (" + ss.length() + ") [" + ss + "]");
		System.out.println("nln= " + nln + ", ncs= " + ncs + ", skp= " + skp);
		for (int i = 0; i < nln; i++) {
			int ne = lnx[i+1];
			if (ne > ncs) {
				System.err.println("** change segment length: " + ne + " to " + ncs);
				ne = ncs;
			}
			int nn = ne - lnx[i];
//			System.out.println("@" + lnx[i] + ", nn= " + nn);
			String s = (nn > 0) ? ss.substring(lnx[i],ne) : "";
			System.out.println(String.format("%3d @%3d:%3d [%s]",i,lnx[i],nn,s));
		}
	}

	public static void main ( String[] as ) {
		String file = (as.length > 0)? as[0] : "text";
		try {
			FileInputStream in = new FileInputStream(file);
			Inputs stream = new Inputs(in);
			Lines lx = new Lines();
			lx.dump();
			System.out.println("total lines= " + lx.set(stream));
			lx.dump();
		} catch (IOException e) {
			System.err.println(e);
		}
	} 

}
