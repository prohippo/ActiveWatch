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
// AW file SelectProbabilities.java : 08Feb98 CPM
// analyze probabilities for n-gram indexing

package aw;

public class SelectProbabilities extends Probabilities {

	private static final int N = 16;

	public Range   rg; // computed probability range
	
	public short[] gm; // selected n-gram indices
	public int     ns; // selection count
	public int     np; // non-zero count

	public double sumplogp = 0;
	
	public double sump = 0;

	// show indices with highest collection probabilities
	
	public SelectProbabilities (
		int n
	) throws AWException {
	
		super();
		rg = new Range();
		if (n < N)
			n = N;
		gm = new short[n+1];
		
		int i,j;
		int k = 0;
		
		np = 0;
		
		for (i = 1; i < Parameter.MXI; i++) {
 
			if (array[i] > 0.) {
				np++;
				
				// select high-probability n-grams

				j = k;
				while (--j >= 0) {
					if (array[gm[j]] >= array[i])
						break;
					gm[j+1] = gm[j];
				}
				gm[j+1] = (short) i;
				if (k < n)
					k++;
				
				// add up probabilities and compute indexing entropy
				
				sump += array[i];
				sumplogp += array[i]*Math.log(array[i]);
			}
		}
 
		ns = k;
		
	}
	
	// standard entropy in bits
	
	public double entropy (
	) {
		return -sumplogp/Math.log(2.);
	}
	
	// entropy as percentage of maximum possible
	
	public double relativeEntropy (
	) {
		return (-100*sumplogp)/Math.log((float)(np));
	}

}

