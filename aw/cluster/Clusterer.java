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
// Clusterer.java : 16jqn2023 CPM
// derive cluster seeds

package aw.cluster;

import aw.*;
import object.LinkMatrix;
import java.io.*;

public class Clusterer {

	private static final int mnsz = 3; // minimum cluster size

	private LinkMatrix x; // cluster links
	private Clustering c; // class for clustering

	private short[] ipz;  // cluster indices for sorting

	private short[]  cm;  // cluster membership list

	private short[]  sz;  // cluster sizes

	private float   dthr; // link density threshold
	private int     mxsz; // maximum cluster seed size

	// initialization

	public Clusterer (

	) throws AWException {

		x = new LinkMatrix(Link.MXML);
		x.show();

		int n = x.nrow;
		System.out.println(n + " rows in link matrix");
		if (n == 0)
			throw new AWException("no links");

		ipz = new short[n+1];
		cm  = new short[n+1];
		sz  = new short[n+1];

		c = new Clustering(x);

	}

	// set up sequence file

	public void run (

		float dth, // link density threshold
		int   nit, // number of iterations
		int   mxs  // maximum cluster seed size

	) throws AWException {

		int iter; // iteration count

		if (x.nrow < 2) {
			System.out.println("no cluster seeds formed");
			save();
			return;
		}

		int nr = x.nrow;
		for (int i = 0; i < nr; i++)
			cm[i] = (short)(i + 1);
		group(nr);

		// first round of splitting and reclustering
		// to meet link density criteria

		dthr = dth;
		mxsz = mxs;
		iter = regroup(1);

		for (int j = 0; j < nit; j++) {

			// get links for residuals to try again

			System.out.println("\n" + c.count + " cluster seeds already found");
			int no = recover(nr);
			System.out.println("\nreclustering " + no + " residual items");

			group(no);

			// more splitting and reclustering

			iter = regroup(iter);

		}

		order();

		int nn = save();

		System.out.println(nn + " items clustered out of " + nr);

	}

	// form new cluster seeds from current links

	private void group (

		int nr

	) throws AWException {

		int sum = 0;
		for (int i = 0; i < nr; i++)
			sum += x.prune(cm[i]);
		System.out.println(sum + " unsupported links pruned");

		for (int i = 0; i < nr; i++)
			c.cluster(cm[i]);

	}

	// reform cluster seeds until all satisfy
	// criterion for density of links

	private int regroup (

		int   iter  // iteration running count

	) throws AWException {

		int im;	    // index of least dense cluster
		int mi;	    // index of largest     cluster
		int ml;     // size of largest cluster
		int mlc;    // minimum item link count
		float d;    // link density measure
		float dm;   // lowest density measure seen
		short lcls; // size of a cluster

		for (;;) {

			System.out.println("pass " + iter++);
			im = mi = ml = 0;
			dm = 10000.F;

			// collect current clusters

			for (int i = 0, j = 0; i <= c.limit; i++) {
				if (c.clsh[i] > 0) {
					ipz[j++] = (short) i;

					// get cluster size and minimum interlinkage for an item in it

					mlc = Link.MXTC;

					lcls = 0;
					for (int n = c.clsh[i]; n > 0; n = c.nlnk[n], lcls++)
						if (mlc > c.vcnt[n])
							mlc = c.vcnt[n];
					sz[i] = lcls;

					// note largest cluster not fully interlinked

					if (ml < lcls && mlc < lcls) {
						ml = lcls;
						mi = i;
					}

					// note the cluster with lowest link density,
					// computed as lowest number of links for
					// any item in the cluster divided by the
					// maximum possible number of links for any
					// item in the cluster

					d = mlc/(float)(lcls-1);
					if (dm > d) {
						dm = d;
						im = i;
					}
				}
			}

			// check for sparsely connected clusters
			// and for the largest cluster

			System.out.print("largest cluster=" + ml);
			System.out.print(" connectivity=" + Format.it(dm,4,2));
			System.out.println(":" + Format.it(dthr,4,2));

			if (ml <= mxsz && dm >= dthr)
				break;
			if (ml >  mxsz)
				im = mi;

			// set threshold to filter weak links in cluster

			int no = sz[im];
			System.out.print("recluster " + no);
			System.out.print(" items with links >= ");

			// break up least dense cluster

			int nle = c.breakUp(im,cm); // how many links dropped

			double thr = c.getThreshold()/LinkMatrix.SCALF;
			System.out.println(Format.it(thr,4,1));
			System.out.println(nle + " links dropped");

			if (nle == 0)
				break;

			// get clusters from links still remaining

			group(no);

			System.out.println();

		}

		return iter;

	}

	// get links of residuals for another
	// round of clustering

	private int recover (

		int no  // total number of items

	) {

		boolean[] wh = new boolean[no+1]; // indicate which are in cluster seeds

		int ns = c.recoverResiduals(cm,wh);

		x.narrow(wh);

		return ns;

	}

	// order clusters by size and strongest link with index array

	private void order (

	) {

		for (int k = 1; k < c.count; k++) {
			short ipzk = ipz[k];

			int j = k;
			for (; --j >= 0; ) {
				short ipzj = ipz[j];
				if (sz[ipzj] > sz[ipzk])
					break;
				else if (sz[ipzj] == sz[ipzk])
					if (c.hiz[ipzj] >= c.hiz[ipzk])
						break;
				ipz[j+1] = ipzj;
			}
			ipz[j+1] = ipzk;
		}

	}

	// write out cluster seeds by descending size

	private static short delim = (short)(-1);

	private int save (

	) throws AWException{

		int nmcl = 0;
		System.out.println();

		try {

			Member mbr  = null;
			double sclf = 1.0/LinkMatrix.SCALF;

			for (int i = 0; i < c.count; i++) {

				System.out.print("cluster " + (i+1));

				int k = ipz[i];
				nmcl += sz[k];

				for (int n = c.clsh[k]; n > 0; n = c.nlnk[n])
					new Member(n,c.vcnt[n] - 1);

				System.out.println(" :(" + sz[k] + " items)");
				System.out.print  ("  links from ");
				System.out.print  (Format.it(sclf*c.loz[k],4,1) + " to ");
				System.out.println(Format.it(sclf*c.hiz[k],4,1));
				mbr = new Member(delim,delim);

			}

			if (mbr == null)
				mbr = new Member(null);

			mbr.close();

		} catch (IOException e) {
			throw new AWException("cannot write clusters");
		}		

		return nmcl;

	}

}
