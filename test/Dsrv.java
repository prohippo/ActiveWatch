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
// Dsrv.java : 18sep2023 CPM
// scans a sequence of segments in a batch and returns those above a threshold
// with ranking of results 

package test;

import aw.AWException;
import aw.Parameter;
import aw.Subsegment;
import aw.Profile;
import aw.Control;
import aw.Format;
import object.SequentialScan;
import object.FullProfile;
import object.ProfileToUse;
import object.ProfileForMatch;
import java.io.*;

class InstrumentedScan extends SequentialScan {

	public int full;   // vector indices stored
	public int actual; // vector indices scanned

	public InstrumentedScan (
		int              bn, // batch to search
		int              sn, // starting subsegment`
		ProfileForMatch  ps  // profile
	) {
		super(bn,sn,ps);
	}

	public double next (

	) throws AWException {
		double sim = super.next();
		full += fullSum();
		actual += actualSum();
		return sim;
	}

}

class Rec {
	int    bn;  // for item batch
	int    sn;  //          segment number
	double ss;  // its significance of match
	Rec (int bns, int sns, double sss) {
		bn = bns;
		sn = sns;
		ss = sss;
	}
}

public class Dsrv {

	private final static int Nr = 250;
	private final static String sigma = "\u03C3";

	public static void main ( String[] a ) {

		int   pno = (a.length > 0) ? Integer.parseInt(a[0]) :   0;
		int   lim = (a.length > 1) ? Integer.parseInt(a[1]) : 100;
		float thr = (a.length > 2) ? Double.valueOf(a[2]).floatValue() : 6.0F;
		int   bno = (a.length > 3) ? Integer.parseInt(a[3]) :   0;

		System.out.print("scan up to " + lim + " vectors in batch " + bno + " with ");
		if (pno == 0)
			System.out.println("current profile");
		else
			System.out.println("profile " + pno);

		Rec[] recs = new Rec[Nr + 1]; // for sorting results
		int   recn = 0;               //

		try {
			ProfileForMatch top = new ProfileForMatch(new ProfileToUse(pno));

			Control ctl = new Control();
			int count = ctl.getBatchCount(bno);
			if (lim > count)
				lim = count;

			InstrumentedScan sc = new InstrumentedScan(bno,0,top);

			for (int i = 0; i < lim; i++) {
				double sm = sc.next();
				if (sm >= thr) {
					Rec nr = new Rec(bno,i,sm);
					int is;
					for (is = recn; is > 0; --is) {
						Rec or = recs[is - 1];
						if (or.ss >= nr.ss)
							break;
						recs[is] = or;
					}
					recs[is] = nr;
					if (recn < Nr)
						recn++;
				}
			}

			System.out.println(recn + " matches sorted");

			for (int j = 0; j < recn; j++) {
				Rec r = recs[j];
				String s = r.bn + "::" + r.sn;
				System.out.print(Format.it(s,8));
				Subsegment ss = new Subsegment(bno,r.sn);
				System.out.print(" (subsegment " + ss.sn + " in");
				System.out.print(" " + Format.it(ss.it,4) + ")");
				System.out.print(" @ " + Format.it(r.ss,4,1));
				System.out.println(sigma);
			}

			System.out.print  (recn + " vectors retrieved out of " + lim + " at ≥ ");
			System.out.print  (Format.it(thr,4,1));
			System.out.println(sigma);
			System.out.print  ("scanned " + sc.actual + "/" + sc.full);
			double percent = (100.*sc.actual)/sc.full;
			System.out.println(" = " + Format.it(percent,5,1) + " percent of vector indices");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
