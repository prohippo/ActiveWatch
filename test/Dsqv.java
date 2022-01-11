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
// AW file Dsqv.java : 30Sep02 CPM

package test;

import aw.*;
import object.*;

public class Dsqv {

	public static void main ( String[] a ) {
		VectorsForInnerProducts vs;
		try {
			vs = new VectorsForInnerProducts();
			PairwiseModel mo = vs.model;
			System.out.println("model parameters for squeezed vectors\n");
			System.out.println("sumps = " + Format.it(mo.sumps,10,8));
			System.out.println("sump2 = " + Format.it(mo.sump2,10,8));
			System.out.println("sump3 = " + Format.it(mo.sump3,10,8));
			System.out.println("sump4 = " + Format.it(mo.sump4,10,8));
			System.out.println("sump22= " + Format.it(mo.sump22,10,8));
			System.out.println("w1n   = " + Format.it(mo.w1n,10,8));
			
			System.out.println("computed for " + vs.count + " items\n");
			
			DsqvIndexVectors divs = new DsqvIndexVectors(vs.ivs);

			int m = (a.length > 0) ? Integer.parseInt(a[0]) :  0;
			int n = (a.length > 1) ? Integer.parseInt(a[1]) : -1;
			if (m == 0)
				divs.dump();
			else {	
				for (int i = 0; i < vs.count; i++) {
					if (n >= 0 && i != n)
						divs.position();
					else {
						System.out.print(Format.it(i,4) + ")");
						int stored = divs.storedSum();
						System.out.print(" stored sum=");
						System.out.print(Format.it(stored,4));
						int computed = divs.computeSum((m > 1 || i == n));
						System.out.print(" computed sum="+ Format.it(computed,4));
						if (stored != computed)
							System.out.print(" *MISMATCH*");
						System.out.println("");
						if (n >= 0)
							break;
					}
					divs.skip();
				}
			}
		} catch (AWException e) {
			e.show();
			e.printStackTrace();
		}
	}
	
}

class DsqvIndexVectors extends IndexVectors {

	public DsqvIndexVectors (
		IndexVectors ivs
	) {
		NX = Parameter.NLX;		
		bb = ivs.buffer();
	}
	
	// scan vector and compute sum
		
	public int computeSum (
		boolean show
	) {
		int n = 0;
		int sum = 0;

		while (next()) {
			if (show) {
				if (n++%8 == 0)
					System.out.println("");
				System.out.print(Format.it(gram,6) + "|" + count);
			}
			sum += count;
		}
		if (show)
			System.out.println("");
		return sum;
	}
	
	public void dump (
	
	) {
		byte[] ba = bb;
		int    la = ba.length;
		int    rw = 0;
		
		for (int i = 0; i < la; i++) {
			if (i%16 == 0)
				System.out.print("\n" + Format.it(rw++,3) + ") ");
			System.out.print(Format.it(ba[i] - ZERO,4));
		}
		System.out.println();;
	}

}