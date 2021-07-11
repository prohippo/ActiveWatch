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
// AW file Control.java : 10jul2021 CPM
// data base control class

package aw;

import java.io.*;

public class Control {

	public static final String file = "control"; // default file name
	public static final int NTB     = 32;        // maximum batch count
	public static final int BADBatch= -1;        // special marker
	
	public int nobs; // number of batches
	public int cubn; // current batch
	public int totb; // total batches ever
	public int ndel; // total items dropped
	
	public int[] noms = new int[NTB]; // number of items in each batch

	// get number of oldest batch
	
	public final int first (
	) {
		if (nobs == 0)
			return cubn;
		else {
			int k = cubn - nobs;
			if (k < 0) k += NTB;
			return k;
		}
	}
	
	// get number of newest batch
	
	public final int last (
	) {
		if (nobs == 0)
			return cubn;
		else {
			int k = cubn - 1;
			if (k < 0) k += NTB;
			return k;
		}
	}
	
	// get number of batch after this one
	
	public final int next (
		int k
	) {
		return (++k == NTB) ? 0 : k;
	}
	
	// get number of batch before this one
	
	public final int previous (
		int k
	) {
		return (k > 0) ? --k : NTB - 1;
	}
	
	// constructor
	
	public Control (
	) {
		try {
			load();
		} catch (IOException e) {
			System.out.println("empty control file");
		}		
	}
	
	// clear control record
	
	public void clear (
	) {
		totb -= nobs;
		cubn = first();
		nobs = 0;
		for (int i = 0; i < NTB; i++)
			noms[i] = 0;
	}
	
	// clear control record and reset
	
	public void clearAll (
	) {
		clear();
		cubn = 0;
	}

	// read control
		
	public void load (
	) throws IOException {
		String fn = FileAccess.to(file);
		DataInputStream in = new DataInputStream(new FileInputStream(fn));
		
		nobs = in.readInt();
		cubn = in.readInt();
		totb = in.readInt();
		ndel = in.readInt();
		for (int i = 0; i < NTB; i++)
			noms[i] = in.readInt();
		in.close();
	}

	// write control
	
	public void save (
	) throws IOException {
		String fn = FileAccess.to(file);
		DataOutputStream out = new DataOutputStream(new FileOutputStream(fn));
		
		out.writeInt(nobs);
		out.writeInt(cubn);
		out.writeInt(totb);
		out.writeInt(ndel);
		for (int i = 0; i < NTB; i++)
			out.writeInt(noms[i]);
		out.close();
	}

	// add a batch
	
	public int increment ( ) {
		if (nobs == NTB)
			return -1;
		nobs++;
		cubn++;
		if (cubn == NTB)
			cubn = 0;
		totb++;
		return cubn;
	}
	
	// drop oldest batch
	
	public void decrementOld ( ) {
		if (nobs == 0)
			return;
		int k = first();
		ndel += noms[k];
		noms[k] = 0;
		--nobs;
	}

	// drop newest batch
	
	public void decrementNew ( ) {
		if (nobs == 0)
			return;
		--nobs;
		--cubn;
		if (cubn < 0)
			cubn += NTB;
		noms[cubn] = 0;
		--totb;
	}

	// get item count for batch
	
	public final int getBatchCount (
		int n
	) {
		return noms[n];
	}
	
	// set item count for batch
	
	public final void setBatchCount (
		int n,
		int count
	) {
		noms[n] = count;
	}

	// get total count of items
	
	public int totalCount (
	) {
		int i,k;
		int nm = 0;
		
		k = first();
		for (nm = i = 0; i < nobs; i++) {
			nm += noms[k++];
			if (k >= NTB) k -= NTB;
		}
		return nm;
	}	

	// show current data status

	private static final int W = 8;

	public void dump ( ) {
		System.out.println(String.format("nobs= %2d, cubn= %2d",nobs,cubn));
		System.out.println(totb + " total batches processed");
		System.out.print(ndel + " deleted");
		for (int i = 0; i < NTB; i++) {
			if (i%W == 0) System.out.println();
			System.out.print(String.format("%6d",noms[i]));
		}
		System.out.println();
	}

	// unit test

	public static void main ( String[] as ) {
		int nob = (as.length > 0) ? Integer.parseInt(as[0]) : -1;
		int bno = (as.length > 1) ? Integer.parseInt(as[1]) : -1;
		Control ctl = new Control();
		if (ctl.noms[ctl.cubn] > 0)
			ctl.dump();
		if (nob >= 0 && bno >= 0) {
			ctl.nobs = nob;
			ctl.cubn = bno;
		}
		try {
			ctl.save();
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
