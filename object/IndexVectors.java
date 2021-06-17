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
// AW file IndexVectors.java : 09Apr99 CPM
// class for multiple buffered vectors

package object;

import aw.AWException;
import aw.Vectors;
import aw.Vector;
import java.io.*;

// higher-level access to index vector buffer

public class IndexVectors extends IndexVector {

	// create a buffer with no initialization
	
	public IndexVectors (
	
	) {
	}
	
	// create an empty vector buffer
	
	public IndexVectors (
		int limit
	) {
		byte[] v = new byte[limit];
		vc = new Vectors(v);
		bb = vc.vb;
	}

	// load vectors from an input stream
	
	public IndexVectors (
		DataInput in,
		int k
	) throws IOException {
		super();
		load(in,k);
		bb = vc.vb;
	}

	// load vectors from run information
	
	public IndexVectors (
		int bn,
		int os,
		int ln
	) throws AWException {
		super();
		try {
			vc = new Vectors(bn,os,ln);
			bb = vc.vb;
		} catch (IOException e) {
			throw new AWException("cannot read vector: ",e);
		}
	}

	// method for reading vectors
			
	public void load (
		DataInput in,
		int k
	) throws IOException {
		vc = new Vectors(in,k);
	}

	// method for writing vectors
		
	public int save (
		DataOutput out
	) throws IOException {
		out.write(vc.vb,0,vc.vl);
		return vc.vl;
	}

	// skip to end of current vector
		
	public void skip (
	) {
		int k = mark;
		for (int i = extent; i < NX; i++)
			k += bb[k] - ZERO;
		reset(k);
	}
	
	// set to allow skip with shallow scanning
	
	public final void position ( ) { mark = base() + VSTART; }

	// get vector buffer
	
	public final byte[] buffer ( ) { return vc.vb; }

	// set buffer size
		
	public final void size ( int n ) { vc.vl = n; }

}
