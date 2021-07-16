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
// Dvec.java : 16jul2021 CPM

package test;

import aw.*;
import object.IndexVector;
import java.io.*;

public class Dvec extends IndexVector {

	private class ByteBuffer extends ByteArrayOutputStream {
	
		public final void zero ( ) { count = 0; }
		
	}

	public Dvec ( int bn, int sn ) throws Exception {
		super(bn,sn);
	}
	
	public void dump ( ) {
		System.out.print("            ");
		for (int i = VSTART; i < vc.vb.length; i++) {
			if (i%16 == 0)
				System.out.println();
			int n = vc.vb[i] - ZERO;
			System.out.print(Format.it(n,4));
		}
		System.out.println();
	}
			
	public void list ( ) throws AWException {
		int extents = -1;
		int total = 0;
				
		System.out.println();
		ByteBuffer b = new ByteBuffer();
		PrintWriter bpw = new PrintWriter(b);
		for (int k = 0; next(); k++) {
			if (extents != extent) {
				extents = extent;
				if (k%8 != 0)
					bpw.println();
				bpw.flush();
				if (total > 0) {
					System.out.println(" (" + total + ")");
					System.out.print(b.toString());
					total = 0;
				}
				System.out.print("extent " + extent);
				k = 0;
				b.zero();
			}
			if (gram > Parameter.EB[extent+1]) {
				System.out.println(" **** **** **** ****");
				System.out.print(b.toString());
				throw new AWException("bad index in extent " + extent + ": " + gram);
			}
			total += count;
			bpw.print(Format.it(gram,6));
			bpw.print('|');
			bpw.print(count);
			if (k%8 == 7)
				bpw.println();
		}
		if (total > 0) {
			bpw.flush();
			System.out.println(" (" + total + ")");
			System.out.print(b.toString());
		}
		System.out.println();
		System.out.println();
	}
	
	private int check ( ) {
		while (next());
		return computedSum();
	}

	public static void main ( String[] av ) {
		boolean full = true; // whether to dump all vectors
	
		try {
			int bno = 0, sno = 0;
			int nit = 4;
			
			if (av.length > 0) {
				Item it = Reference.to(av[0]);
				bno = it.bn;
				sno = it.xn;
				if (av.length > 1)
					nit = Integer.parseInt(av[1]);
				full = false;
			}
			else {
				Control c = new Control();
				bno = c.last();
				if (c.noms[bno] == 0)
					bno = c.next(bno);
				nit = c.noms[bno];
			}
			
			double sml = 0, sms = 0;
			int    mxl = 0, mxs = 0;
			for (int k = sno; k < sno + nit; k++) {
				Dvec x = new Dvec(bno,k);
				int ssn = x.subsegmentIndex();
				int ln  = x.vc.vb.length;
				System.out.print("vector " + bno + "::" + k + " ");
				if (ln < VSTART + Parameter.NEX)
					throw new AWException("** bad length= " + ln);
				int vs = x.storedSum();
				if (!full) {
					System.out.println("\nsubsegment index= " + ssn);
					x.dump();
					x.reset();
					x.list();
					System.out.println(" stored sum   =" + vs);
					int smx = x.computedSum();
					if (vs != smx)
						System.out.print(" should be     " + smx);
					System.out.println();
				}
				System.out.print(" stored length=" + ln + " ");
				if (!full)
					System.out.println();
				else {
					sml += ln;
					sms += vs;
					if (mxl < ln)
						mxl = ln;
					if (mxs < vs)
						mxs = vs;
					int as = x.check();
					if (vs != as)
						if (vs != 1 || as != 0)
							throw new AWException("bad vector sum: " + as + " != " + vs);
				}
				int lnx = x.tell();
				if (lnx != ln)
					System.out.print("should be     " + lnx);
				System.out.println();
				if (!full)
					System.out.println("--------");
			}
			if (full) {
				System.out.println();
				System.out.print  ("mean vector length= " + Format.it(sml/nit,6,1));
				System.out.println(", maximum= " + mxl);
				System.out.print  ("mean index  count = " + Format.it(sms/nit,6,1));
				System.out.println(", maximum= " + mxs);
			}
			System.out.println("DONE");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

