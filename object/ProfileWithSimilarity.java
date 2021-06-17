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
// ProfileWithSimilarity.java : 06Aug01 CPM
// for the most basic match computation

package object;

import aw.*;

public class ProfileWithSimilarity extends Profile {

	// copy over profile elements

	public ProfileWithSimilarity ( Profile pro ) {
		nhth = pro.nhth;
		shth = pro.shth;
		sgth = pro.sgth;
		uexp = pro.uexp;
		uvar = pro.uvar;
		gms  = pro.gms;
		wts  = pro.wts;
		trc  = pro.trc;
	}
	
	public ProfileWithSimilarity ( int pn ) throws AWException {
		super(pn);
	}
	
	//compute match with profile
	
	private int hitn,hits;
	private int nmiss;
	
	private float sim;

	public float match ( byte[] vec, int vecl ) {
	
		Filter.clear();
		hitn = hits = nmiss = 0;
		int sum = 0;

		for (int gwi = 0;; gwi++) {
			int gram = gms[gwi];
			if (gram > Parameter.MXI)
				break;
			int wght = wts[gwi];;
			if (vec[gram] == 0)
				nmiss++;
			else {
				sum += vec[gram]*wght;
				if (gram < Parameter.MXN)
					hitn++;
				else
					hits++;
				if (trc[0] != 0)
					Filter.hit(gwi);
			}
		}
 
		// compute significance of similarity

		double vars = vecl*uvar;
		double exps = vecl*uexp;

		if (vars == 0.)
			vars = (float) 1.;
		return sim = (float)((sum - exps)/Math.sqrt(vars));

	}
	
	public final boolean noMisses ( ) { return (hitn > 0 && nmiss == 0); }
	
	public final boolean hitsMet ( ) { return (hitn >= nhth && hits >= shth); }
	
	public final boolean filtersMet ( ) { return Filter.check(trc); }

	public final boolean significanceMet ( ) { return (sim >= sgth); }
	
}