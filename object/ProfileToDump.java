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
// ProfileToDump.java : 06mar2023 CPM
// with special methods for display

package object;

import aw.*;
import gram.*;
import object.ProfileToUse;
import java.io.*;

public class ProfileToDump extends ProfileToUse {

	private static GramDecode gd;

	public ProfileToDump ( String file ) throws AWException {
		super(file);
		initialize();
	}

	public ProfileToDump ( int n ) throws AWException {
		super(n);
		initialize();
	}

	public ProfileToDump ( ) throws AWException {
		this("profile");
	}

	public ProfileToDump ( Profile pp ) throws AWException {
		nhth = pp.nhth;
		shth = pp.shth;
		sgth = pp.sgth;
		uexp = pp.uexp;
		uvar = pp.uvar;
		gms  = pp.gms;
		wts  = pp.wts;
		trc  = pp.trc;
	        initialize();
	}

	private void initialize ( ) throws AWException {
		if (gd == null) {
                        GramStart gs = new GramStart();
                        LiteralDecoding ld = new LiteralDecoding(gs.table);
			gd = new GramDecode(gs.map,ld);
		}
	}

	private PrintStream out = System.out;

	public void setOutput ( PrintStream out ) {
		this.out = out;
	}

	public void showMatching ( ) {

		out.println("significance threshold= " + sgth);
		out.println("expected value= " + Format.it(uexp,8,6));
		out.println("variance      = " + Format.it(uvar,8,6));

	}

	public void showFiltering ( ) {

//		int tn = 0;
//		int k = trc[tn++];		

//		if (k == 0)
//			out.println("\nno filters");
//		else {
//			out.print("\n" + k + " filter");
//			if (k != 1)
//				out.print("s");
//			out.print(" active ");
//			out.println("(type=" + trc[tn++] + ")");
//		}

//		int n;

//		for (int i = 0; i < k; i++) {
//			out.print(Format.it(i,2) + ") thr=" + trc[tn++] + " :");
//			for (int j = 0; (n = trc[tn++]) < Profile.MXP; j++) {
//				if (j%6 == 0)
//					out.print("\n        ");
//				int g = gms[n];
//				out.print("[" + g + " " + gd.toString(g) + "]");
//			}
//			out.println();
//		}

	}

	public void showWeighting ( boolean full ) throws IOException {

		int[]   cta = null;
		float[] pba = null;
		double upper = 0;

		FastProbabilities pbs = null;
		Counts cts = null;
		Range  rng = null;

		if (full) {
			try {		
				cts = new Counts();
				pbs = new FastProbabilities();
				rng = new Range();
				cta = cts.array;
				upper = rng.high/4;
			} catch (Exception e) {
				if (cts == null)
					cta = new int[Parameter.MXI+2];
				if (rng == null)
					upper = 0;
			}
		}

		out.println("\nweights");

		for (int i = 0; i < Profile.MXP; i++) {
			int g = gms[i];
			if (g > Parameter.MXI)
				break;
			out.print(Format.it(i,3) + ": ");
			out.print(Format.it(gd.toString(g),10) + " ");
			out.print(Format.it(wts[i],3) + " ");
			if (full) {
				out.print("(" + Format.it(g,5) + ") ");
				float pb = pbs.at(g);
				if (pb >= 0) {
					out.print("(p=" + Format.it(pb,7,6));
					out.print((pb > upper ? "*" : " ") + ") ");
				}
				out.print("(c=" + Format.it(cta[g],4) + ")");
			}
			out.println();
		}

	}

}
