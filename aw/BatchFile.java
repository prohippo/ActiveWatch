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
// AW file BatchFile.java : 27nov2022 CPM
// shared code for random-access AW batch files

package aw;

import java.io.*;

public abstract class BatchFile {

	// saved ID constants < 0

	public static final int unDEFINED   = -32768; // no ID yet saved
	public static final int unSPECIFIED = -1;     // must differ from unDEFINED

	// go to specific record

	protected static void seekIt (
		RandomAccessFile ios, // file
		int size, // record size
		int n     // record number may be >= 0 or unSPECIFIED
	) throws IOException {
		if (n < 0)
			ios.seek(ios.length());
		else {
			int no = n*size;
			if (no >= ios.length())
				throw new EOFException();
			ios.seek(no);
		}
	}

	// open batch file for random access

	protected static RandomAccessFile openIt (
		String root, // file name basis
		int batch,   // batch number
		int n        // record number may be >= 0 or unSPECIFIED
	) throws IOException {
		String fn = FileName.make(root,batch);
		return new RandomAccessFile(fn,(n < 0) ? "rw" : "r");
	}

	// close I/O for entire class

	protected static void closeIt (
		RandomAccessFile ios
	) {
		try {
			if (ios != null) {
				ios.close();
				ios = null;
			}
		} catch (IOException ignore) {
		}
	}

	// count records already in file

	protected static int countIt (
		String root, // file name basis
		int batch,   // batch number
		int size     // record size
	) {
		File f = new File(FileName.make(root,batch));
		int n = (int)f.length()/size;
		return n;
	}

	//////
	//////

	protected RandomAccessFile io; // for reading and writing records

	// access to record in file

	protected void access (
		int n // record number may be >= 0 or unSPECIFIED
	) throws IOException {
		io = iosF();	
		if (io == null) {
			io = openIt(rootF(),bnsF(),n);
			iosF(io);
		}
		seekItX(sizeF(),n);
	}

	// to allow class methods to be overridden in effect

	protected void seekItX (
		int sz, // size of record
		int n   // record number may be >= 0 or unSPECIFIED
	) throws IOException {
		seekIt(io,sz,n);
	}

	protected RandomAccessFile createItX (
		String r, // file name root
		int b     // batch number
	) throws IOException {
		return openIt(r,b,unSPECIFIED);
	}

	// must be defined by subclasses

	public abstract void save ( int bn ) throws IOException;

	protected abstract String rootF ( ); // root for subclass file name
	protected abstract int sizeF    ( ); // size of record for subclass
	protected abstract int bnsF     ( ); // saved batch number for subclass

	protected abstract RandomAccessFile iosF ( );
	protected abstract void iosF ( RandomAccessFile io );

}
