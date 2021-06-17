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
// AW file Source.java : 07Sep99 CPM
// source file name record class

package aw;

import java.io.*;

public class Source extends BatchFile {

	public  static final String root = "source"; // for file name
	public  static final int    size = 128;      // of source record
	
	private static RandomAccessFile ios = null;
	
	private static int bns = unDEFINED; // saved batch number
	private static int ids = unDEFINED; // saved source id in batch

	private static byte[] name = new byte[size]; // for file name

	public  static void close ( ) {
		closeIt(ios);
		ios = null;
	}
	
	// compose source record from name string
	
	public Source (
		String s // URL string to store
	) {
		if (s.length() > size)
			s = s.substring(0,size);
		int ln = s.length();
		byte[] ba = s.getBytes();
		System.arraycopy(ba,0,name,0,ln);
		for (int i = ln; i < size; i++)
			name[i] = 32; // blank fill
		ids = -1;
	}

	// read source record from file
		
	public Source (
		int bn, // batch number
		int id  // index number
	) throws IOException {
		if (ids == id && bns == bn)
			return;
		if (bns != bn) {
			bns = bn;
			ios = null;
		}
		access(id);
		if (id >= 0) {
			ids = id;
			ios.readFully(name);
		}
	}

	// convert source record to name string
		
	public String getName (
	
	) {	
		String s = new String(name);
		return s.trim();
	}

	// write out source record
		
	public void save (
		int bn  // batch number
	) throws IOException {
		bns = bn;
		access(unSPECIFIED);
		ios.write(name);
	}

	// get record count
	
	public static int count (
		int bn
	) {
		return countIt(root,bn,size);
	}

	// accessors
	
	protected final String rootF ( ) { return root; }
	protected final int  sizeF ( ) { return size; }
	protected final int  bnsF ( ) { return bns; }
	
	protected final RandomAccessFile iosF ( ) { return ios; }
	protected final void iosF ( RandomAccessFile io ) { ios = io; }
	
}