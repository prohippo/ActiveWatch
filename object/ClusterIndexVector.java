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
// AW file ClusterIndexVector.java : 20Sep02 CPM
// cluster profile generation

package object;

import aw.Parameter;
import aw.AWException;

// access to index vectors for aggregating n-gram occurrences

public class ClusterIndexVector extends SimpleIndexVector {

	// constructor
	
	public ClusterIndexVector (
	
		SimpleIndexVector v
		
	) throws AWException {
		bb = v.bb;
	}
	
	// accumulate counts
	
	public int aggregate (

		int weight,
		short[] fv,
		byte [] cv
		
	) {
		if (weight == 0)
			return 0;
		int nr = 0;
		byte[] xv = new byte[Parameter.MXI+1];

		reset();
		while (next()) {
			if (fv[gram] == 0)
				nr++;
			fv[gram] += weight;
			if (xv[gram] == 0)
				cv[gram]++;
			xv[gram]++;
		}
		return nr;
	}
	
}

