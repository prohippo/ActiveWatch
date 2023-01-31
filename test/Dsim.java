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
// Dsim.java : 30jan2023 CPM
// compute reduced inner product between two vectors

package test;

import aw.*;
import gram.*;
import object.*;
import java.io.*;

class IVL extends IndexVectorForLink {

	private static GramDecode gd;

	public static final void setMinimum ( int m ) { IndexVectorForLink.mvsum = m; }

	public static double mlp;
	public static double div;

	public final double getMultiplier ( ) { return mlp; }

	public final double getDivisor ( ) { return div; }

	public IVL (
		int bn,
		int xn
	) throws AWException {
		super(new IndexVector(bn,xn));
		if (gd == null) {
			GramStart gs = new GramStart();
			LiteralDecoding ld = new LiteralDecoding(gs.table);
			gd = new GramDecode(gs.map,ld);
		}
		expandIt();
	}

	public final void traceTerm (
		int   gram,
		int  other,
		float term,
		double sum
	) {
		System.out.print(Format.it(gd.toString(gram),12));
		System.out.print("(" + Format.it(gram,5) + "," + count + ")");
		System.out.print(" x " + other);
		System.out.print(" x " + Format.it(pw[gram],6,2));
		System.out.print(" = " + Format.it(term,5,2));
		System.out.print(" : sum = " + Format.it(sum,8,2));
		System.out.println();
	}

	public final void traceLimit (
		int    lim,
		double sum
	) {
		System.out.println(lim + " indices kept with total prob=" + Format.it(sum,6,4));
	}

	public final void traceSums (
		int vsum1,
		int vsum2
	) {
		System.out.println("subsegment 1 sum=" + Format.it(vsum1,4));
		System.out.println("subsegment 2 sum=" + Format.it(vsum2,4));
	}

	public final void compute (
		IVL ov
	) {
		double sim = scaledSimilarity(ov);
		System.out.print  ("raw weighted inner product sum=" + Format.it(sum,6,1));
		System.out.println(" from " + nhs + " hits");
		System.out.println("expected sum=" + Format.it(expected,6,1));
		System.out.println("standard deviation=" + Format.it(sigma,6,2));
		System.out.println("scaled similarity =" + Format.it(sim,6,2));
	}

}

public class Dsim {

	public static void main ( String[] av ) {
		if (av.length < 2) {
			System.out.println("USAGE: Dsim item1 item2");
			return;
		}
		System.out.println("Dsim utility");
		try {
			Item one = Reference.to(av[0]);
			Item two = Reference.to(av[1]);
			System.out.print("compute similarity between subsegments ");
			System.out.print  (one.bn + "::" + one.xn + " and ");
			System.out.println(two.bn + "::" + two.xn);
			IVL.mlp = (av.length > 2) ? Float.valueOf(av[2]).floatValue() : 10;
			IVL.div = (av.length > 3) ? Float.valueOf(av[3]).floatValue() : 0.5F;
			if (av.length > 4)
				IVL.setMinimum(Integer.parseInt(av[4]));
			IVL v1 = new IVL(one.bn,one.xn);
			IVL v2 = new IVL(two.bn,two.xn);
			v1.compute(v2);
		} catch (AWException e) {
			e.printStackTrace();
		}
	}

}
