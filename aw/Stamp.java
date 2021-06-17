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
// AW file Stamp.java : 20Oct97 CPM
// for time stamp and related statistics for profiles

package aw;

import java.io.*;

public class Stamp {

	public long  cdate; // date of last change
	public long  mdate; // date of last match
	public long  rdate; // date of last review
	public short nrnew; // number of new matches
	public short nrold; //           old
	public short genum; // generation number

	// read stamp from file
	
	public void load (
	
		RandomAccessFile io
		
	) throws IOException {

		cdate = io.readLong();
		mdate = io.readLong();
		rdate = io.readLong();
		nrnew = io.readShort();
		nrold = io.readShort();
		genum = io.readShort();
	
	}
	
	// write stamp to file
	
	public void save (

		RandomAccessFile io
		
	) throws IOException {
	
		io.writeLong(cdate);
		io.writeLong(mdate);
		io.writeLong(rdate);
		io.writeShort(nrnew);
		io.writeShort(nrold);
		io.writeShort(genum);
		
	}
}
