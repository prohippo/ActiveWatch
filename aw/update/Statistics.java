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
// AW file Statistics.java : 22Oct97 CPM
// for computing n-gram statistics

package aw.update;

import aw.*;

public class Statistics {

	public Counts        cts;
	public Probabilities pbs;
	public Range         rng;

	public Statistics (
	
	) throws AWException {
	
		cts = new Counts();
		pbs = new Probabilities();
		rng = new Range();
	
	}
	
	public int[] toVector (
	
	) {
		int[] v = new int[Parameter.MXI+1];
		float pbm = pbs.array[0];
		if (pbm > 0)
			for (int i = 1; i <= Parameter.MXI; i++)
				v[i] = (int) (pbs.array[i]/pbm);
			
		return v;
	}
	
	public void fromVector (

		int[] v
		
	) {
		float pb,pbm;
		
		int sum = 0;
		for (int i = 1; i <= Parameter.MXI; i++)
			sum += v[i];
		if (sum == 0)
			return;
		
		pbs.array[0] = pbm = (float) (1.0/sum);
		rng.low  = (float) (1.0);
		rng.high = (float) (0.0);

		float sump = 0;
		int k = 0;
		
		for (int i = 1; i <= Parameter.MXI; i++) {
			if (v[i] == 0)
				continue;
				
			pb = v[i]*pbm;
			pbs.array[i] = pb;
			
			if (rng.low  > pb)
				rng.low  = pb;
			if (rng.high < pb)
				rng.high = pb;

			sump += pb*Math.log(pb);
			k++;
		}
		
		if (k > 0)
			rng.entropy = (float) (100.*(-sump/Math.log(k)));
		else
			rng.entropy = 0;
	}
	
	public void save (
	
	) throws AWException {
		pbs.save();
		cts.save();
		rng.save();
	}
}
