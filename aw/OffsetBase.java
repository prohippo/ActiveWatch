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
// OffsetBase.java : 17Apr99 CPM
// generic offset to variable-length batch object

package aw;

import java.io.*;

public abstract class OffsetBase extends BatchFile {

	public static final int size = 4; // of offset record
	
	public int offset; // current file offset value

	// new offset record
	
	public OffsetBase (
		int o  // offset in file
	) {
		offset = o;
	}

	// read offset from file
		
	public OffsetBase (
		int bn, // batch number
		int in  // item  number
	) throws IOException {
		int bns = bnsF();
		int ins = insF();
		if (bns == bn && ins == in) {
			offset = offsetsF();
			return;
		}
		if (bns != bn)
			bnsF(bn);
		access(in);
		if (in >= 0) {
			insF(in - 1);
			load();
		}
	}
	
	// write field of record
	
	public void save (
		DataOutput out
	) throws IOException {
		out.write(offset);
	}
	
	// accessors
	
	protected final int sizeF ( ) { return size; }
	
	// still need to define these methods
	
	protected abstract int  insF ( );
	protected abstract void insF ( int n );
	protected abstract void bnsF ( int b );
	protected abstract int offsetsF ( );
	public abstract int load ( ) throws IOException;
	
}