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
// AW file Attribute.java : 07Sep99 CPM
// profile attribute definition for I/O

package aw;

import java.io.*;

public class Attribute extends ProfileFile {

	public static final int PKWL = 320; // buffer size for saved keys

	private static final byte VB = 124;
	
	private static final byte[] empty = {
		32,VB,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
		32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32
	};
	
	private static final String file = "attributes"; // where to store attributes
	private static final int    size = 30 + PKWL;    // of attribute record
	
	private static RandomAccessFile ios;

	public Stamp  stm; // time stamp
	public byte[] kys; // stored descriptive keys

	// allocate empty attribute record
	
	public Attribute (
	
	) {
		stm = new Stamp();
		kys = new byte[PKWL];
		System.arraycopy(empty,0,kys,0,PKWL);
	}

	// read in specified attribute record
		
	public Attribute (
	
		int pn
		
	) throws AWException {
		this();
		io = ios;
		super.load(pn);
		ios = io;
	}

	// save attribute record as specified
	
	public void save (
	
		int pn
	
	) throws AWException {
		io = ios;
		super.save(pn);
		ios = io;
	}

	// close file
	
	public void close (
	
	) {
		io = ios;
		super.close();
		ios = null;
	}

	// how to read attribute record
	
	protected void loadF (
	
	) throws IOException {

		stm.load(io);		
		io.readFully(kys);
	
	}
	
	// how to write profile record
		
	protected void saveF (

	) throws IOException {
	
		stm.save(io);
		io.write(kys);
	
	}
	
	// get descriptive keys
	
	public String keys (
	
	) {
		String s = new String(kys);
		return s.trim();
	}
	
	final String nameF ( ) { return file; }
	
	final int    sizeF ( ) { return size; }
	
}