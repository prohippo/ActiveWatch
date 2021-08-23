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
// AW file Parameter.class : 23aug2021 CPM
// basic indexing definitions

package aw;

// this is defined as a separate class to facilitate customization

public class Parameter {

	// n-gram indexing parameters
	
	public static final int MXN =10001; // limit on lexical n-grams
	public static final int MXI =13584; // limit on all n-gram indices
	public static final int MXV = 8192; // limit on compressed vector size

	// parameters for compressing index vector
	
	public static final int NLX =   20; // number of lexical extents
	public static final int NPH =   12; // number of phonetic extents
	public static final int NEX = NLX + NPH;
	
	public static final short[] EB = {  // extent starts
	     0,  400,  800, 1200, 1600, 2000, 2400, 2800, 3200, 3600, 4000, 4400,
	  4800, 5200, 5600, 6000, 6400, 6800, 7200, 7600, 8000, 8400, 8800, 9200,
	  9600,10326,10796,11266,11736,12206,12676,13146,13616,    0,    0,    0
	};

	// logarithmic transformation of n-gram counts
	
	private static       short[] TRANSFORM = { 0,1,2,2,3,3,3,3,4,4,4,4,4 };
	private static final int     TLMT      = TRANSFORM.length - 1;
	
	// size of transform table
	
	public static final int transformSize (
	
	) {
		return TRANSFORM.length;
	}
	
	// class method for transform
	
	public static final int transform (
		int k // value to map
	) {
	
		if (k > TLMT || k < 0)
			k = TLMT;
			
		return TRANSFORM[k];
		
	}
	
	// for experiment only
	
	public static final void redefineTransform (
		short[] v // new transformations
	) {
	
		int n = v.length;
		if (n > TRANSFORM.length)
			n = TRANSFORM.length;
		for (int i = 0; i < n; i++)
			TRANSFORM[i] = v[i];
	
	}

}
