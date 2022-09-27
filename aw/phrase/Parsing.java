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
// Parsing.java : 08sep2022 CPM
// for reading and writing phrase analyses
// from and to files

package aw.phrase;

import java.io.*;

public class Parsing {

	public static final byte Empty =  127;
	public static final byte Pad   = -128;
	public static final byte Phrase    = -4; // component codes
	public static final byte Sentence  = -3;
	public static final byte Paragraph = -2;

	short  count;  // of phrases
	int    length; // of analysis buffer in bytes
	byte[] buffer; // analysis itself

	// initialize from stream

	public Parsing (

		DataInput in

	) throws IOException {
		load(in);
	}

	// initialize from array

	public Parsing (

		int     count,
		int    length,
		byte[] buffer

	) {
		this.count  = (short) count;
		this.length = length;
		this.buffer = buffer;
	}

	// write parsing to output stream

	public void save (

		DataOutput out

	) throws IOException {
		out.writeShort(count);
		out.writeInt  (length);
		out.write(buffer,0,length);
	}

	// read parsing from input stream

	public void load (

		DataInput in

	) throws IOException {
		count  = in.readShort();
		length = in.readInt  ();
//		System.out.println("load: count= " + count + ", length= " + length);
		buffer = new byte[length];
		in.readFully(buffer,0,length);
	}

	// access from outside package

	public final int getCount ( ) { return count; }

	public final int getLength ( ) { return length; }

	public final byte[] getBuffer ( ) { return buffer; }

	private static final int L = 32; // how many initial parse bytes to show
	private static final int M = 16; // how many final   parse bytes

	public String toString ( ) {
		String s = " " + count + " phrases in " + length + " bytes:\n";
		int ln = (length < L) ? length : L;
		for (int i = 0; i < ln; i++)
			s += String.format("%02x ",buffer[i]);
		int ll = length - ln;
		if (ll > 0) {
			s += "...";
			if (ll > M) ll = M;
			for (int j = length - ll; j < length; j++)
				s += String.format(" %02x",buffer[j]);
		}
		return s;
	}
}
