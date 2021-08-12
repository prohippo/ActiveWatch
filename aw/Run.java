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
// AW file Run.java : 11aug2021 CPM
// create run sequence file

package aw;

import java.io.*;

public class Run {
	static final int size = 14;

	public short bbn; // batch number for run
	public short ssn; // starting subsegment number
	public short nss; // number of subsegments
	public int   rfo; // offset of first vector in run
	public int   rln; // number of vector bytes

	// create an empty run

	public Run (
	) {
	}

	// create run record from subsegment sequence

	public Run (
		int bns,
		int sss,
		int ess
	) throws AWException {
		try {
			Offset oo = new Offset(bns,sss);
			Offset on = new Offset(bns,ess);

			bbn = (short) bns;
			ssn = (short) sss;
			nss = (short) (ess - sss);
			rfo = oo.offset;
			rln = on.offset - oo.offset;
		} catch (IOException e) {
			System.err.println(e);
			String x = "bns=" + bns + ", sss=" + sss + ", ess=" + ess;
			throw new AWException("no offsets - " + x);
		}
	}

	// load run record sequentially from a file

	public Run (
		RandomAccessFile io
	) throws IOException {
		load(io);
	}

	// run record input

	public void load (
		RandomAccessFile in
	) throws IOException {
		bbn = in.readShort();
		ssn = in.readShort();
		nss = in.readShort();
		rfo = in.readInt();
		rln = in.readInt();
	}

	// run record output

	public void save (
		RandomAccessFile out
	) throws IOException {
		out.writeShort(bbn);
		out.writeShort(ssn);
		out.writeShort(nss);
		out.writeInt(rfo);
		out.writeInt(rln);
	}

}
