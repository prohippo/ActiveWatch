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
// Dpro.java : 08oct2022 CPM

package test;

import aw.*;
import gram.*;
import object.ProfileChecker;
import object.ProfileToDump;
import java.io.*;

public class Dpro {

	private static ProfileToDump pro = null;
	private static ProfileChecker chk;
	private static float[] pb;
	private static float   ph;

	public static void main ( String[] a ) {
		
		try {
		
			System.out.print("profile ");
			int pn = (a.length > 0) ? Integer.parseInt(a[0]) : 0;
			if (pn > 0) {
				pro = new ProfileToDump(pn);
				System.out.println(a[0]);
			}
			else {
				pro = new ProfileToDump();
				System.out.println(" from saved file");
			}
				
			pro.showMatching();
			pro.showFiltering();
			pro.showWeighting(true);
			
			chk = new ProfileChecker(pro,Profile.MXP,ProfileChecker.MWT);
			FastProbabilities fp = new FastProbabilities();
			Range rg = new Range();
			pb = fp.array();
			ph = rg.high/4;
			System.out.println("nominal vector matches");
			check( 250);
			check( 500);
			check(1000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private static void check (
		int ln
	) {
	
		System.out.print(Format.it(ln,4) + ": ");
		double x = chk.check(pro,ln,pb,ph);
		System.out.println(Format.it(x,5,1) + " standard deviations");
		
	}
		
	
}
