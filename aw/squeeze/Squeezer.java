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
// AW file Squeezer.java : 11aug2021 CPM
// create run squeezed vector file

package aw.squeeze;

import aw.*;
import object.*;
import java.io.*;

class IndexVectorsToSqueeze extends IndexVectors {

	// load vectors from run information
	
	public IndexVectorsToSqueeze (
	
		int bn,
		int os,
		int ln
		
	) throws AWException {
		super(bn,os,ln);
	}

	// add squeezed vector to buffer
		
	public int squeeze (
	
		IndexVector iv, // where to put squeezed vector
		int        ivn, // offset in vector buffer
		boolean[] keep, // what n-grams to retain
		int      mvsum  // minimum vector sum to store
	
	) {
	
		ivn = iv.startEncoding(ivn);

		while (next()) {

			// take only lexical indices
							
			if (extent >= Parameter.NLX)
				break;

			if (keep[gram])
				ivn = iv.encodeGramCount(ivn,gram,count);

		}

		return iv.endEncoding(ivn,Parameter.NLX,mvsum);
		
	}
	
}

// collect squeezed vectors for inner products

public class Squeezer {

	private PairwiseModel mdl; // noise model

	private byte[] sv = new byte[Parameter.MXV];
	private boolean[] keep = new boolean[Parameter.MXI];
		
	// initialize and process
	
	public Squeezer (
	
		float minm,
		float maxm
		
	) throws AWException {
	
		// precompute noise model values
				
		mdl = new PairwiseModel(minm,maxm,keep);
	
	}
	
	public void run (
	
		int  mvsum
		
	) throws AWException {
	
		VectorsForInnerProducts vip; // to compute inner products from
		IndexVectorsToSqueeze siv;   // input vectors to be squeezed
		IndexVectors ivs;            // output squeezed vectors
		
		int ivn  = 0; // allocation index for squeezed vector buffer
		int nitm = 0; // number of squeezed vectors, one per item
		int nssg;     // subsegment count
		int ssn;      // subsegment index
		byte[] vb;    // output vector buffer
		
		try {
		
			// get sequence of vectors to squeeze
			
			SequencePlus seq = new SequencePlus("sequence");
			System.out.println("\n" + seq.nmitm + " items to squeeze\n");
			nssg = seq.nmssg;
			if (nssg == 0)
				throw new AWException("empty sequence");

			// set up output file
			
			ivs = new IndexVectors(seq.nmitm*Parameter.MXV/2);
			vip = new VectorsForInnerProducts(mdl,0,ivs);
			vb = ivs.buffer();						

			for (int i = 0; i < seq.nmrun; i++) {
			
				// get next run of subsegment vectors
				
				System.out.print("at " + seq.r[i].bbn + "::" + seq.r[i].ssn);
				System.out.print(", " + seq.r[i].nss + " subsegments in run (");
				System.out.println(seq.r[i].rfo + ", " + seq.r[i].rln + ")");
				siv = new IndexVectorsToSqueeze(seq.r[i].bbn,seq.r[i].rfo,seq.r[i].rln);
				
				for (int j = 0; j < seq.r[i].nss; j++) {
				
					// squeeze vectors only for first subsegment of item
					
					ssn = siv.subsegmentIndex();
					System.out.print("@" + ssn);
					if (ssn != 1) {
						System.out.println(" disregarded");
						System.out.println("i=" + i + ", j=" + j);
					}
					else {
						nitm++;
						ivn = siv.squeeze(ivs,ivn,keep,mvsum);
						System.out.println(": base=" + ivn);
					}
					siv.skip();
					
				}
				
			}
			
			// mark end of squeezed vectors and save count
			
			ivs.size(ivn);
			ivs.close();
			
			vip.count = nitm;
			vip.save();
		
		} catch (IOException e) {
			throw new AWException("cannot write squeezed vectors");
		}	

		System.out.println("\n" + nitm + " squeezed out of " + nssg);
		
	}

}
