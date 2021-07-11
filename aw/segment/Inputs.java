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
// AW file Inputs.java : 03Jul2021 CPM
// special line buffering for reading ASCII text files
// in DOS, Unix, or Macintosh formats on Internet

package aw.segment;

import aw.CharArray;
import web.*;
import java.io.*;

public class Inputs implements Runnable {

	// the Inputs class is needed because a line read by standard I/O may omit
	// characters like the \r at the end of lines in DOS text files

	private static final byte HT =  9;   // ASCII horizontal tab
	private static final byte LF = 10;   // ASCII linefeed
	private static final byte CR = 13;   // ASCII carriage return
	private static final byte SP = 32;   // ASCII space
	private static final byte NUL=  0;
	private static final byte HI =  0;   // for Ünicode conversion
	
	private static final int LL= 2048;   // maximum line length
	private static final int BL= 3*LL;   // maximum amount in buffer

	private InputStreamReader data; // the text source

	protected char[] buffer = new char[BL + LL + 2];
	protected int bl; // where to stop in buffer
	protected int bb; // saved beginning of line

	private int saveposition; // record start of a segment
	private int backposition; // for backing up on match
	private boolean backup;

	private int thisposition; // current character offset in text file
	private int thislength;   // line length
	
	protected int ll=LL;      // maximum line length to get
	
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

		thisposition = 0;
		thislength   = 0;
		backup = false;
		line = new CharArray(buffer.length);

		bb = 0;
	}
	
	// required for Runnable, executes read in separate thread
	
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
	
	private static final int delay = 60; // seconds
	
	private void refill (
	
	) {
		// is buffer getting low?

		int bs = bb + thislength;
		int k  = bl - bs;
		if (k >= ll)
			return;

		// if so, realign text in buffer without previous line

		bl = k;
		if (bl > 0)
			System.arraycopy(buffer,bs,buffer,0,bl);

		// fill buffer out as needed with new text

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
		nb -= count;

		// reset buffer parameters and add sentinel
		
		bb = 0;
		buffer[bl] = NUL;
		
		// note end of file if buffer not full

		if (bl < BL + LL)
			ll = -1;

		// adjust length because previous line not copied
		
		thisposition += thislength;
		thislength = 0;
	}

	// find next line in buffer
	
	private int markLine (
		int bs  // where to start in buffer
	) {
		int bp; // index of last char in line

		// reset start and scan to first LF or CR
				
		for (bp = bb = bs; bs < bl; bs++) {
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
			
			if (buffer[bs] == 0)
				buffer[bs] = SP;
		}
		
		// actual number of bytes scanned for line
		
		int n = (bs < bl) ? 1 : 0;
		thislength = bs - bb + n;
			
		// include any final LF in line to be marked off

		if (buffer[bp] != LF)
			return thislength;
		else
			return bp - bb + n;
	}
	
	// return next line in buffer
	
	public CharArray input (
	
	) {
		int bs,ss;

		refill();

		thisposition += thislength;
		ss = bs = bb + thislength;

		// skip over initial NULs

		for (; bs < bl; bs++)
			if (buffer[bs] != NUL)
				break;

		// if buffer is exhausted, return nothing
		
		thisposition += bs - ss;
		if (bs >= bl) {
			bb = bs;
			thislength = 0;
			return null;
		}

		// return a line of text as a String

		int n = markLine(bs);
		line.fillChars(buffer,bs,n);
		return line;
	}

	// get line offset in text file
	
	public int position (
		int     set // side effects
	) {
		int pos;

		if (set < 0)

			// get position with no side effects
			
			return thisposition;

		if (backup) {

			// set to previous position
			
			pos = backposition;
			backup = false;
			
		}
		else {

			// set to start of last input line
			
			pos = thisposition;
				
		}

		if (set > 0)

			// record position to start segment
			
			saveposition = pos;

		return pos;
	}

	// start of next line

	public final int nextPosition (
	
	) {
		return thisposition + thislength;
	}

	// total length of segment to end of next line

	public final int length (
	
	) {
		return thisposition - saveposition + thislength;
	}

	// mark position for possible later backup

	public final void reposition (
		int pos
	) {
		backup = true;
		backposition = pos;
	}
	
}

