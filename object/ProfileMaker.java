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
// AW file ProfileMaker.java : 09oct2023 CPM
// base class for profile generation

package object;

import aw.*;

public class ProfileMaker {

	public static final float STDSTHR = 6F; // default match threshold for profile

	public FillerProfile profile;           // profile to construct

	protected static Probabilities pb; // for current data
	protected static Counts        ct;

	protected float[] pbs; // packed probabilities
	protected short[]  vg; // profile n-grams
	protected short[]  vf; // profile frequencies

	protected short[]  fv; // accumulating raw frequencies
	protected byte []  cv; // accumulating raw item counts

	public int length = 0; // how many n-gram indices in profile

	// initialize

	public ProfileMaker (

	) {
		fv = new short[Parameter.MXI];
		cv = new byte [Parameter.MXI];
		profile = new FillerProfile();
	}

	// store current weights into profile

	protected final void fill (
		int k
	) throws AWException {
		length = profile.fill(k,vg,vf,pbs,ProfileMaker.STDSTHR);
	}

	// get profile non-zero length

	public final int adjustedLength (

	) {
		return length;
	}

	// a nicety; could save profile directly

	public final void save (
		short pn
	) throws AWException {
		profile.save(pn);
	}

	// a nicety; could close profile directly

	public void close (

	) {
		profile.close();
	}

	// filter out n-gram indices by probability criteria

	protected final int compress (
		int nr,
		int mult
	) throws AWException {
		int i,j,k;

		if (nr == 0)
			return 0;

		vg  = new short[nr]; // allocate arrays for profile
		vf  = new short[nr];
		pbs = new float[nr];

		// load n-gram probabilities, if necessary

		if (pb == null)
			pb = new Probabilities();

		double pthr = mult*pb.array[0];

		// select n-gram indices by probability and pack

		for (i = 0, j = 1, k = 0; i < nr; i++, j++) {
			while (fv[j] == 0)
				j++;
			if (pb.array[j] > pthr) {
				pbs[k] = pb.array[j];
				vg[k  ] = (short) j;
				vf[k++] = fv[j];
			}
		}
		return k;
	}

	// derive actual profile weights from selected n-grams and importance weights

	private static final int WBAS = 10; // to transform importance weight

	protected final int weight (
		int no
	) {
		// set weights and note maximum

		int max = 1;
		int[] ws = new int[no];

		for (int i = 0; i < no; i++) {
			int w = (vf[i] > 0) ? (int)((WBAS + vf[i])/Math.sqrt(pbs[i])) : 0;
			if (max < w)
				max = w;
			ws[i] = w;
		}

		// normalize new weights

		int n = 0;

		double f = ((double) FillerProfile.MXWT)/max;

		for (int i = 0; i < no; i++) {
			short x = (short)(f*ws[i]);
			if (x > 0) {
				pbs[n] = pbs[i];
				vg[ n] = vg[i];
				vf[n++] = x;
			}
		}
		return n;
	}
}
