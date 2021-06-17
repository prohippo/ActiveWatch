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
// AW file ProfileModel.java : 28Mar02 CPM
// profile noise model computation

package object;

import java.io.*;

public class ProfileModel {

	public double expM; // for unit expected value
	public double varM; // for unit variance

	// compute expected value and variance
	// of similarity measures for profile
 
	public ProfileModel (

		int     lp, // number of n-grams in profile
		short[] pf, // profile n-gram weights
		float[]  p  // packed probabilities
 
	) {
	
		double   pfp;  // weighted probability
		double sumps;  // sum of p's 
		double sump2;  // sum of p's times q's
		double sump11; // covariance sum
		double sumtm;  // covariance sum increment

		sumps = 0.0;
		sump2 = 0.0;
		sump11= 0.0;
		sumtm = 0.0;
		for (int j = 0; j < lp; j++) {
			pfp = pf[j]*p[j];
			sumps += pfp;
			sump2 += pf[j]*pfp*(1-p[j]);
			sump11 +=pfp*sumtm;
			sumtm  +=pfp;
		}
		expM = sumps;
		varM = sump2 - 2*sump11;
	}

	public final float expectedValue (
	
	) {
		return (float) expM;
	}
	
	public final float variance (
	
	) {
		return (float) varM;
	}
	
}