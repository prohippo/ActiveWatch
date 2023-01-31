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
// Dsmx.java : 30jan2023 CPM
// compute similarity measure for given item with current profile

package test;

import aw.*;
import gram.*;
import object.*;
import java.io.*;

class IV extends IndexVector {

	private static GramDecode gd;
	private static FastProbabilities fp;

	public IV ( 
		int bn,
		int xn
	) throws AWException, IOException {
		super(bn,xn);
		if (gd == null) {
			GramStart gs = new GramStart();
			LiteralDecoding ld = new LiteralDecoding(gs.table);
			gd = new GramDecode(gs.map,ld);
			fp = new FastProbabilities();
		}
	}

	int nlx;
	int nph;
	double ex;

	public int product (
		byte[]  fv,
		short[] hx
	) throws AWException, IOException {
		int n = 0;
		int sum = 0;
		int vsum = storedSum();
		ex = 0;
		nlx = nph = 0;
		while (next()) {
			if (fv[gram] != 0) {
				double term = fv[gram]*count;
				sum += term;
				if (gram < Parameter.MXN)
					nlx++;
				else
					nph++;
				hx[n++] = gram;
				float prb = fp.at(gram);
				double x = vsum*prb*count;
				ex += x;
				System.out.print(Format.it(gd.toString(gram),12));
				System.out.print("(" + Format.it(gram,5) + "," + count + ")");
				System.out.print(" x " + Format.it(fv[gram],3));
				System.out.print(" = " + Format.it(term,6,2));
				System.out.print(" ( " + Format.it(x,5,1) + " exp)");
				System.out.print(" : sum = " + Format.it(sum,8,2));
				System.out.println();
			}
		}
		return sum;
	}

}

public class Dsmx {

	public static void main ( String[] av ) {
		if (av.length < 2)
			return;

		try {

			int pn = Integer.parseInt(av[0]); // which profile to use
			Item it = Reference.to(av[1]);    // which item vector to match against

			// get profile and vector

			Profile pp = new ProfileToUse(pn);
			FullProfile pro = new FullProfile(pp);
			byte[] fv = pro.vector();
			int np = pro.count();
			IV iv = new IV(it.bn,it.xn);
			int fln = iv.storedSum();
			System.out.print("vector sum for subsegment " + it.bn + "::" + it.xn);
			System.out.println(" =" + fln);
			short[] hl = new short[Profile.MXP+1];
			int sum = iv.product(fv,hl);
			System.out.print(iv.nlx + "/" + pp.nhth + " lexical hits, ");
			System.out.println(iv.nph + "/" + pp.shth + " phonetic");

			// compute similarity

			double vars = fln*pp.uvar;
			double exps = fln*pp.uexp;
			double sgm = Math.sqrt(vars);
			double sim = (sum - exps)/sgm;
			System.out.println("  S =" + Format.it(sum,6));
			System.out.print("E[S]=" + Format.it(exps,6,1));
			System.out.print(" (" + Format.it(iv.ex,6,1) + "), ");
			System.out.println("with 1 sigma=" + Format.it(sgm,6,3));
			System.out.println("scaled similarity=" + Format.it(sim,6,1));
			if (iv.nlx < pp.nhth || iv.nph < pp.shth)
				System.out.println("*** not enough hits");

			// apply post filter

			short[] hx = new short[Profile.MXP+1];
	 		byte[] h = new byte[Profile.MXP];
	 		byte[] f = pp.trc;
	 		int k = 1;
			if (f[0] > 0) {
				int n = f[k++];
				String ap = (f[k++] > 0) ? " conjunctive" : " disjunctive";
				System.out.print(n + ap + " post-filter(s) active");
				int m = 0;
				for (int i = 0; hx[i] != 0; i++) {
					while (hx[i] != pp.gms[m])
						m++;
					h[m++] = 1;
				}
				for (int i = 0; --n >= 0; i++) {
					int nr = f[k++];
					int nh = 0;
					int ix;
					while ((ix = f[k++]) < Profile.MXP)
						nh += h[ix];
					System.out.print("filter " + Format.it(i,2) + ": ");
					System.out.println(nh + " hit(s) out of " + nr + " required");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
