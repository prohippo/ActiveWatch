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
// AW file Indexer.java : 03Mar00 CPM
// top-level n-gram indexing

package aw.index;

import aw.*;
import gram.*;
import stem.*;
import object.*;
import java.io.*;

public class Indexer {

	VectorDerivation vd; // to break text down to raw n-gram vector
	Control co;          // data base parameters from file

	// get data base and prepare for n-gram text analysis
			
	public Indexer (
		VectorDerivation vdr
	) throws AWException {

		co = new Control();
		vd = vdr;

	}
	
	// to process all text items in current new batch
	
	public int run (
	
	) throws AWException {

		byte[] vb; // byte array for index vector
		int     k; // item count

		// process only new items

		IndexVector iv = null;
		int ns = Subsegment.count(co.cubn);
		int n  = Offset.count(co.cubn);
		if (n > 0)
			--n;
							
		for (k = 0; n < ns; n++) {

			// continue for rest of items
			
			if (!vd.set(co.cubn,n))
				break;
			int six = vd.index();
			if (six == 1)
				k++;
			
			System.out.println("subsegment " + n);
			
			// get n-gram indexing
			
			System.out.println(vd.length() + " chars");			
			System.out.println("** analyzing");
			vb = vd.derive();
			System.out.println("** transforming");

			// build transformed count array for index vector
			
			for (int j = 1; j <= Parameter.MXI; j++)
				if (vb[j] > 0)
					vb[j] = (byte) Parameter.transform(vb[j]);

			// build compressed vector and write out
			
			System.out.println("** compressing");
			iv = new IndexVector(vb,1);
			iv.subsegmentIndex(six);
			System.out.println("** writing");
			iv.save(co.cubn);

		}

		// close objects and update control file

		Index ix = new Index();
		int m = ix.count(co.cubn);
		Index.close();
		try {
			vd.close();
			if (iv != null)
				iv.close();
			co.setBatchCount(co.cubn,m);
			co.save();
		} catch (IOException e) {
			throw new AWException("control update");
		}

		// how many new items processed
				
		return k;
	
	}

}

