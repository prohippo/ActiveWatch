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
// AW file FullProfile.java : 19Oct99 CPM
// profile with expansion to full vector of weights

package object;

import aw.*;

// get full vector of weights from profile,
// avoiding constant reallocation of large array

public class FullProfile {

	private static int[]  ps = new int[Profile.MXP]; // index nonzero weights
	public  static int    psn; // nonzero weights count
	public  static byte[] pv;  // all profile weights

	private Profile pp;
	
	// constructors
			
	public FullProfile (
	
		int pn     // profile index
		
	) throws AWException {
	
		set(new Profile(pn));
	
	}
	
	public FullProfile (
	
		Profile pr // profile itself
		
	) throws AWException {
	
		set(pr);
	
	}

	// profile expansion
		
	private void set (
	
		Profile p
		
	) throws AWException {
		
		pp = p;
		
		if (pv == null)
			pv = new byte[Parameter.MXI+1];

		// zero out previous weights for reuse of vector
					
		for (int i = 0; i < psn; i++)
			pv[ps[i]] = 0;

		// store weights for new profile
		
		for (psn = 0; p.gms[psn] < Parameter.MXI; psn++) {
			short g = p.gms[psn];
			ps[psn] = g;
			pv[g] = p.wts[psn];
		}
	}
	
	// close out profiles
	
	public final void close (
	) {
		if (pp != null)
			pp.close();
	}
	
	public final int count ( ) { return psn; }
	
	public final byte[] vector ( ) { return pv; }
	
	public final Profile profile ( ) { return pp; }

}