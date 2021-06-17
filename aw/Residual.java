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
// AW file Residual.java : 15Jun98 CPM
// for management of the residuals file

package aw;

import java.io.*;

public class Residual extends Sequence {

	private static final String file = "residual";
	
	protected RandomAccessFile out;
	
	protected Run rr = new Run(); // addition to residual file

	// load the residual file
	
	public Residual (
	) throws AWException {
		super(file);
	}
	
	// create an empty residual file
	
	public Residual (
		int n  // just to distinguish this method
	) throws AWException {
		super(1);
		try {
			out = new RandomAccessFile(file,"rw");
			out.writeInt(nmssg);
			out.writeInt(nmrun);
		} catch (IOException e) {
			throw new AWException("cannot write residuals");
		}
	}

	// set run for output
	
	public void reset (
		Run rs
	) {
		rr.bbn = rs.bbn;
		rr.ssn = rs.ssn;
		rr.rfo = rs.rfo;
		rr.rln = 0;
	}

	// update residuals file with sequence
	// of items not matching up to now
	
	public int record (
		int ssno, // starting subsegment number of matched item
		int ssix, // index in item of matched subsegment
		int cvo,  // current vector offset
		int nvo   // next vector offset
	) throws AWException{
		int n = 0;
		
		// any preceding unmatched items?
		
		if (ssno > rr.ssn) {
		
			// if so, write out residual run record
			
			rr.nss = (short)(ssno - rr.ssn);
			rr.rln = cvo - rr.rfo;
			
			writeRun();

			// update residual counts
			
			n = rr.nss;
			nmssg += n;
			nmrun++;
			
		}
 
		// reset for next run

		rr.ssn = (short)(ssno + ssix);
		rr.rfo = nvo;
		return n;
	}
	
	// write run record
	
	protected void writeRun (
	) throws AWException {
		try {
			if (out == null) {
				out = new RandomAccessFile(file,"rw");
				out.seek(out.length());
			}
			rr.save(out);
		} catch (IOException e) {
			throw new AWException("cannot write residuals");
		}
	}

	// close out residual file
	
	public void close (
	) throws AWException {
		if (out == null)
			return;
		try {
			out.seek(0L);
			out.writeInt(nmssg);
			out.writeInt(nmrun);
			out.close();
			out = null;
		} catch (IOException e) {
			throw new AWException("cannot close residuals");
		}
	}
	
	// save modified residuals
	
	public void save (
	) throws AWException {
		save(file);
	}
	
}