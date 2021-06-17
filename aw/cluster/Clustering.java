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
// Clustering.java : 08Oct02 CPM
// basic clustering algorithm

package aw.cluster;

import aw.*;

public class Clustering {

	public short[] ncls; // cluster assignment for each item
	public short[] nlnk; // cluster node linkage for item
	public short[]  hiz; // strongest cluster link
	public short[]  loz; // weakest
	public short[] vcnt; // count of links for an item
 
	public short  count; // current number of clusters
	public short  limit; // highest cluster number used
	public short[] clsh; // cluster list heads
	
	private LinkMatrix matrix;

	// allocate data structures for minimal spanning tree
	
	public Clustering (
	
		LinkMatrix x
		
	) {
		int n = x.nrow + 1;

		// allocate
			
		ncls = new short[n];
		nlnk = new short[n];
		hiz  = new short[n];
		loz  = new short[n];
		vcnt = new short[n];
		clsh = new short[n];
		
		// initialize cluster lists

		count = 0;
		limit = 0;
		
		for (int i = 0; i < n; i++) {
			ncls[i] = -1;
			clsh[i] = -1;
		}
		
		matrix = x;
		
	}

	// produces a set of clusters from a set of links with
	// a variation of the minimal spanning tree algorithm
	// and returns the number of items clustered
	
	public int cluster (
	
		int row  // row to process
		
	) throws AWException {
	
		short first;  // in link
		short second; // in link
		short p,q;    // cluster list indices
		
		int no = 0;   // number of new clusters formed

		for (int j = matrix.lrU[row]; j < matrix.lrU[row+1]; j++) {

			// link dropped?

			if (matrix.lkU[j] <= 0)
				continue;

			// get cluster for next link

			first = matrix.lkU[j];
			p = ncls[first];
			if (p < 0) {
			
				// new clustered for unclustered item
				
				no++;
				p = allocate();
				insert(first,p);
				vcnt[first] = 0;
				hiz[p] = 0;
				loz[p] = 10000;
				
			}
			else if (clsh[p] < 0)
				System.err.println("item " + first + " in null cluster " + p);

			hiz[p] = hiz[p] >= matrix.lvU[j] ? hiz[p] : matrix.lvU[j];
			loz[p] = loz[p] <= matrix.lvU[j] ? loz[p] : matrix.lvU[j];

			// get cluster for second item

			second = (short) row;
			q = ncls[second];
			
			// merge clusters, if necessary
			
			if (q < 0) {
				no++;
				insert(second,p);
				vcnt[second] = 0;
			}
			else if (p != q) {
				if (clsh[q] < 0)
				System.err.println("item " + second + " in null cluster " + p);

				connect(p,q);
				if (clsh[p] < 0)
					System.err.println("bad join of " + p + " and " + q);

				hiz[p] = (hiz[p] >= hiz[q]) ? hiz[p] : hiz[q];
				loz[p] = (loz[p] <= loz[q]) ? loz[p] : loz[q];
			}

			// update linkage counts

			vcnt[first]++;
			vcnt[second]++;

		}
		
		return no;
	}
    
	// gets the index of the first free cluster list

	private short allocate (
	
	) throws AWException {
		for (short k = 0; k < clsh.length; k++)
			if (clsh[k] < 0) {
				count++;
				if (limit < k)
					limit = k;
				return k;
			}
		throw new AWException("cluster overflow");
	}
 
	// inserts a new item into a sorted cluster list

	private void insert (

		int item,
		int clsn

	) {
		ncls[item] = (short) clsn;

		short nb = -1;
		short n  = clsh[clsn];
		while (n > 0) {
			if (item < n) break;
			if (item == n) return;
			nb = n;
			n = nlnk[n];
		}
		
		nlnk[item] = n;
		if (nb < 0)
			clsh[clsn] = (short) item;
		else
			nlnk[nb] = (short) item;
	}

	// joins two clusters having a common item
	// resulting in one fewer cluster

	private void connect (

		int p,  // cluster indices
		int q

	) {
		short np = clsh[p];
		short nq = clsh[q];

		// start merging cluster lists

		short lp = 0;
		while (np > 0 && nq > 0) {
			if (np < nq) {
				nlnk[lp] = np;
				lp = np;
				np = nlnk[np];
			}
			else {
				ncls[nq] = (short) p;
				nlnk[lp] = nq;
				lp = nq;
				nq = nlnk[nq];
			}
		}

		// combine clusters and deallocate one

		nlnk[lp] = np >= nq ? np : nq;
		for (; nq > 0; nq = nlnk[nq])
			ncls[nq] = (short) p;
		clsh[p] = nlnk[0];
		clsh[q] = -1;
		--count;
	}
    
    // break up a cluster for regrouping
    
	private static final int incm = 1; // fixed-point threshold increment
    
    private int th; // save new minimum threshold
    
    public int breakUp ( int k, short[] cm ) {
        th = loz[k] + incm;
        
        int nle = 0; // how many links dropped
        
        for (int i = clsh[k], j = 0; i > 0; i = nlnk[i]) {
            cm[j++] = (short) i;
            ncls[i] = -1;
            nle += matrix.drop(i,th);
        }
        
        --count;
        clsh[k] = -1;

        return nle;
    }
    
    // get threshold for breakup
    
    public final int getThreshold ( ) { return th; }
    
    // recover unclustered
    
    public int recoverResiduals ( short[] cm, boolean[] wh ) {
        int nr = 0;
        for (short i = 1; i < ncls.length; i++) {
            if (ncls[i] < 0)
                cm[nr++] = i;
            else
                wh[i] = true;
        }
        return nr;
    }
    
    public int getSize ( int k ) {
        if (k < 0 || k > limit)
            return -1;
        int no = 0;
        for (int n = clsh[k]; n > 0; n = nlnk[n])
            no++;
        return no;
    }
	
}
