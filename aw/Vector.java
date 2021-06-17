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
// AW file Vector.java : 17Apr99 CPM
// vector file access class

package aw;

import java.io.*;

public class Vector extends BatchFile {

	public static final String root = "vector"; // for file name
	public static final int    size = 0;        // not used
	
	private static RandomAccessFile ios = null;
	
	private static int bns = unDEFINED; // saved batch number
	private static int vos = unDEFINED; // saved offset in file
	
	private static Vector save;
	
	public static  void close ( ) {
		closeIt(ios);
		ios = null;
	}

	public byte[] vb; // vector buffer
	public int    vl; // actual length

	// for subclasses with constructors of different signatures
		
	protected Vector (
	
	) {
	}
	
	// fill vector buffer
	
	public Vector (
		byte[] v,
		int    l
	) {
		vb = v;
		vl = l;
		vos = -1;
	}

	// read vector from file
		
	public Vector (
		int bn, // batch number
		int vo, // offset
		int vn  // length
	) throws IOException {
		if (bns == bn && vos == vo) {
			vb = save.vb;
			vl = save.vl;
			return;
		}
		if (bns != bn) {
			bns = bn;
			ios = null;
		}
		if (vo < 0)
			return;
		vos = vo;
		vl  = vn;
		vb  = new byte[vl];
		access(0);
		ios.seek(vo);
		ios.readFully(vb);
		save = this;
	}

	// write out vector

	public void save (
		int bn  // batch number
	) throws IOException {
		bns = bn;
		access(unSPECIFIED);
		ios.write(vb,0,vl);
	}
	
	// write out vector to designated output
	
	public int save (
		DataOutput out
	) throws IOException {
		out.write(vb,0,vl);
		return vl;
	}
	
	// file position for I/O
	
	public static int position (
	
	) throws IOException {
		return (ios == null) ? 0 : (int) ios.getFilePointer();
	}
	
	// current file length
	
	public static int length (
		int bn  // batch number
	) {
		return countIt(root,bn,1);
	}
	
	// accessors
	
	protected final String rootF ( ) { return root; }
	protected final int  sizeF ( ) { return size; }
	protected final int  bnsF ( ) { return bns; }
	
	protected final RandomAccessFile iosF ( ) { return ios; }
	protected final void iosF ( RandomAccessFile io ) { ios = io; }
	
}