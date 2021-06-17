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
// AW file Logic.java : 18Feb98 CPM
// for logical dependence of classification profiles

package aw;

import java.io.*;

public class Logic extends ProfileFile {

	public  static final int NWY = 4; // maximum dependencies for logic
	
	private static final String file = "logics";  // where to store attributes
	private static final int    size = 4 + 2*NWY; // of attribute record
	
	private static RandomAccessFile ios;

	public byte  nil; // =1 for NULL profile
	public byte  tmp; // =0 to maintain hit list
	public byte  ncj; // conjunctive dependents
	public byte  nds; // disjunctive
	public short[] depend; // dependencies

	// allocate empty attribute record
	
	public Logic (
	
	) {
		depend = new short[NWY];
	}

	// read in specified logic record
		
	public Logic (
	
		int pn
		
	) throws AWException {
		this();
		io = ios;
		super.load(pn);
		ios = io;
	}

	// save logic record as specified
	
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

	// how to read logic record

	protected void loadF (
	
	) throws IOException {

		nil = io.readByte();
		tmp = io.readByte();
		ncj = io.readByte();
		nds = io.readByte();

		for (int i = 0; i < NWY; i++)
			depend[i] = io.readShort();	
	}
	
	// how to write logic record
		
	protected void saveF (

	) throws IOException {
	
		io.writeByte(nil);
		io.writeByte(tmp);
		io.writeByte(ncj);
		io.writeByte(nds);

		for (int i = 0; i < NWY; i++)
			io.writeShort(depend[i]);	
	
	}
	
	final String nameF ( ) { return file; }
	
	final int    sizeF ( ) { return size; }
	
}