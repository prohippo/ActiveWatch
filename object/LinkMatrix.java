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
// LinkMatrix.java : 16jan2023 CPM
// full link matrix for computations

package object;

import aw.*;

// generates the upper and lower triangles of a cluster link matrix
// in compressed form stored by rows, with each row element showing
// an item linked to plus a link significance

public class LinkMatrix {

	public  static final int    SCALF = 10;

	private static final float  UPPRL = 100F;
	private static final short  LKLMT = (short)(Link.MXTC + 1);

	public int[]   lrU; // link row index upper triangle
	public short[] lkU; // item linked to
	public short[] lvU; // value of link
	public int []  lrL; // link row index lower triangle
	public short[] lkL; // item linked to

	public int nrow;    // last row in link matrix
	public int nent;    // number of distinct link entries

	private double vm = 0; // maximum link value

	// start with empty matrix

	public LinkMatrix (

		int liml   // link array limit

	) throws AWException {

		lrU = new int[Link.MXTC + 2];
		lkU = new short[liml];
		lvU = new short[liml];
		lrL = new int[Link.MXTC + 2];
		lkL = new short[liml];

		Link.close();

		int[] nn = new int[Link.MXTC]; // source row numbers for backfill
		int[] np = new int[Link.MXTC]; // indices of next element in each row

		// initialize column backfill limits and allocation indices        

		int  neU = 0; //
		int  neL = 0; // allocation pointer for links        
		short no = 0; // number of saved rows for backfill
		short nno;    // value of no for next iteration
		short mx = 0;     // highest column referenced
		short mn = LKLMT; // lowest  column referenced yet to do
		short rw = 0; // row index


		// iterate on links from file

		Link lk = new Link(); // cluster link record
		lk.load();				
		short m = lk.from;    // linkage
		short n = lk.to;      //
		float v = lk.value;   // link strength

		for (short prw = 0;;) {

			// fill lower triangle to next row referenced, if any;
			// otherwise fill out remaining column entries from old rows

			rw = (m < Link.MXTC) ? m : mx; // (< IS the correct comparison)

			for (int i = prw + 1; i <= rw; i++) {
				lrL[i] = neL;
				lrU[i] = neU;

				// check whether row occurs as column in previous row

				if (i == mn) {

					nno = 0;
					mn = LKLMT;

					// fill in lower triangle row from upper
					// triangle links of previous rows

					for (int j = 0; j < no; j++) {
						int nnj = nn[j];
						int npj = np[j];
						if (lkU[npj] == i) {

							// add lower triangle link
							lkL[neL++] = (short) nnj;
							npj++;
							if (npj >= lrU[nnj+1])
								continue;
						}

						// squeeze backfill pointers
						if (mn > lkU[npj])
							mn = lkU[npj];
						nn[nno  ] = nnj;
						np[nno++] = npj;
					}
					no = nno;
				}
			}

			if (m > Link.MXTC)
				break;

			// put in upper triangle entries for next row,
			// saving offset to start of allocation

			nn[no  ] = rw;
			np[no++] = neU;

			if (mn > n)
				mn = n;
			while (m == rw) {

				lkU[neU] = n;
				if (vm < v)
					vm = v;
				if (v > UPPRL)
					v = UPPRL;

				lvU[neU++] = (short) Math.round(SCALF*v);

				lk.load();
				m = lk.from;
				n = lk.to;
				v = lk.value;

				if (m > Link.MXTC)
					m = n = LKLMT;

			}

			if (mx < lkU[neU-1])
				mx = lkU[neU-1];
			prw = rw;

		}

		// fill in limits for last row (this is a dummy)

		lrL[rw+1] = neL;
		lrU[rw+1] = neU;

		nrow = rw;
		nent = neU;

	}

	// print statistics

	public void show ( ) {

		System.out.println("2 x " + nent + " matrix entries in all");
		System.out.println("strongest link=" + vm);

	}

 	// reduces a row of similarity links by eliminating those
	// between items without an alternate path of length 2

	public int prune (

		int row  // row to prune

	) {
 
 		int i,j,k,m;
		int   jU,jL; // matrix indices
		int  np = 0; // count of links pruned

		// evaluate links for row with three cases corresponding to the three
		// possible positions of an intermediate item on an alternate path

		for (jU = lrU[row]; jU < lrU[row+1]; jU++) {

			if ((k = lkU[jU]) <= 0)
				continue;

			// case #1: precedes first item

			for (jL = lrL[row]; jL < lrL[row+1]; jL++) {
				if ((m = lkL[jL]) <= 0)
					continue;

				for (i = lrU[m]; i < lrU[m+1]; i++)
					if (lkU[i] == k)
						break;
				if (i < lrU[m+1])
					break;
			}

			if (jL < lrL[row+1])
				continue;

			// case #2: between first and second item

			for (j = lrU[row]; j < jU; j++) {
				if ((m = lkU[j]) <= 0)
					continue;

				for (i = lrU[m]; i < lrU[m+1]; i++)
					if (lkU[i] == k)
						break;
				if (i < lrU[m+1])
					break;
			}

			if (j < jU)
				continue;

			// case #3: after second item

			for (j = jU + 1, i = lrU[k]; j < lrU[row+1] && i < lrU[k+1]; ) {
				if (lkU[j] <= 0)
					j++;
				else if (lkU[i] <= 0)
					i++;
				else if (lkU[i] > lkU[j])
					j++;
				else if (lkU[i] < lkU[j])
					i++;
				else
					break;
			}

			if (j < lrU[row+1] && i < lrU[k+1])
				continue;

			// drop link

			lkU[jU] = (short)(-k);
			np++;
		}

		return np;

	}

	// drop links below threshold in a row

	public int drop (

		int  i, // row index
		int th  // fixed-point threshold

	) {

		int nd = 0;
		for (int j = lrU[i]; j < lrU[i+1]; j++) {
			if (lkU[j] > 0 && lvU[j] < th) {
				short m = lkU[j];
				lkU[j] = (short)(-m);
				nd++;

				for (int k = lrL[m]; k < lrL[m+1]; k++)
					if (lkL[k] == i) {
						lkL[k] = (short)(-lkL[k]);
						break;
					}
			}
		}
		return nd;

	}

	// set links used in clusters to zero and restore
	// pruned and dropped links between residuals

	public void narrow (

		boolean[] wh // which items are clustered

	) {

		for (short i = 1; i < wh.length; i++)
			for (int j = lrU[i]; j < lrU[i+1]; j++) {
				if (lkU[j] > 0) {
					int m = lkU[j];
					lkU[j] = 0;
					for (int k = lrL[m]; k < lrL[m+1]; k++)
						if (lkL[k] == i) {
							lkL[k] = 0;
							break;
						}
				}
				else if (!wh[i] && !wh[-lkU[j]]) {
					int m = lkU[j] = (short)(-lkU[j]);
					for (int k = lrL[m]; k < lrL[m+1]; k++)
						if (lkL[k] == -i) {
							lkL[k] = i;
							break;
						}
				}
			}

	}

} 
