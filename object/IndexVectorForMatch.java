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
// IndexVectorForMatch.java : 03Oct02 CPM
// supports basic similarity measure versus profile

package object;

import aw.*;
import java.io.*;

// for computing scaled similarity against profile

public class IndexVectorForMatch extends SimpleIndexVector {

    // initialize from an unsaved vector
        
    public IndexVectorForMatch (
        SimpleIndexVector v
    ) {
        bb = v.bb;
    }
	
	// compute squared similarity
		
	public boolean match (
		ProfileForMatch p, // profile to use
		double         th  // squared significance threshold
	) {

		int hk  = 0; // filter index count
		int nlx = 0; // lexical hits
		int nph = 0; // phonetic hits
		int ips = 0; // inner product score
        reset();
		
		boolean f = Filter.exist(p.trc);

		if (f)
			Filter.clear();
		
		// compute inner product of vector with profile
				
		for (int i = 0; i < p.xl; i++) {
			toExtent(p.xs[i]);
			int ng = p.gms[i];
			while (ng > gram && next());
			if (ng == gram) {
				ips += p.wts[i]*count;
				if (gram < Parameter.MXN)
					nlx++;
				else
					nph++;
				if (f)
					while (p.gms[hk] < gram) hk++;
						Filter.hit(hk++);
			}
		}
		
        x = 0;

		// apply model to scale similarity squared
				
		if (ips > 0 && nlx >= p.nhth && nph >= p.shth)
			if (Filter.check(p.trc)) {
				int vsm = decodeSum(); // interrupted scan forces this
				double sgms = vsm*p.uvar;
				double expv = vsm*p.uexp;
				double z = ips - expv;
				x = (z < 0) ? 0 : z*z/sgms;
				return (x < th && nlx + nph < p.xl) ? false : (x > 0);
			}
		
		return false;

	}
        
    private double x; // saved scaled similarity squared
        
    public  double scaledSimilarity ( ) { return Math.sqrt(x); }
	
	public  final int sum ( ) { return sum; }
	
}
