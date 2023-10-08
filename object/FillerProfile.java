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
// AW file FillerProfile.java : 07oct2023 CPM
// profile class with methods to store indices and weights

package object;

import aw.*;
import gram.Gram;

public class FillerProfile extends Profile {

	public static final short  HWGT =  1000; // infinite weight

	public static final short  NHGM = 29999; // sentinel values for profile
	public static final byte   NHWT =    -1;

	public static final double MXWT =   127; // for normalizing weights

	public int literalCount = 0; // for fill

	// initialize as empty

	public FillerProfile (

	) {
		super();
	}

	// initialize from stored profile

	public FillerProfile (

		int n

	) throws AWException {
		super(n);
	}

	// store selected indices and weights and compute noise model

	public int fill (

		int     no,  // n-gram count
		short[] vg,  // n-gram indices
		short[] vf,  // n-gram weights
		float[] pbs, // packed probabilities
		float   thr  // significance threshold

	) throws AWException {
		int  k,n;    // general indices
		short gm;    // n-gram index number
		byte  wt;    // profile weight

		// get maximum for normalizing

		short mx = 0;
		short mn = HWGT;
		for (k = 0; k < no; k++) {
			if (mx < vf[k])
				mx = vf[k];
			if (mn > vf[k])
				mn = vf[k];
		}
		double f = (mx > 0) ? MXWT/mx : 0;

		// reduce index count to fit into profile array

		int ni = no;		
		while (ni >= Profile.MXP - 1) {
			ni = 0;
			short mm = HWGT;
			for (int j = 0; j < no; j++) {
				if (vf[j] == mn)
					vf[j] = 0;
				else if (vf[j] > 0) {
					if (mm > vf[j])
						mm = vf[j];
					ni++;
				}
			}
			mn = mm;
		}
		no = ni;

		// retain indices with non-zero weights

		for (n = k = 0; k < no; k++) {
			wt = (byte)(f*vf[k]);
			if (wt == 0)
				continue;
			gm = vg[k];
			if (gm >= Parameter.MXI)
				throw new AWException("bad index=" + gm);
			if (gm <= Gram.NLIT)
				literalCount++;
			gms[n] = gm;
			wts[n] = wt;
			pbs[n] = pbs[k];
			vf[n++] = wt;
		}
		gms[n] = NHGM;
		wts[n] = NHWT;

		// derive noise model parameters for unit vector

		if (n == 0) {
			uexp = 0F;
			uvar = 1F;
		}
		else {
			ProfileModel model = new ProfileModel(n,vf,pbs);
			uexp = model.expectedValue();
			uvar = model.variance();
		}

		trc[0] = 0;
		nhth = shth = 0;
		sgth = thr;
		return n;
	}

}

