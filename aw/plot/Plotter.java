// Copyright (c) 2023, C. P. Mah
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
// AW file Plotter.java : 19jan2013 CPM
// plot cluster profile matches along a time line
// by producing a .csv file for input to a spreadsheet app

package aw.plot;

import aw.*;
import object.ProfileToUse;
import object.ProfileForMatch;
import object.SequentialScan;
import java.io.*;

public class Plotter {

	private Short[] bins;            // how to bin profile hits
	private Profile         pro;     // reference profile
	private ProfileForMatch mpr;     // for computing scaled similarity
	private SequentialScan  scn;     // compute scaled similarity for batch

	private double minimumThreshold; // significance level for plotting

	private int count = 0;           // how many vectors to scan;

	private String fn;               // output .csv file name

	public Plotter (
		int bn,     // batch number
		int cn,     // cluster number
		Short[] bwd // bin widths for plotting
	) throws AWException {
		bins = bwd;
		pro = new ProfileToUse(cn);
		mpr = new ProfileForMatch(pro);
		scn = new SequentialScan(bn,mpr);

		minimumThreshold = pro.sgth; // by default

		Control ctl = new Control();
		count = ctl.getBatchCount(bn);

		fn = "PL" + String.valueOf(cn) + ".csv";
	}

	public void setThreshold ( double thr ) { minimumThreshold = thr; }

	public double threshold ( ) { return minimumThreshold; }

	public int minimumFeatureCount ( ) { return pro.nhth; }

	public int run (

	) throws AWException {
		if (count == 0) return 0;

		try {
			PrintStream out = new PrintStream(new FileOutputStream(fn));

			out.println("wd,ct,sg");
			int[] rec = { 0 , 0 , 0 }; // bin width, hit count, maximum score
			int j  = 0;
			int k  = 0;
			int kl = 0;
			int lstkl = bins[j];
			for (int i = 0; i < count; i++) {
				if (k == kl) {
					out.print(rec[0] + "," + rec[1] + ",");
					out.printf("%.2f\n",rec[2]/100.);
					rec[0] = rec[1] = rec[2] =  0;
					k  = 0;
					kl = (j < bins.length) ? bins[j++] : lstkl;
					lstkl = kl;
				}
				k++;
				rec[0]++;
				double ss = scn.next();
				if (ss >= pro.sgth) {
//					System.out.println("ss= " + ss);
					rec[1]++;
				}
				int ssn = (int)(100*ss);
				if (rec[2] < ssn) rec[2] = ssn;
			}
			out.print(rec[0] + "," + rec[1] + ",");
			out.printf("%.2f\n",rec[2]/100.);
			out.close();
		} catch (IOException e) {
			System.err.println(e);
			return 0;
		}
		return count;
	}

}

