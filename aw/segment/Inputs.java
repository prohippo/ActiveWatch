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
// AW file Inputs.java : 01aug2021 CPM
// special line buffering for reading UTF-8 text files
// in DOS, Unix, or Macintosh formats on Internet

package aw.segment;

import aw.ByteTool;
import aw.CharArray;
import web.*;
import java.io.*;
import java.util.*;

public class Inputs implements Runnable {

	// the Inputs class is needed because a line read by standard I/O may omit
	// characters like the \r at the end of lines in DOS text files

	private static boolean track = false;

	private static final byte HT =  9;   // ASCII horizontal tab
	private static final byte LF = 10;   // ASCII linefeed
	private static final byte CR = 13;   // ASCII carriage return
	private static final byte SP = 32;   // ASCII space

	private static final int LL= 2048;   // maximum line length
	private static final int BL= 3*LL;   // maximum amount in buffer

	private InputStreamReader data; // the text source to read

	protected char[] buffer = new char[BL + LL + 2];
	protected int bl; // where data ends in buffer
	protected int bb; // saved beginning of next line

	private int saveposition; // record start of a segment
	private int backposition; // for backing up on match
	private int byteposition; // current byte offset in text file
	private boolean backup;

	private int charlength;   // line length in chars
	private int bytelength;   //             in UTF-8 bytes originally

	protected int ll=LL;      // maximum line length to get, also E-O-F flag

	private CharArray line;   // input line as object to return

	// constructor

	public Inputs (
		InputStream in // text file for line buffering
	) {
		try {
			data = new InputStreamReader(in,"UTF8");
		} catch (UnsupportedEncodingException e) {
			System.err.println(e);
			data = null;
		}

		byteposition = 0;
		bytelength   = 0;
		charlength   = 0;
		backup = false;
		line = new CharArray(LL);

		bb = bl = 0;  // buffer starts empty
	}

	// required for Runnable, executes a read in separate thread

	private int length; // how many bytes to read

	public void run (

	) {
		try {
			length = data.read(buffer,bl,length);
		} catch (IOException e) {
			length = -1;
		}
	}

	// load buffer from text input

	private static final int delay = 10; // seconds to wait

	private void refill (

	) {
		// is buffer of chars getting low?

		int k  = bl - bb;
		if (k >= ll)
			return;
		if (track)
			System.out.println("refill k=" + k + ", ll=" + ll);

		// if so, move any remaining text to front of buffer

		if (k > 0) {
			System.arraycopy(buffer,bb,buffer,0,k);
			if (track) {
				int nc = (k < 36) ? k : 36;
				String bcs = new String(buffer,0,nc);
				System.out.println("refilled: buffer= [" + bcs + "]");
				System.out.println(k + " chars");
			}
		}
		bb = 0;
		bl = k;

		// fill out buffer with fresh text

		int nb = BL + LL - bl;

		int count = nb;
		do {
			length = count;
			ThreadTimer tmr = new ThreadTimer(this,delay);
			tmr.run();
			if (length > 0) {
				bl += length;
				count -= length;
			}
		} while (count > 0 && length > 0);

		// add sentinel

		buffer[bl] = '\0';

		if (track)
			System.out.println("bb=" + bb + ", bl=" + bl);

		// note end of file if buffer not full

		if (bl < BL + LL)
			ll = -1;
	}

	// find next line in buffer

	private int markLine (

	) {
		int bp; // index of last char in line
		int bs = bb;

		// reset start and scan to first LF or CR

		for (bp = bb; bs < bl; bs++) {
			if (buffer[bs] == LF) {
				bp = bs;
				break;
			}

			if (buffer[bs] == CR) {
				buffer[bs] =  LF;

				// handle possible DOS text file with multiple CR's followed by LF

				for (bp = bs + 1;; bp++) {
					if (buffer[bp] == LF) {
						int sp = bs; bs = bp; bp = sp;
						break;
					}
					else if (buffer[bp] != CR) {
						bp = bs;
						break;
					}
				}
				break;
			}

			// convert any embedded nulls to spaces

			if (buffer[bs] == '\0')
				buffer[bs] = SP;
		}

		// number of chars scanned for line plus any final LF
		// (often less that number of UTF-8 bytes scanned!)

		int n = (bs < bl) ? 1 : 0;
		int nscan = bs - bb + n;

		// easiest way to find UTF-8 byte count for line
		char[] cs = Arrays.copyOfRange(buffer,bb,bb+nscan);
		bytelength = ByteTool.utf8length(cs);  // in original UTF-8 input bytes

		if (track)
			System.out.println("charl=" + charlength + ", bytel=" + bytelength); 

		charlength = nscan;

		return charlength;
	}

	// return next line in buffer

	public CharArray input (

	) {
		int k = 0;

		byteposition += bytelength;

		refill();

		// skip over initial NULs

		for (; bb < bl; bb++, k++)
			if (buffer[bb] != '\0')
				break;

		// if buffer is exhausted, return nothing

		if (bb >= bl) {
			charlength = 0;
			bytelength = 0;
			return null;
		}

		// return a line of text as a Line object

		int n = markLine();
		int bs = bb;
		bb += charlength;
		if (track) {
			System.out.println("READ position=" + byteposition);
			System.out.println("k=" + k);
		}
		bytelength += k;
		line.fillChars(buffer,bs,n);
		return line;
	}

	public int getCharLength ( ) { return charlength; }

	//
	// these methods use byte offsets into a UTF-8 text input file
	// to make processing positions
	//
	// get line offset in text file

	public int position (
		int     set // side effects
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

	public final int nextPosition (

	) {
		if (track)
			System.out.println("NEXT position=" + byteposition);
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

}

