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
// AW file Vectors.java : 09Apr99 CPM
// class for multiple buffered vectors

package aw;

import java.io.*;

// class for multiple vectors in buffer

public class Vectors extends Vector {

	// load vectors from an input stream
	
	public Vectors (
		DataInput in,
		int   length
	) throws IOException {
		super();
		vl = length;
		vb = new byte[vl];
		in.readFully(vb);
	}

	// set up an empty vector buffer with a byte array
		
	public Vectors (
		byte[] v
	) {
		super(v,0);
	}

	// set up vector buffer with a filled byte array
		
	public Vectors (
		byte[] v,
		int    l
	) {
		super(v,l);
	}

	// get vectors from run information
		
	public Vectors (
		int bn,
		int os,
		int ln
	) throws IOException {
		super(bn,os,ln);
	}
	
}
