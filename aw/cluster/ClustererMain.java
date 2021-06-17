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
// AW file ClustererMain.java : 03Mar97 CPM
// derive cluster seeds

package aw.cluster;

import aw.*;

public class ClustererMain {

	static float dthr=(float)0.5; // link density threshold
	static int nitr= 3;           // number of rounds to cluster
	static int szmx=16;           // maximum cluster size

	public static void main (
		String[] av
	) {
		Clusterer x;
		
		if (av.length > 0) dthr = Float.valueOf(av[0]).floatValue();
		if (av.length > 1) nitr = Integer.parseInt(av[1]);
		if (av.length > 2) szmx = Integer.parseInt(av[2]);

		if (szmx < 3) szmx = 3; // must be at least minimum size
		
		Banner banner = new Banner("Clusterer");
		banner.show();
			
		try {
			x = new Clusterer();
			x.run(dthr,nitr,szmx);
		} catch (AWException e) {
			e.printStackTrace();
		}
	}

}