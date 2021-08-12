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
// AW file Dprb.java : 09aug2021 CPM
// dump probabilities

package test;

import aw.*;
import gram.*;
import java.io.*;

public class Dprb {

	static SelectProbabilities pb;
	static GramDecode gd;
	
	public Dprb (
		int n
	) throws AWException {
		pb = new SelectProbabilities(n);
		gd = new GramDecode();
	}
	
	public static void main ( String[] a ) {
	
		System.out.println("analysis of probabilities");

		String fx;	
		int n = (a.length > 0) ? Integer.parseInt(a[0]) : 16;
		try {
			Dprb x = new Dprb(n);
		} catch (AWException e) {
			e.show();
			return;
		}
		
		System.out.print("\nprobability range= " + Format.it(pb.rg.low,8,6));
		System.out.println(" : " + Format.it(pb.rg.high,8,6));
	
		System.out.print("\n" + pb.np + " non-zero indices ");
		System.out.println("with sum of probabilities=" + Format.it(pb.sump,8,6));

		int mnp = (int)(1/pb.array[0]);		
		System.out.println("based on " + mnp + " total occurrences of indices");

		if (pb.np > 0) {
			System.out.print("computed entropy = " + Format.it(pb.entropy(),4,1) + " bits, ");
			System.out.println(Format.it(pb.relativeEntropy(),4,1) + " percent of maximum");
		}
		System.out.println("(saved entropy= " + Format.it(pb.rg.entropy,4,1) + ")\n");

		double sum = 0;
		
		System.out.println(pb.ns + " indices with highest percentages of occurrence\n");
		for (int j = 0; j < pb.ns; j++) {
			int i = pb.gm[j];
			System.out.print(" " + Format.it(gd.toString(i),16));
			fx = Format.it(100.*pb.array[i],6,4);
			System.out.println(" (" + Format.it(i,5) + "):  percent=" + fx);
			sum += pb.array[i];
		}
		System.out.print("\ntotal percentage of top " + pb.ns + " occurrences= ");
		System.out.println(Format.it(100.*sum,5,2));
		
	}
	
}
