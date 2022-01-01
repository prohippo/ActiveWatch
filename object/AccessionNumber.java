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
// AW file AccessionNumber.java : 31May00 CPM
// item number conversion

package object;

import aw.*;
import java.io.*;

public class AccessionNumber implements ISAMData {

	public static final int MXMN = 2147483000; // largest allowable number

	private static Control ctrl;
		
	private static int start; // lowest valid number
	
	private static int[] bn  = new int[Control.NTB];   // order of batches: bn[0]  oldest
	private static int[] bs  = new int[Control.NTB];   // batch relative accession starts 
	private static int[] bss = new int[Control.NTB+1]; // accession starts: bss[0] oldest
	private static int   bnm; // total item count in batches
	
	private int bns = -1;
	private int ins;

	private int number; // unique ID number for item

	// trivial constructor
	
	public AccessionNumber (
		int m
	) {
		init();
		number = m;
	}

	// convert b:m reference
			
	public AccessionNumber (
		int bn,
		int in
	) throws AWException {
		set(bn,in);
	}
	
	// reload control information
	
	public static void reset (
	) {
		bnm = 0;
		init();
	}
	
	// set accession number
	
	public final void set ( int m ) {
		number = m;
		bns = -1;
	}
	
	// set accession number indirectly
	
	public final void set (
		int bn,
		int in
	) throws AWException {
		init();
		if (bn >= 0 && bn < Control.NTB &&
			in >= 0 && in < ctrl.getBatchCount(bn))
			number = start + bs[bn] + in;
		else
			throw new AWException("bad item: " + bn + ":" + in);
	}

	// get accession number
		
	public final int value ( ) { return number; }
	
	// get base accession number
	
	public static final int base  ( ) { init(); return start; }
	
	// get count of active accession numbers
	
	public static final int count ( ) { init(); return bnm; }
	
	// get batch number
	
	public final int batch (
	) {
		convert();
		return bns;
	}
	
	// get index in batch
	
	public final int index (
	) {
		convert();
		return ins;
	}
	
	// convert to batch and item index
	
	private void convert (
	) {
		if (bns >= 0)
			return;
		int mn = number - start;
		if (mn < 0 || mn >= bnm) {
			bns = -1;
			return;
		}

		int j = 0;			
		for (; mn >= bss[j]; j++);
		--j;
		bns = bn[j];
		ins = mn - bss[j];
	}

	// initialize tables for faster computation
	
	private static void init (
	
	) {

		// do all this only once
		
		if (bnm > 0)
			return;

		// load control information
		
		ctrl = new Control();
		if (ctrl.nobs == 0)
			return;

		int k = ctrl.first();
		int km = k;

		bn[0] = k;
		bnm = ctrl.getBatchCount(km);
		
		int i = 1;
		for (; i < ctrl.nobs; i++) {
			k = ctrl.next(k);
			bss[i] = bs[k] = bnm;
			bn[i] = km = k;
			bnm += ctrl.getBatchCount(km);
		}
		bss[i] = MXMN + 1;

		// zero out rest of control batches as precaution
		
		for (; i < Control.NTB; i++) {
			k = ctrl.next(k);
			ctrl.setBatchCount(k,0);
		}

		start = ctrl.ndel;
		
	}

	// required by ISAMdata interface
	
	public final int compare ( ISAMData other ) {
		AccessionNumber an = (AccessionNumber) other;
		int m = an.value();
		if (number < m)
			return -1;
		if (number > m)
			return  1;
		else
			return  0;
	}

	public final int  size ( ) { return 4; }
	
	public final void read ( DataInput in ) throws IOException { number = in.readInt(); }
	
	public final void write ( DataOutput out ) throws IOException { out.writeInt(number); }
	
	public final ISAMData copy ( ) { return new AccessionNumber(number); }
	
}
