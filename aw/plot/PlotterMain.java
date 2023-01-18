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
// AW file PlotterMain.java : 15jan2023 CPM
// cluster profile matches on a time line for batch of items

package aw.plot;

import aw.*;
import java.io.*;
import java.util.*;

public class PlotterMain {

	static private Plotter x;

	public static void main (
		String[] a
	) {
		int bn=0;        // number of data batch to process
		int cn=0;        // number of reference profile
		Short[] wd=null; // bin widths from file for plotting
		double mthr=0;   // minimum threshold for plot

                Banner banner = new Banner("Plotter");
                banner.show();

		try {
			if (a.length > 3) {
				System.out.println("minimum threshold to plot " + a[3]);
				mthr = Double.parseDouble(a[3]);
			}
			if (a.length > 2) {
//				System.out.println("a[2]= " + a[2]);
				if (a[2].substring(0,2).equals("w=")) { // all bin widths will be the same
					short w = (short) Integer.parseInt(a[2].substring(2));
					wd = new Short[1];
					wd[0] = w;
				}
				else {                                  // explicit bin widths defined in file
					ArrayList<Short> lwd = new ArrayList<Short>();
					try {
						String ls;
						FileInputStream fs = new FileInputStream(a[2]);
						BufferedReader rd = new BufferedReader(new InputStreamReader(fs));
						while ((ls = rd.readLine()) != null){
							short w = (short) Integer.parseInt(ls.trim());
							lwd.add(w);
						}
						rd.close();
					} catch (Exception e) {
						System.err.println(e);
						System.exit(1);
					}
					int sz = lwd.size();
					wd = new Short[sz];
					lwd.toArray(wd);
				}
			}
			else {
				wd = new Short[1];
				wd[0] = 1;
			}
			if (a.length > 1) cn = Integer.parseInt(a[1]);
			if (a.length > 0) bn = Integer.parseInt(a[0]);
			x = new Plotter(bn,cn,wd);
			System.out.println("processing batch " + bn + " with cluster " + cn);
			if (wd.length == 1)
				System.out.println("fixed bin width= " + wd[0]);
			else
				System.out.println(wd.length + " bin widths explicitly defined");
			if (mthr > 0)
				x.setThreshold(mthr);
			x.run();
		} catch (AWException e) {
			e.show();
			e.printStackTrace();
		}
  
	}
}
