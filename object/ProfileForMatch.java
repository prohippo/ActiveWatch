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
// ProfileForMatch.java : 13jan2023 CPM
// set for fast sequential scanning of index vectors

package object;

import aw.*;

public class ProfileForMatch extends Profile {

	public int     xl;
	public short[] xs = new short[MXP]; // extents for indices

	public ProfileForMatch ( Profile pro ) {
		nhth = pro.nhth;
		shth = pro.shth;
		sgth = pro.sgth;
		uexp = pro.uexp;
		uvar = pro.uvar;
		gms  = pro.gms;
		wts  = pro.wts;
		trc  = pro.trc;

		short en = 0;
		for (xl = 0; xl < MXP; xl++) {
			int ng = gms[xl];
			if (ng > Parameter.MXI)
				break;
			while (Parameter.EB[en + 1] <= ng)
				en++;
			xs[xl] = en;
		} 
	}

}
