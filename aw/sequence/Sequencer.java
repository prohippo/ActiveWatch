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
// AW file Sequencer.java : 16May00 CPM
// create run sequence file

package aw.sequence;

import aw.*;
import java.io.*;

public class Sequencer {

	private static final int M = 8192; // default maximum to cluster in a batch
	
	public Sequence sq = new Sequence(1);
	
	Control co = new Control();

	// initialization
		
	public Sequencer (
	
	) throws AWException {
	
		if (co.nobs == 0)
			throw new AWException("no batches found");
			
	}

	// set up a sequence file with a specified maximum item count
		
	public void run (
		int maximum
	) throws AWException {
	
		System.out.println("encoding up to " + maximum + " in last batch processed");

		// get number of the batch to put into a sequence
		
		int bn = co.last();
		
		// get limit on subsegment numbers in batch
		
		int no = co.getBatchCount(bn);
		int in = no - 1;

		Index ix = access(bn,in);
		int end = ix.sx + ix.ns;

		// enforce maximum for clustering
		
		int start = 0;
		
		if (no > maximum) {
			ix = access(bn,no - maximum);
			start = ix.sx;
		}

		ix.close();
		
		// get range of subsegments to include
				
		sq.addRun(bn,start,end);

		// write out sequence
				
		sq.save("sequence");
		
	}
	
	// set a sequence to an entire batch up to the default maximum
	
	public void run (
	
	) throws AWException {
		run(M);
	} 

	// set a specified sequence
	
	public void run (
		String r
	) throws AWException {
	
		int n = r.indexOf('-');
		
		if (n < 0) {
		
			// single-vector run
			
			Item ita = Reference.to(r);
			int  nsg = Reference.count();
			sq.addRun(ita.bn,ita.xn,ita.xn + nsg);
			
		}
		else {
		
			// run included between two references
			
			Item ita = Reference.to(r.substring(0,n));
			Item itb = Reference.to(r.substring(n+1));
			if (ita.bn != itb.bn)
				throw new AWException("run must be in one batch");
				
			Subsegment  ss;
			Index       ix;
			int start,stop;
			try {
			
				// get full run with all associated subsegments
				
				ss = new Subsegment(ita.bn,ita.xn);
				ix = new Index(ita.bn,ss.it);
				start = ix.sx;
				ss = new Subsegment(itb.bn,itb.xn);
				ix = new Index(itb.bn,ss.it);
				stop  = ix.sx + ix.ns;
				ss.close();
				ix.close();
			} catch (IOException e) {
				throw new AWException("I/O error: ",e);
			}
			
			sq.addRun(ita.bn,start,stop);
			
		}
		
		// write out sequence
				
		sq.save("sequence");
		
	}
	
	// get index record for specified item
	
	private Index access (
		int bn,
		int in
	) throws AWException {
		try {
			Index ix = new Index(bn,in);
			return ix;
		} catch (IOException e) {
			throw new AWException("cannot read index");
		}
	}
	
}
