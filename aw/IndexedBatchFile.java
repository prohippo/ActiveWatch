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
// AW file IndexedBatchFile.java : 24jan2022 CPM
// access to batch files with variable length records

package aw;

import java.io.*;

public abstract class IndexedBatchFile extends BatchFile {

	private int length;    // computed length of variable record
	private OffsetBase or; // offset record

	// to handle index record
	
	public void save (
		int bn,
		long o
	) throws IOException {
		or.offset = (int) o;
		or.save(bn);
	}
	
	// override class methods through instance methods
	
	protected void seekItX (
		int sz,
		int no
	) throws IOException {
		or = offsetRecord(bnsF(),no);
		io.seek(or.offset);
	}
	
	protected RandomAccessFile createItX (
		String r,
		int b
	) throws IOException {
		or = offsetRecord();
		return super.createItX(r,b);
	}

	// no count available
	
	public static int count (
	) {
		return 0;
	}
	
	// for getting offset record of right type (must override these)
	
	public abstract OffsetBase offsetRecord ( int bn, int n ) throws IOException;
	public abstract OffsetBase offsetRecord (               );

}

