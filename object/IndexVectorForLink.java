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
// IndexVectorForLink.java : 10jan2022 CPM
// with methods for scaled pairwise inner products

package object;

import aw.AWException;
import aw.Probabilities;
import aw.Letter;
import aw.Parameter;

public abstract class IndexVectorForLink extends SimpleIndexVector {

	protected static int mvsum = 10; // minimum vector sum

	protected byte[] fv; // expansion for computing
	protected int   nhs; // hit count in inner product
	private   int  vsum; // reduced vector sum

	public final int vsum ( ) { return vsum; }

	// initialize from full vector

	public IndexVectorForLink (
		byte[] v
	) throws AWException {
		super(v,mvsum);
		setUp();
	}
	// initialize from saved vector

	public IndexVectorForLink (
		int bn,
		int xn
	) throws AWException {
		this(new IndexVector(bn,xn));
	}

	// initialize from an unsaved vector

	public IndexVectorForLink (
		SimpleIndexVector v
	) throws AWException {
		bb = v.bb;
		setUp();
	}

	// precompute model parameters for scaling similarity

	private void setUp (

	) throws AWException {
		initializeModel();
		vsum = reducedSum();
		if (vsum < mvsum)
			vsum = mvsum;
	}

	// actual inner product for similarity

	private double product (
		byte[] fv
	) {
		double summation = 0;
		nhs = 0;
		while (next()) {
			if (gram > Parameter.MXN)
				break;
			if (bk[gram])
				if (fv[gram] != 0) {
					float term = fv[gram]*count*pw[gram];
					summation += term;
					traceTerm(gram,fv[gram],term,summation);
					nhs++;
				}
		}
		reset();
		return summation;
	}

	// needed as model parameter

	public int reducedSum (

	) {
		int sum = 0;
		while (next()) {
			if (gram > Parameter.MXN)
				break;
			if (bk[gram])
				sum += count;
		}
		reset();
		return sum;
	}

	// precompute multinomial model values

	protected static boolean[] bk;   // indices to compute with

	protected static float[]   pw;   // n-gram probabilities divided by weights

	private static PairwiseModel mo; // precomputed noise model values

	private void initializeModel (

	) throws AWException {

		if (mo != null)
			return;

		// get model sums for multinomial noise distribution
		// with conditional probabilities for n-grams kept

		bk = new boolean[Parameter.MXI+2];
		mo = new PairwiseModel((float) getMultiplier(),(float) getDivisor(),bk);

		// read in probabilities

		Probabilities pb = new Probabilities();

		int nl = Parameter.MXN; // maximum index number

		// compute weights for n-grams kept

		int lim = 0;		// number of indices kept
		double sum = 0;	 // accumulated sum of probabilities
		double mwt = 1e20F; // minimum weight
		pw = new float[Parameter.MXI];
		for (int i = 1; i < nl; i++) {
			if (bk[i]) {
				float pbi = pb.array[i];
				sum += pbi;
				float w = pw[i] = (float) Math.sqrt(1./pbi);
				if (mwt > w)
					mwt = w;
				lim++;
			}
		}

		traceLimit(lim,sum);
		if (lim == 0)
			throw new AWException("no nonzero probabilities");

		// compute factors for conditioning probabilities
		// and normalizing weights

		double wn = 1.0/mwt;

		// compute n-gram weights

		for (int i = 1; i < Parameter.MXN; i++)
			if (bk[i])
				pw[i] *= wn;

	}

	// must define in subclass for n-gram selection in model

	public abstract double getMultiplier ( );
	public abstract double getDivisor ( );

	// override for any instrumentation

	public void traceTerm  ( int gram, int count, float term, double sum ) { }
	public void traceLimit ( int lim, double sum )  { }
	public void traceSums  ( int vsum1, int vsum2 ) { }

	protected double sum;	  // sum computed for inner product
	protected double expected; // expected value	 of sum
	protected double sigma;	// standard deviation of sum

	// intermediate model sums precomputed

	private double fsms; 
	private double fsm2;
	private double fsm3;
	private double fsm4;
	private double fsm22;

	protected void expandIt (

	) {
		if (fv == null) {
			fv = expand();
			fsms = vsum*mo.sumps; 
			fsm2 = vsum*mo.sump2;
			fsm3 = vsum*mo.sump3;
			fsm4 = vsum*mo.sump4;
			fsm22= vsum*mo.sump22;
		}
	}

	// get scaled similarity

	public double scaledSimilarity (
		IndexVectorForLink ivl
	) {
		expandIt();
		int vsum1 = vsum;
		int vsum2 = ivl.vsum();

		traceSums(vsum1,vsum2);

		// compute model

		double c1 = vsum1 + vsum2 - 1.;
		double c2 = c1 - 1.;
		double es = vsum2*fsms;
		double cv = -vsum2*c1*fsm22;
		double vr = vsum2*(fsm2 + c2*fsm3 - c1*fsm4);
		double variance = vr + cv;
		if (variance == 0)
			return 0;

		// compute scaled inner product

		expected = es;
		sum = ivl.product(fv);
		nhs = ivl.nhs;
		sigma = Math.sqrt(variance);
		return (sum - expected)/sigma;
	}

}
