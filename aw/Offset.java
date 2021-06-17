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
// AW file Offset.java : 17Apr99 CPM
// vector file offset record class

package aw;

import java.io.*;

public class Offset extends OffsetBase {

	public  static final String root = "offset"; // for file name
	
	private static RandomAccessFile ios = null;
	
	private static int bns = unDEFINED; // saved batch number
	private static int ins = unDEFINED; // saved item index in batch
	
	private static       int offsets;   // saved offset
	private static final int zero = 0;  // for first offset
	
	// create new record
	
	public Offset ( int o ) {
		super(o);
	}
	
	// load record from file
	
	public Offset ( int b, int n ) throws IOException {
		super(b,n);
	}
		
	// close file
	
	public static void close ( ) {
		closeIt(ios);
		ios = null;
	}

	// get next offset from file
	
	public int load (
	
	) throws IOException {
		offset = ios.readInt();
		offsets = offset;
		ins++;
		return offset;
	}
	
	// write out offset record
		
	public void save (
		int bn  // batch number
	) throws IOException {
		bns = bn;
		access(unSPECIFIED);
		if (ios.getFilePointer() == 0)
			ios.writeInt(zero);
		ios.writeInt(offset);
	}
	
	// get record count
	
	public static int count (
		int bn  // batch number
	) {
		return countIt(root,bn,size);
	}

	// accessors
	
	protected final String rootF ( ) { return root; }
	protected final int  insF ( ) { return ins; }
	protected final void insF ( int n ) { ins = n; }
	protected final int  bnsF ( ) { return bns; }
	protected final void bnsF ( int b ) { bns = b; ios = null; }
	
	protected final int offsetsF ( ) { return offsets; }
	
	protected final RandomAccessFile iosF ( ) { return ios; }
	protected final void iosF ( RandomAccessFile io ) { ios = io; }
	
}