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
// Dplt.java : 19sep2023 CPM
// plot binned similarity scores for profile over given range of vectors

package test;

import aw.AWException;
import aw.Parameter;
import aw.Subsegment;
import aw.Profile;
import aw.Control;
import aw.Format;
import object.SequentialScan;
import object.ProfileToUse;
import object.ProfileForMatch;
import java.io.*;

public class Dplt {

	public static void main ( String[] a ) {

		int   pno = (a.length > 0) ? Integer.parseInt(a[0]) :   0;
		int   bno = (a.length > 1) ? Integer.parseInt(a[1]) :   0;
		int   sno = (a.length > 2) ? Integer.parseInt(a[2]) :   0;
		int   lim = (a.length > 3) ? Integer.parseInt(a[3]) : 500;
		int   wid = (a.length > 4) ? Integer.parseInt(a[4]) :  10;

		System.out.print("bin up to " + lim + " similarity scores from " + sno);
		System.out.print(" in batch " + bno + " with ");
		if (pno == 0)
			System.out.println("default profile");
		else
			System.out.println("profile " + pno);

		try {
			ProfileForMatch top = new ProfileForMatch(new ProfileToUse(pno));

			Control ctl = new Control();
			int count = ctl.getBatchCount(bno);

			double sum = 0; // for binning
			int    num = 0; //

			SequentialScan sc = new SequentialScan(bno,sno,top);

			int lmt = sno + lim;
			if (lmt > count)
				lmt = count;

			for (int i = sno; i < lmt; i++) {
				double sm = sc.next();
				num++;
				if (sm < 0) {
					System.out.printf("negative score at %d\n",i);
					sm = 0;
				}
				if (num > 0 && num%wid == 0) {
					System.out.printf("%6.2f\n",sum/wid);
					sum = 0;
				}
				sum += sm;
			}
			if (sum > 0) {
				int w = num%wid;
				System.out.println(w + " left over");
				System.out.printf("%6.2f\n",sum/w);
			}

			System.out.print(num + " vectors scanned");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
