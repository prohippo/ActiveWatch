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
// KeyProfile.java : 10jan2022 CPM
// profile generation from keys

package object;

import aw.*;
import gram.*;

public class KeyProfile extends ProfileMaker {

	private static final int MLTP = 10; // index must occur at least 10 times

	private KeyTextAnalysis an;

//	create profile from a text string

	public KeyProfile (
		String s
	) throws AWException {

		an = new KeyTextAnalysis();
		int lp = extract(s);
	
		if (lp > 0) {

			// standard profile
			
			lp = compress(lp,MLTP);
			lp = rebalance(lp);
			lp = weight(lp);

			for (int i = 0; i < lp; i++)
				if (pbs[i] == 0.)
					pbs[i] = (float) pb.array[0]; // minimum probability instead of 0

		}

		fill(lp);

	}
	
	// get n-gram counts for keys to be analyzed
		
	private int extract (
		String s
	) {
	
		int n = 0;
		
		an.setText(s);
		AnalyzedToken at;
		
		while ((at = an.next()) != null) {
			Short[] x = at.indices();
			for (int i = 0; i < x.length; i++) {
				short g = x[i];
				if (g == 0)
					break;
				if (fv[g] == 0) {
					cv[g] = 1;
					n++;
				}
				fv[g]++;
			}
		}
		return n;
		
	}

	// smooth out n-gram frequencies
	
	private int rebalance (
		int no
	) {
	
		for (int i = 0; i < no; i++)
			vf[i] = (short) Parameter.transform(vf[i]);
		return no;
		
	}

}
