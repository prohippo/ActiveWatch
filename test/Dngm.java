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
// AW file Dngm.java : 12aug2023 CPM
// sjow sum of probabilities for each class of n-grams

package test;

import aw.*;
import gram.*;
import java.io.*;

public class Dngm {

	private static GramStart       gs;
	private static LiteralDecoding ld;
	private static GramDecode      gd;
	private static float[] p;

	private static int slice ( int ns , int lm , String s ) {
		double sum = 0;
		double min = 1;
		double max = 0;

		int nz = 0;
		for (int i = ns; i < lm; i++) {
			float dp = p[i];
			if (dp > 0.0) {
				Pair pp = select[count];
				pp.insert(i,dp);

				nz++;
				sum += dp;
				if (min > dp) min = dp;
				if (max < dp) max = dp;
//				System.out.println("gd=" + gd);
				if (gd != null) {  // checking whether n-grams will be printed
					pp = select[count];
					pp.insert(i,dp);

					int k;
					for (k = count; k > 0; --k) {
						if (select[k-1].prob >= pp.prob) break;
						select[k] = select[k-1];
					}
					select[k] = pp;
					if (count < limit) count++;
//					System.out.println("count=" + count);
				} 
			}
		}
		System.out.println(s);
		System.out.println("total prob=" + Format.it(sum,8,6) + " for " + nz + " indices");
		System.out.println("min=" + Format.it(min,8,6) + ", max=" + Format.it(max,8,6));
		System.out.println("--");
		return nz;
	}

	static class Pair {
		int   indx;  // of n-gram
		float prob;  // its probability

		void insert (int ix, float px) {
			indx = ix;
			prob = px;
		}

		void display ( ) {
			String ngms = " " + gd.toString(indx);
			System.out.println(String.format("%6d %-12s  p=%6f",indx,ngms,prob));
		}
	}

	private static final int limit = 16;
	private static int       count;
	private static final Pair[] select = new Pair[limit+1];

	public static void main ( String[] a ) {

		System.out.println("analysis of n-gram contributions");
		System.out.println();

		Probabilities pb = null;

		try {
			pb = new Probabilities();
		} catch (AWException e) {
			System.err.println(e);
			System.exit(1);
		}
		for (int j = 0; j <= limit; j++)
			select[j] = new Pair();

		p = pb.array;

		int bo = 1;
		int to = Parameter.MXI;
		int n = -1;

		if (a.length > 0) {
			n = Integer.parseInt(a[0]);
			if (n < 0 || n > 5) {
				System.err.println("bad argument: " + a[0]);
				System.exit(1);
			}
			if (n == 0) {
				System.out.println("checking user-defined n-grams");
				to = Gram.IB2;
			}
			else if (n == 1) {
				System.out.println("no checking");
				System.exit(0);
			}
			else if (n == 2) {
				System.out.println("checking 2-grams");
				bo = Gram.IB2;
				to = Gram.IB3;
			}
			else if (n == 3) {
				System.out.println("checking 3-grams");
				bo = Gram.IB3;
				to = Gram.IB4;
			}
			else if (n == 4) {
				System.out.println("checking 4-grams");
				bo = Gram.IB4;
				to = Gram.IB5;
			}
			else if (n == 5) {
				System.out.println("checking 5-grams");
				bo = Gram.IB5;
			}

		}

		if (n >= 0) {
			count = 0;
			for (int j = 0; j <= limit; j++) select[j] = new Pair();
		
			try {
				gs = new GramStart();
				ld = new LiteralDecoding(gs.table);
				gd = new GramDecode(gs.map,ld);
			} catch (AWException e) {
				System.err.println(e);
				gd = null;
			}

		}

		int no = 0; // total count of non-zero n-grams
		if (n < 0) {
			no += slice(1,Gram.IB2,"User-defined n-grams");
			no += slice(Gram.IB2,Gram.IB3,"Alphanumeric 2-grams");
			no += slice(Gram.IB3,Gram.IB4,"Alphabetic 3-grams");
			no += slice(Gram.IB4,Gram.IB5,"Alphabetic 4-grams");
			no += slice(Gram.IB5,p.length,"Alphabetic 5-grams");
		}
		else if (n == 0) {
			no += slice(bo,to,"--");
		}
		else {
			no  += slice(bo,to,"");
		}
		System.out.println(no + " non-zero n-gram indices");

		if (n >= 0) {
			if (count == 0)
				System.out.println("no n-grams to report");
			else {
				System.out.println("most frequent are");
				for (int j = 0; j < count; j++) {
					Pair pp = select[j];
					pp.display();
				}
			}
		}

	}

}
