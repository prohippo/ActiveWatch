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
// AW file Subsegment.java : 17Apr99 CPM
// subsegment record of text item

package aw;

import java.io.*;

public class Subsegment extends BatchFile {

	public  static final String root = "segmn"; // for file names
	public  static final int    size = 10;      // of source record
	
	private static RandomAccessFile ios = null;
	
	private static int  bns = unDEFINED; // saved batch number
	private static int  sis = unDEFINED; // saved subsegment index
	
	private static Subsegment save;

	public  static void close ( ) {
		closeIt(ios);
		ios = null;
	}
	
	public int   it; // item number
	public short sn; // subsegment number in item
	public short so; // subsegment offset in item
	public short ln; // subsegment length

	// read record from file
	
	public Subsegment (
		int bn, // batch number
		int si  // subsegment in batch
	) throws IOException {
		if (bns == bn && sis == si) {
			it = save.it;
			sn = save.sn;
			so = save.so;
			ln = save.ln;
			return;
		}
		if (bns != bn) {
			bns = bn;
			ios = null;
		}
		access(si);
		if (si >= 0) {
			sis = si;
			load(ios);
			save = this;
		}
	}
	
	// create new record
	
	public Subsegment (
	
	) {
	}
	
	// read record fields
	
	public void load (
		DataInput in
	) throws IOException {
		it = in.readInt();
		sn = in.readShort();
		so = in.readShort();
		ln = in.readShort();
	}
	
	// save record to file

	public void save (
		int bn  // batch number
	) throws IOException {
		bns = bn;
		access(unSPECIFIED);
		save(ios);
	}
	
	// write record fields
	
	public void save (
		DataOutput out
	) throws IOException {
		out.writeInt(it);
		out.writeShort(sn);
		out.writeShort(so);
		out.writeShort(ln);
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