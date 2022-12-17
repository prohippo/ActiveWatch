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
// AW file ClusterProfileBase.java : 09dec2022 CPM
// cluster profile generation

package object;

import aw.*;
import gram.Gram;
import java.io.*;

// creation of n-gram profile to describe a given cluster

public class ClusterProfileBase extends ProfileMaker {

	private static final int MULTPL = 10; // for setting minimum probability threshold
	private static final int MXM    =  9; // maximum number of items to consider

	private static final int MINLNG = 11; // minimum profile index count

	// build profile from list of items

	public ClusterProfileBase ( ) {
	
	}
	
	public ClusterProfileBase (

		SimpleIndexVector[] item, // item list
		int[]   weight, // item weights
		int     multpl, // multiple for acceptance threshold
		int     minlng  // minimum profile length

	) throws AWException {
	
		combine(item,weight,multpl,minlng);
		
	}
	
	public void combine (

		SimpleIndexVector[] item, // item list
		int[]   weight, // item weights
		int     multpl, // multiple of minimum probability for n-grams
		int     minlng  // minimum profile length

	) throws AWException {
		int count = item.length;
		if (count == 0)
			return;

		int nr = accumulate(item,weight);

		// set thresholds for index selection

		int min = (count == 1) ? 1 : 2;

		int thr = 0;
		for (int i = 0; i < count; i++)
			thr += weight[i];
		thr /= 3;

		for (int i = 1; i < Parameter.MXI; i++) {
			if (fv[i] > 0)
				if (fv[i] < thr || cv[i] < min) {
					fv[i] = 0;
					--nr;
				}
		}

		// keep indices with highest discrimination value

		int k = compress(nr,multpl);
		if (k < minlng)
			k = 0;
		k = focus(k,count);
		k = weight(k);
		fill(k);
	}

	// build n-gram vectors fv and cv for selected items

	private int accumulate (

		SimpleIndexVector[] it,
		int[]  wt

	) throws AWException {
		int nr = 0;
		for (int i = 0; i < it.length; i++) {
			ClusterIndexVector iv = new ClusterIndexVector(it[i]);
			nr += iv.aggregate(wt[i],fv,cv);
		}
		return nr;
	}

	private static final double TH	= 1.0; // minimum constribution
	private static final int    M	=   1; // minimum item count

	private static final double prTHR = .75; // what percentage of items an index must occur in

	private static int LMN = Gram.IB2 + Letter.NA*Letter.NAN; // limit on n-grams starting with letter

	// select best discriminators for profile

	private int focus (

		int no,
		int nm  // what to use as message base size

	) throws AWException {
		float[] pr; // n-gram probability
		float[] cn; // n-gram contributions
		int  maxct; // largest item count for index
		int  count; // current count
		short[] ip; // sort indices
 
		// load n-gram counts, if not done already

 		if (ct == null)
 			ct = new Counts();
 			
		if (no <= 0 || nm <= 0)
			return 0;

		cn = new float[no];
		pr = new float[no];
		ip = new short[no+2];

		maxct = ct.array[0];
		if (maxct <= 0)
			maxct = 100000;

		// derive contributions of profile indices to cumulative vector
		// and estimate their chance of occurring in any given vector

		for (int i = 0; i < no; i++) {
			count = ct.array[vg[i]];
			if (count <= M)
				count = 0;

			pr[i] = (float)(count)/maxct;
			cn[i] = vf[i];
		}
 
		// subtract out expected contribution according
		// to binomial model with derived probabilities

		for (int i = 0; i < no; i++) {
			short sw = 0;
			if (pr[i] > 0. && pr[i] < prTHR) {
				double w = (cv[vg[i]] - nm*pr[i])/Math.sqrt(nm*pr[i]*(1.-pr[i]));
				if (vg[i] >= LMN && vg[i] < Parameter.MXN)
					w *= .20;
				sw = (short) w;
			}
			vf[i] = sw;
		}

		return no;
	}

}
