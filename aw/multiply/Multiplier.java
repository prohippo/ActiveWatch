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
// AW file Multiplier.java : 14aug2021 CPM
// create run sequence file

package aw.multiply;

import aw.*;
import object.*;
import java.io.*;

class MultiplyIndexVectors extends IndexVectors {

	private float fnd,fne; // vector sums
	private float fsms;    // intermediate model sums
	private float fsm2;    //
	private float fsm3;    //
	private float fsm4;    //
	private float fsm22;   //
	
	float[] full = new float[Parameter.MXN]; // expanded index vector
	short[] fvnz = new short[Parameter.MXV]; // indices of non-zero entries
	int     flm  = 0;      // non-zero count
 
	private Probabilities pb; // n-gram probabilities
	private PairwiseModel pm; // saved noise model
	
	private float[] pw = new float[Parameter.MXN]; // n-gram weightings
	
	// initialize for inner product computations
	
	public MultiplyIndexVectors (
		PairwiseModel mdl,
		byte[]        vbs
	) throws AWException {
	
		super();
		
		NX = Parameter.NLX;
		
		// set vector buffer and model
		
		vc = new Vectors(vbs,vbs.length);
		bb = vbs;
		pm = mdl;
		
		// precompute n-gram weights
		
		pb = new Probabilities();

		for (int i = 1; i < Parameter.MXN; i++)
			if (pb.array[i] > 0.)
				pw[i] = (float) (Math.sqrt(1./pb.array[i])*pm.w1n);
				
	}

	// expand vector for row of link matrix
	
	int rebase = 0; // where to start computing a row of links
			
	public void expandA (
	
	) {
		// zero out vector expansion
		
		for (int k = 0; k < flm; k++)
			full[fvnz[k]] = 0;

		// get sum for next vector
				
		reset(rebase);
		fnd = storedSum();

		// get vector entries
		
		for (flm = 0; next(); flm++) {
			full[gram] = pw[gram]*count;
			fvnz[flm] = gram;
		}
 		skip();
 		
 		rebase = base();

		fsms = fnd*pm.sumps;
		fsm2 = fnd*pm.sump2;
		fsm3 = fnd*pm.sump3;
		fsm4 = fnd*pm.sump4;
		fsm22= fnd*pm.sump22;		

	} 

	// compute scaled similarity for row and column of link matrix
	
	public double computeAB (

	) {

		fne = storedSum();

		// derive noise parameters
		
		double c1 = fnd + fne - 1.;
		double c2 = c1 - 1.;
		double eS = fne*fsms;
		double cov = -fne*c1*fsm22;
		double v  = fne*(fsm2+c2*fsm3-c1*fsm4);
		double vS = v + cov;
		double sS = 0;

		if (vS == 0)
			return -100;
			
		// compute inner product
			
		while (next())
			sS += full[gram]*count;
		skip();

		double z = sS - eS;

		// statistical scaling

		if (z > 0)
			return z*z/vS;
		else
			return 0;
			
	}
	
}

// class for AW inner product computation

public class Multiplier {

	private VectorsForInnerProducts vip; // access to squeezed vectors
	private MultiplyIndexVectors vs;     // objects for actual computation

	// initialization
		
	public Multiplier (
	
	) throws AWException {
		vip = new VectorsForInnerProducts();
		vs = new MultiplyIndexVectors(vip.model,vip.ivs.buffer());
		Link.close();
	}

	// compute links within specified significance range
		
	public void run (
		float lowerThr, // minimum significance for link
		float upperThr  // maximum significance
	) throws AWException {
	
		double  sim; // scaled similarity
		int   count; // of distinct links
		int fb0,fb1; // for generating Fibonacci numbers
		
		// compute scaled similarity for each distinct pair of vectors

		System.out.println("computing pairwise similarity >= " + lowerThr + " and <= " + upperThr);
		System.out.println(vip.count + " vector entries");

		double lowr = lowerThr*lowerThr;
		double uppr = upperThr*upperThr;
		
		count = 0;
		fb0 = fb1 = 1;
		
		Link lk = new Link();
				
		for (int m = 1; m < vip.count; m++) {
		
			// tell current row

			if (m == fb1) {
				System.out.println(" row " + m);
				fb1 += fb0;
				fb0 = m;
			}

			// expand vector for row
			
			vs.expandA();
			
			for (int n = m + 1; n <= vip.count; n++) {
			
				// compute pairwise similarity
				sim = vs.computeAB();
				
				// check against minimum threshold
				if (sim >= lowr && sim <= uppr) {
					lk.set((short)m,(short)n,(float)Math.sqrt(sim));
					lk.save();
					count++;
				}
			}
		}
		
		// put in sentinel record

		short sentinel = (short)(Link.MXTC+1);
		lk.set(sentinel,sentinel,(float)0.0);
		lk.save();
		
		System.out.println(count + " links computed");
		
	}

}
