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
// AW file ClusterWeighting.java : 10jan2022 CPM
// class for assigning weights to items in given cluster

package object;

import aw.AWException;
import aw.Item;
import object.IndexVectorForLink;
import java.io.*;

// derive connectivity of items by pairwise similarity 

public class ClusterWeighting {

	// class for pairwise similarity computation

	private static class IxVecL extends IndexVectorForLink {
		public IxVecL ( int b, int x ) throws AWException { super(b,x); }
		public final double getMultiplier ( ) { return 10; }
		public final double getDivisor    ( ) { return  2; }
	}

	private IxVecL[] iv; // index vectors for listed items
	private double[] sm; // similarity measures

	// compute upper triangle of pairwise similarity measures
	
	public ClusterWeighting  (
		Item[] its,  // items to weight
		int    nit   // number of items
	) throws AWException {
		iv = new IxVecL[nit];
		sm = new double[((nit - 1)*nit)/2];
		for (int i = 0; i < nit; i++)
			iv[i] = new IxVecL(its[i].bn,its[i].xn);
		for (int i = 0, k = 0; i < nit - 1; i++)
			for (int j = i + 1; j < nit; j++, k++)
				sm[k] = iv[i].scaledSimilarity(iv[j]);
	}
	
	// item weight set to number of links over threshold
	
	public int[] score (
	    double thr  // minimum scaled similarity for link
	) {
		int nit = iv.length;
		int[] w = new int[nit];
		for (int i = 0; i < nit; i++)
		    w[i] = 1;
		for (int i = 0, k = 0; i < nit - 1; i++)
			for (int j = i + 1; j < nit; j++, k++)
				if (sm[k] >= thr) {
					w[i]++; w[j]++;
				}
		return w;
	}

}
