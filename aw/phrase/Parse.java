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
// AW file Parse.java : 28feb2022 CPM
// phrase analysis access class

package aw.phrase;

import aw.BatchFile;
import java.io.*;

public class Parse extends BatchFile {

	public static final String root = "parse"; // for file name
	public static final int    size = 1;       // byte

	private static RandomAccessFile ios = null;

	private static int bns = unDEFINED; // saved batch number
	private static int ns  = unDEFINED; // saved index in file

	private static Parsing save;

	public Parsing analysis; // actual parsing

	// implicit closing

	public static void close (

	) {
		closeIt(ios);
		ios = null;
	}

	// to support subclasses with constructors of different signatures

	public Parse ( ) { }

	// create parsing record

	public Parse (
		Parsing analysis
	) {
		this.analysis = analysis;
		ns = -1;
	}

	// read parsing from file

	public Parse (
		int bn, // batch number
		int n   // index
	) throws IOException {
		if (bns == bn && ns == n) {
			analysis = save;
			return;
		}
		if (bns != bn) {
			bns = bn;
			ios = null;
		}
		access(n);
		if (n >= 0) {
			ns = n;
			save = analysis = new Parsing(ios);
		}
	}

	// write out analysis

	public void save (
		int bn  // batch number
	) throws IOException {
		bns = bn;
		access(unSPECIFIED);
		analysis.save(ios);
		Start sr = new Start(position());
		sr.save(bn);
	}

	// file position for I/O
	
	public static int position (
	
	) throws IOException {
		return (ios == null) ? 0 : (int) ios.getFilePointer();
	}
	
	// accessors

	protected final String rootF ( ) { return root; }
	protected final int    sizeF ( ) { return size; }
	protected final int     bnsF ( ) { return bns; }

	protected final RandomAccessFile iosF ( ) { return ios; }
	protected final void iosF ( RandomAccessFile io ) { ios = io; }

}
