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
// AW file Inputs.java : 05dec2022 CPM
// special line buffering for reading UTF-8 text files
// in DOS, Unix, or pre-Darwin Macintosh text formats

package aw;

import aw.ByteTool;
import aw.CharArray;
import web.*;
import java.io.*;
import java.util.*;

public class Inputs {

	// the Inputs class is needed because a line read by standard I/O may omit
	// characters like the \r at the end of lines in DOS text files

	private static boolean track = false;

	private static final byte HT =  9;   // horizontal tab
	private static final byte LF = 10;   // linefeed
	private static final byte CR = 13;   // carriage return
	private static final byte SP = 32;   // space

	private static final int LL= 2048;   // maximum line buffer length

	private BufferedInputStream data;    // the text source to read

	private byte[] bb = new byte[LL + 2];
	private int bp;           // buffer pointer
	private int bl;           // where data ends in buffer

	// text file is assumed to be UTF-8 with at most 2**31 bytes

	private int saveposition; // record start of a segment in text file
	private int backposition; // for backing up on match
	private int byteposition; // current byte offset
	private boolean backup;

	private int charlength;   // line length in Unicode chars
	private int bytelength;   //             in UTF-8 bytes originally

	private byte saved;       // leftover char from last getting last line

	private String  string;   // input line as String
	private CharArray line;   // input line as object to return

	// constructor

	public Inputs (
		InputStream in // text file for line buffering
	) {
		data = new BufferedInputStream(in);
		line = new CharArray(LL);

		byteposition = 0;
		bytelength   = 0;
		charlength   = 0;
		backup = false;
		saved = 0;
		string = "";
		bp = 0;  // line buffer starts empty
		bl = LL;
	}

	// collect next line in buffer

	private int markLine (

	) {
		if (string == null) // end of stream flag
			return 0;

		if (track)
			System.out.println("markLine: + " + bytelength);

		byteposition += bytelength;
		bytelength = 0;
		int skips = 0;      // count of bytes not returned in input line

		bp = 0;             // set up line buffer for next input
		if (saved != 0) {
			bb[bp++] = saved;  // initialize with any byte left from last line
			saved = 0;
		}

		// scan data stream to first LF or CR, and skip extra CRs

		int nb = 0;         // for next byte from stream (returned as int)
		for (; bp < bl; bp++) {
//			System.out.println("bp= " + bp);
			try {
				nb = data.read();
			} catch (IOException e) {
				System.err.println(e);
				System.exit(1);
			}
//			System.out.println("loop nb= [" + nb + "]");
			if (nb == -1 || nb == LF)
				break;

			if (nb == CR) {
				skips++;
				nb = LF;     // handles old pre-DARWIN text files

				// DOS text file may have multiple CR's followed by LF

				int nnb = 0; // next byte in stream
				for (; bp < bl; bp++) {
					try {
						nnb = data.read();
					} catch (IOException e) {
						System.err.println(e);
						System.exit(1);
					}
					if (nnb == LF)
						break;
					else if (nnb != CR) {
						saved = (byte) nnb;
						break;
					}
					skips++; // count up skipped CRs
				}
				break;
			}

			if (nb == 0)
				nb = SP;      // convert any embedded null to space
			bb[bp] = (byte) nb;   // store byte in line buffer
//			System.out.println("add nb=[" + nb + "]");
		}

		if (track)
			System.out.println("nb= " + nb);

		if (nb != 0 && nb != -1)
			bb[bp++] = (byte) nb; // line terminater

		if (bp == 0) {                // end of stream?
			string = null;
			return 0;
		}

		try {
			string = new String(bb,0,bp,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.err.println(e);
			System.exit(1);
		}

		if (track)
			System.out.println("string=" + string);

		bytelength = bp + skips;
		charlength = string.length();

		if (track)
			System.out.println("charl=" + charlength + ", bytel=" + bytelength); 

		return charlength;
	}

	// return next line in buffer

	public CharArray input (

	) {
		if (track)
			System.out.println("READ position=" + byteposition);

		// return a line of text as a CharArray object

		int n = markLine(); // collect next line of input text

		if (string == null) // check for end of input stream
			return null;

		line.fillChars(string.toCharArray(),0,n);
		return line;
	}

	public int getCharLength ( ) { return charlength; }

	// get line offset in text file

	//
	// these methods use byte offsets into a UTF-8 text input file
	// to allow positioning in input text file
	//
	// get line offset in text file

	public int position (
		int set // side effects
	) {
		int pos;

		if (track)
			System.out.println("HERE position=" + byteposition);

		if (set < 0)

			// get position with no side effects

			return byteposition;

		if (backup) {

			// set to previous position

			pos = backposition;
			backup = false;

		}
		else {

			// set to start of last input line

			pos = byteposition;

		}

		if (set > 0)

			// record position to start segment

			saveposition = pos;

		return pos;
	}

	// start of next line

	public final int nextposition (

	) {
		if (track) {
			System.out.print  ("NEXT position=" + byteposition);
			System.out.println(" + " + bytelength);
		}
		return byteposition + bytelength;
	}

	// total length of segment to end of next line

	public final int length (

	) {
		return byteposition - saveposition + bytelength;
	}

	// mark position for possible later backup

	public final void reposition (
		int pos
	) {
		backup = true;
		backposition = pos;
	}

	////////
	//////// for debugging

	public static void main ( String[] a ) {

		CharArray line;
		String file = (a.length > 0) ? a[0] : "text";
		track = true;
		System.out.println("testing Inputs");
		try {
			FileInputStream in = new FileInputStream(file);
			Inputs inp = new Inputs(in);
			line = inp.input();
			System.out.println("> " + line);
			line = inp.input();
			System.out.println("> " + line);
			line = inp.input();
			System.out.println("> " + line);
			line = inp.input();
			System.out.println("> " + line);
			in.close();
			System.out.println("@" + inp.nextposition());
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}

