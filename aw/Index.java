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
// AW file Index.java : 05dec2022 CPM
// text index record class

package aw;

import java.io.*;

public class Index extends BatchFile {

	public  static final String root = "index"; // for file names
	public  static final int    size = 20;      // of index record

	private static RandomAccessFile ios = null;

	private static int bns = unDEFINED; // saved batch number
	private static int ins = unDEFINED; // last item number referenced

	private static Index save;

	public  static void close ( ) {
		closeIt(ios);
		ios = null;
	}

	public int   os; // offset in text file
	public short si; // text file source ID
	public short se; // segment number for divided text
	public int   sx; // subsegment index
	public short ns; // subsegment count
	public short sj; // subject line
	public short hs; // header size
	public short tl; // text length

	// empty constructor

	public Index (

	) {
	}

	// get nth record from index file

	public Index (
		int bn,
		int in
	) throws IOException {
		if (bns == bn && ins == in) {
			os = save.os;
			si = save.si;
			se = save.se;
			sx = save.sx;
			ns = save.ns;
			sj = save.sj;
			hs = save.hs;
			tl = save.tl;
			return;
		}
		if (bns != bn) {
			bns = bn;
			ios = null;
		}
		access(in);
		if (in >= 0) {	
			ins = in;
			load(ios);
			save = this;
		}
	}

	// read record fields

	public void load (
		DataInput in
	) throws IOException {
		os = in.readInt();
		si = in.readShort();
		se = in.readShort();
		sx = in.readInt();
		ns = in.readShort();
		sj = in.readShort();
		hs = in.readShort();
		tl = in.readShort();
	}

	// write out index record

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
		out.writeInt(os);
		out.writeShort(si);
		out.writeShort(se);
		out.writeInt(sx);
		out.writeShort(ns);
		out.writeShort(sj);
		out.writeShort(hs);
		out.writeShort(tl);
	}

	// get record count

	public static int count (
		int bn // batch number
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
