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
// AW file Filter.java : 25Feb99 CPM
// class methods for application of packed filter

package aw;

public class Filter {

	private static byte[] hv = new byte[Profile.MXP]; // profile hit vector

	// mark hit for nth profile index
	
	public static void hit (
	
		int n // ordinal index
		
	) {
	
		hv[n] = 1;
		
	}

	// see whether enough filters are satisfied
	
	public static final int d = 128;
	
	public static boolean check (
	
		byte[] fb // packed filter
		
	) {
	
		int nfiltr; // number of filters
		int thresh; // minimum to match
		int npass;  // actual filter count passed
		int sum;    // number of matches for filter
		int j,n;

		int k = 0;
		nfiltr = fb[k++];
		if (nfiltr == 0)
			return true;
			
		// get number of filters to satisfy
		
		thresh = fb[k++];
		npass = 0;

		while (--nfiltr >= 0) {

			// get hit threshold for next filter
			
			n = fb[k++];
			
			// count up actual hits against it
			
			for (sum = 0; (j = fb[k++] + d) < Profile.MXP; )
				sum += hv[j];
			
			// check against required number of hits
			
			if (sum >= n) {
				if (++npass == thresh)
					return true;
			}
			else if (npass + nfiltr < thresh)
				return false;
				
		}
		return false;
		
	}
	
	// are there any filters?
	
	public static boolean exist (
	
		byte[] fb // packed filter
		
	) {
		
		return (fb[0] > 0);
		
	}

	// reset profile hits
	
	public static void clear (
	
	) {
	
		for (int i = 0; i < hv.length; i++)
			hv[i] = 0;
			
	}

}
