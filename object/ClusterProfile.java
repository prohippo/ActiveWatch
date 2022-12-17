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
// AW file ClusterProfile.java : 09dec2022 CPM
// cluster profile generation from examples

package object;

import aw.*;
import java.io.*;

// creation of n-gram profile to describe a given cluster

public class ClusterProfile extends ClusterProfileBase {

	private static final int MULTPL = 10; // for setting minimum probability threshold
	private static final int MXM    =  9; // maximum number of items to consider

	private static final int MINLNG = 11; // minimum profile index count

	public ClusterProfile (

		int      count, // how many items
		Item[]    item, // item list
		int[]   weight  // item weights

	) throws AWException {
		this(count,item,weight,MULTPL,MINLNG);
	}

	// build profile from list of items

	public ClusterProfile (

		int      count, // how many items
		Item[]    item, // item list
		int[]   weight, // item weights
		int     multpl, // multiple for minimum n-gram probability
		int     minlng  // minimum profile length

	) throws AWException {
        	super();

		if (count == 0)
			return;

		int[] ord = new int[count];

		// get indices from selected items

		int nselect = select(count,weight,ord);

		ClusterIndexVector[] vs = new ClusterIndexVector[nselect];
		int[] ws = new int[nselect];
        
		for (int j = 0; j < nselect; j++) {
			int i = ord[j];
			iv = new IndexVector(item[i].bn,item[i].xn);
			vs[j] = new ClusterIndexVector(iv);
			ws[j] = weight[i];
		}

		combine(vs,ws,multpl,minlng);

	}

	private IndexVector iv;

	// close out objects no longer needed

	public void close (

	) {
		super.close();
		if (iv != null)
			iv.close();
	}

	// choose subset of items to process by setting weights

	private int select (

		int    count, // how many items
		int[] weight, // item weights
		int[] order   // ranking of items

	) {
		// normalize weights

		int mx = 0;
		for (int i = 0; i < count; i++) {
			order[i] = i;
			if (mx < weight[i])
				mx = weight[i];
		}

		if (mx == 0)
			return 0;

		for (int i = 0; i < count; i++)
			weight[i] = (weight[i]<<3)/mx;

		if (count <= MXM)
			return count;

		// select top MXM items by weight

		for (int i = 1; i < count; i++) {
			int oi = order[i];
			int wt = weight[oi];
			int j = i;
			for (; j > 0; --j) {
				int ojm = order[j-1];
				if (wt <= weight[ojm])
					break;
				order[j] = ojm;
			}
			order[j] = oi;
		}

		return MXM;
	}

}
