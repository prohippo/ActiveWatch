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
// ProfileChecker.java : 19Jul99 CPM
// create nomimal vector for profile evaluation

package object;

import aw.Parameter;
import aw.Profile;
import aw.Filter;
import stem.Token;

public class ProfileChecker {

	private class Sf {
		int   mns; // filter match
		int[] mnf; // decoded filter array
	}

	public static final int MWT = 44; // nominal profile weight threshold

	private short[] g = new short[Profile.MXP]; // indices for nominal vector
	
	private int nh; // high-weight profile index count
	private int  l; // count of all profile indices
	private int  m; // index count for nominal story
	
	public final int nh ( ) { return nh; }
	public final int l  ( ) { return  l; }
	public final int m  ( ) { return  m; }
	
	private static final int NFL = Profile.MXP/2; // maximum filter

	private static Sf[] sel; // filter selection array

	private static byte [] wghtu;
	private static short[] wghts = new short[Profile.MXP+1]; // for sorting

	public ProfileChecker (
		Profile prof, // profile source
		int     lim,  // how many indices to take
		int     mwt   // profile weight threshold
	) {
	
		// allocate array once
		
		if (sel == null) {
			sel = new Sf[NFL];
			for (int i = 0; i < NFL; i++)
				sel[i] = new Sf();
		}
		
		// count up profile indices, noting highly weighted ones
		
		nh = 0;
		short[] gram = prof.gms;
		byte [] wght = prof.wts;
		boolean [] wx = new boolean[wght.length]; // mark index selections
		
		for (l = 0; gram[l] < Parameter.MXI; l++)
			if (wght[l] > mwt)
				nh++;

		if (lim > l)
			lim = l;
		if (lim == 0) {
			m = 0;
			return;
		}
		
		// check for filters to choose indices for vector
		
		byte[] tr = prof.trc;
		int n = tr[0];
		if (n == 0) {
		
			// no filters; select by weight only
		
			int w;
			for (int i = 0; i < l; i++) {
				w = wght[i];
				int j = i;
				for (; --j >= 0; ) {
					if (wghts[j] >= w)
						break;
					wghts[j+1] = wghts[j];
				}
				wghts[j+1] = (short) w;
			}
			
			// get cutoff and select
			
			w = wghts[lim-1];
			for (int i = 0; i < l; i++)
				if (wght[i] >= w)
					wx[i] = true;
					
		}
		else {
		
			// get indices referenced in filters
		
			int nm,sm;
			int is;

			int fp = 2;
			for (int i = nm = 0; i < n; i++) {
			
				// expand next filter and compute sum of weights for indices
				
				int fk = 0;
				int[] fa = new int[Token.MXW+1];
				for (sm = 0, ++fp; (is = tr[fp++] + Filter.d) < Profile.MXP; ) {
					fa[fk++] = is;
					sm += wght[is];
				}
				
				// sort filters by sum of weights
				
				int j = nm;
				while (--j >= 0) {
					if (sel[j].mns <= sm)
						break;
					sel[j+1] = sel[j];
				}
				sel[j+1].mnf = fa;
				sel[j+1].mns = sm;
				
				// keep only minimum count to satisfy filter condition
				
				if (nm < tr[1])
					nm++;
			}
			
			// get indices for top filters
			
			for (int j = 0; j < nm; j++)
				for (fp = 0; (is = sel[j].mnf[fp++]) < Profile.MXP; )
					wx[is] = true;
			
		}
		
		// copy selected indices

		gram = prof.gms;
		m = 0;
		for (int i = 0; i < l; i++)
			if (wx[i])
				g[m++] = gram[i];
		g[m] = (short)(Parameter.MXI + 1);
		
	}
	
	// compute scaled similarity versus profile
	// (add high-probability indices implicitly to nominal vector)
	
	public double check (
		Profile prof,
		int     ln, // nominal item length in match
		float[] pb, // n-gram probabilities
		float   ph  // threshold for high-probability index
	) {
	
		// computing inner product
		
		int sum = 0, k = 0;
		for (int i = 0; prof.gms[i] < Parameter.MXI; i++) {
			int gm = prof.gms[i];
			int wt = prof.wts[i];
			if (g[k] < gm)
				k++;
			else if (g[k] > gm)
				sum += (pb[gm] > ph) ? ln*pb[gm]*wt : 0; // for more realism
			else {
				sum += wt; k++;
			}
		}
		
		// statistical scaling
		
		double ex = ln*prof.uexp;
		double va = ln*prof.uvar;
		if (va > 0)
			return (sum-ex)/Math.sqrt(va);
		else {
			System.out.println("** zero variance!");
			return 0.0;
		}
		
	}
	
}
