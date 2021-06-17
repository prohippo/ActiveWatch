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
// AW file Sequence.java : 04Feb99 CPM
// create run sequence file

package aw;

import java.io.*;

public class Sequence {

	public int nmssg; // number of subsegments in sequence
	public int nmrun; // number of run records
	public Run[]   r; // run records

	public int nmr = 0;

	// create an empty sequence
		
	public Sequence (
		int n
	) {
		if (n == 0)
			n = 1;
		nmr = n;
		r = new Run[n];
	}

	// load a sequence from a specified file
		
	public Sequence (
		String file
	) throws AWException {
		RandomAccessFile in;
		
		try {
			in = new RandomAccessFile(FileAccess.to(file),"r");
			
			nmssg = in.readInt();
			nmrun = in.readInt();
			nmr = nmrun;
			r = new Run[nmr];
			for (int i = 0; i < nmrun; i++)
				r[i] = new Run(in);
			in.close();
			
		} catch (IOException e) {
			throw new AWException("cannot load runs");
		}
	}

	// save a sequence to a file
		
	public void save (
		String file
	) throws AWException {
		RandomAccessFile out;
		
		try {
			out = new RandomAccessFile(FileAccess.to(file),"rw");
			out.writeInt(nmssg);
			out.writeInt(nmrun);
			for (int i = 0; i < nmrun; i++)
				r[i].save(out);
			out.close();
		} catch (IOException e) {
			throw new AWException("cannot save runs");
		}
	}

	// add a run record to a sequence
		
	public void addRun (
		int bns,
		int sss,
		int ess
	) throws AWException {
		if (nmrun == nmr)
			throw new AWException("run overflow");
		r[nmrun] = new Run(bns,sss,ess);
		nmssg += r[nmrun++].nss;
	}
	
}