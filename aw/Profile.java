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
// AW file Profile.java : 30May00 CPM
// profile definition with I/O

package aw;

import java.io.*;

public class Profile extends ProfileFile {

	public static final int MXP = 132; // maximum index count in profile
	
	private static final String file = "profiles"; // where to store profiles
	private static final int    size = 16 + 4*MXP; // of profile record
	
	private static RandomAccessFile ios;

	public short  nhth; // minimum lexical hit threshold
	public short  shth; // minimum phonetic hit threshold
	public float  sgth; // minimum significance threshold
	public float  uexp; // expected value for unit vector
	public float  uvar; // variance       for unit vector

	public short[] gms; // n-gram indices
	public byte[]  wts; // n-gram weights
	public byte[]  trc; // post-filter

	// allocate empty profile
	
	public Profile (
	
	) {
		gms = new short[MXP];
		wts = new byte[MXP];
		trc = new byte[MXP];
	}

	// read in specified profile
		
	public Profile (
	
		int pn
		
	) throws AWException {
		this();
		load(pn);
	}

	// load profile as specified
		
	public final void load (
	
		int pn
	
	) throws AWException {
		io = ios;
		super.load(pn);
		ios = io;
	}
	
	// save profile as specified
		
	public final void save (
	
		int pn
	
	) throws AWException {
		io = ios;
		super.save(pn);
		ios = io;
	}
	
	// close file
	
	public final void close (
	
	) {
		io = ios;
		super.close();
		ios = null;
	}

	// how to read profile record
	
	protected void loadF (
	
	) throws IOException {

		nhth = io.readShort();
		shth = io.readShort();
		sgth = io.readFloat();
		uexp = io.readFloat();
		uvar = io.readFloat();

		for (int i = 0; i < MXP; i++)
			gms[i] = io.readShort();
		io.readFully(wts);
		io.readFully(trc);
		
	}
	
	// how to write profile record
		
	protected void saveF (

	) throws IOException {
	
		io.writeShort(nhth);
		io.writeShort(shth);
		io.writeFloat(sgth);
		io.writeFloat(uexp);
		io.writeFloat(uvar);

		for (int i = 0; i < MXP; i++)
			io.writeShort(gms[i]);

		io.write(wts);
		io.write(trc);
		
	}
	
	final String nameF ( ) { return file; }
	
	final int    sizeF ( ) { return size; }
	
}