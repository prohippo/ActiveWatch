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
// AW file Dngm.java : 25nov2021 CPM
// sjow sum of probabilities for each class of n-grams

package test;

import aw.*;
import gram.*;
import java.io.*;

public class Dngm {

	private static void slice ( int ns , int lm , float[] p , String s ) {
		double sum = 0;
		double min = 1;
		double max = 0;
		int nz = 0;
		for (int i = ns; i < lm; i++) {
			float dp = p[i];
			if (dp > 0.0) {
				nz++;
				sum += dp;
				if (min > dp) min = dp;
				if (max < dp) max = dp;
			}
		}
		System.out.println(s);
		System.out.println("total prob=" + Format.it(sum,8,6) + " for " + nz + " indices");
		System.out.println("min=" + Format.it(min,8,6) + ", max=" + Format.it(max,8,6));
		System.out.println("--");
	}

	public static void main ( String[] a ) {
	
		System.out.println("analysis of n-gram contributions");
		System.out.println();

		Probabilities pb;

		try {
			pb = new Probabilities();
		} catch (AWException e) {
			e.show();
			return;
		}
		
		float[] p = pb.array;
		slice(1,Gram.IB3,p,"Literal N-Grams");
		slice(Gram.IB2,Gram.IB4,p,"Alphanumeric 2-grams");
		slice(Gram.IB3,Gram.IB2,p,"Alphabetic 3-grams");
		slice(Gram.IB4,Gram.IB5,p,"Alphabetic 4-grams");
		slice(Gram.IB5,p.length,p,"alphabetic 5-grams");
	}
	
}
