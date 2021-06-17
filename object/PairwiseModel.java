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
// AW File PairwiseModel.java : 18Aug99 CPM
// squeeze vectors for inner product computation

package object;

import aw.*;
import object.*;
import java.io.*;

public class PairwiseModel {

	public  static final int size = 24;

	// maximum n-gram index kept
	
	private static final int NAN = Letter.NAN;
	private static final int MXL = Parameter.MXN - NAN*NAN + Letter.NA*NAN;
	
	protected int getMXL ( ) { return MXL; }

	// model components
	
	public float sumps;  // weighted sum of probabilities squared
	public float sump2;  // doubly weighted probabilities squared
	public float sump3;  // doubly weighted probabilities cubed
	public float sump4;  // doubly weighted of probabilities doubly squared
	public float sump22; // doubly weighted cross probabilities squared
	public float w1n;    // n-gram weight normalization

	// initialize
	
	public PairwiseModel (
		float  minimum, // for probabilities of interest
		float  maximum, // for probabilities of interest
		boolean[] keep  // which n-gram indices to retain in vectors
	) throws AWException {
	
		int lim = 0; // number of accepted n-grams
		float app;   // weighted product of p's
		float smp;   // summed probabilities
		float sum;   // accumulated sum
		float mwt;   // minimum weight
		float vr,cv; // components of variance
		float f1,f2; // factors to condition p's
		float w1,w2; // normalization coefficients

		Probabilities pb = new Probabilities();
		Range         rg = new Range();

		// set probability limits
		
		float lo = rg.low*minimum;
		float hi = rg.high*maximum;

		System.out.println("probability thresholds: low= " + lo + ", high= " + hi);
		
		int mxl = getMXL();
		
		float[] a = new float[mxl];
		float[] p = new float[mxl];

		mwt = (float) 1e20;

		// pack probabilities and associated
		// weights for text n-grams kept

		smp = 0F;
		int n = 0;
		
		for (int i = 1; i < mxl; i++) {
			if (pb.array[i] > 0.)
				n++;
			if (lo <= pb.array[i] && pb.array[i] < hi) {
				smp += pb.array[i];
				p[lim] = pb.array[i];
				a[lim] = (float)Math.sqrt(1./p[lim]);
				if (mwt > a[lim])
					mwt = a[lim];
				lim++;
				keep[i] = true; // mark n-gram for retention
			}
		}
		
		if (lim == 0)
			throw new AWException("no significant indices");
 
		System.out.print(lim + " text indices kept out of " + n);
		System.out.println(" with sum of probabilities = " + smp);

		// compute factors to condition probabilities
		// and normalize weights

		f1 = (float)(1./smp);
		f2 = f1*f1;
		w1 = (float)(1./mwt);
		w2 = w1*w1;
		w1n = w1;
 
		// get model sums for multinomial noise distribution
		// with conditional probabilities for n-grams kept

		sumps = 0F;
		sump2 = sump3 = sump4 = 0F;
		sump22= 0F;
		sum   = 0F;
		for (int i = 0; i < lim; i++) {
			app = a[i]*p[i]*p[i];
			sumps += app;
			sump2 += a[i]*app;
			sump3 += a[i]*app*p[i];
			sump4 += app*app;
			sump22+= app*sum;
			sum   += app;
		}
		sumps *= w1*f2;
		sump2 *= w2*f2;
		sump3 *= w2*f1*f2;
		sump4 *= w2*f2*f2;
		sump22*= 2.*w2*f2*f2;

	}
	
	// load model from file
	
	public PairwiseModel (
		DataInputStream in
	) throws AWException {
		load(in);
	}
	
	// I/O methods
	
	public void load (
		DataInputStream in
	) throws AWException {
		try {
			sumps  = in.readFloat();
			sump2  = in.readFloat();
			sump3  = in.readFloat();
			sump4  = in.readFloat();
			sump22 = in.readFloat();
			w1n    = in.readFloat();
		} catch (IOException e) {
			throw new AWException("cannot load model");
		}
	}
		
	public void save (
		DataOutputStream out
	) throws AWException {
		try {
			out.writeFloat(sumps);
			out.writeFloat(sump2);
			out.writeFloat(sump3);
			out.writeFloat(sump4);
			out.writeFloat(sump22);
			out.writeFloat(w1n);
		} catch (IOException e) {
			throw new AWException("cannot save model");
		}
	}

}