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
// AW file Updater.java : 09Apr99 CPM
// top-level class for computing n-gram statistics

package aw.update;

import aw.*;
import object.IndexVector;
import java.io.*;

// reads the vectors file and recomputes
// statistics for the current collection
// after adding or dropping a batch

public class Updater {

	protected Statistics sso;
	protected Control     co;
	
	protected static int bno = 0;

	public Updater (
	
	) throws AWException {
		sso = new Statistics();
		co  = new Control();
	}
	
	public int run (
	
		boolean add,
		boolean old
		
	) throws AWException {
		int m = 1;
		int n = 0;
		int[] fs;
		Offset os;
		UpdateIndexVector iv = null;

		try {
		
			// find which batch to update on
			
			if (add) {
				if (co.nobs == Control.NTB)
					throw new AWException("too many batches");
				bno = co.cubn;
			}
			else {
				if (co.nobs == 0)
					throw new AWException("no data");
				bno = (old) ? co.cubn - co.nobs : co.cubn - 1;
				if (bno < 0)
				bno += Control.NTB;
				m = -1;
			}

			n = Offset.count(bno) - 1;
			if (n < 0)
				throw new AWException("no update");
		
			// get batch vectors and update statistics
		
			fs = sso.toVector();
			for (int i = 0; i < n; i++) {
				System.out.println(bno + "::" + i);
				iv = new UpdateIndexVector(bno,i);
				iv.update(fs,sso.cts.array,m);
			}
			sso.fromVector(fs);
			
			// update control file

			if (add)
				co.increment();
			else if (old)
				co.decrementOld();
			else
				co.decrementNew();
		
			// update statistics and control data
		
			sso.save();		
			co.save();
		
		} catch (IOException e) {
			throw new AWException("cannot get data");
		}
		
		if (iv != null)
			iv.close();
		
		return n;
	}
	
}

class UpdateIndexVector extends IndexVector {

	// always loaded from file
	
	public UpdateIndexVector (
	
		int bn,
		int in
		
	) throws AWException {
		super(bn,in);
	}
	
	// update n-gram frequencies and item counts from a vector
		
	public void update (
	
		int[] fs,
		int[] cs,
		int    m
		
	) {
		reset();
		if (subsegmentIndex() == 1)
			cs[0] += m;

		while (next()) {
			if (gram >= fs.length)
				break;
			fs[gram] += m*count;
			cs[gram] += m;
		}
	}

}
