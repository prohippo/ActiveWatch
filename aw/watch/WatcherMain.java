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
// AW file SqueezerMain.java : 20nov2021 CPM
// look for text segments with low-probability n-gram indices

package aw.watch;

import aw.*;

public class WatcherMain {

	static int    nSelL =  12;        // number of segments to select
	static int    nSelH =   0;        // number of segments to select
	static int    minL  =  50;        // minimum vector length
	static String filn  = "residual"; // sequence file to work from

	public static void main (
		String[] av
	) {

		// convert any arguments

		if (av.length > 0)
			nSelL = Integer.parseInt(av[0]);
		if (av.length > 1)
			nSelH = Integer.parseInt(av[1]);
		if (av.length > 2)
			minL  = Integer.parseInt(av[2]);
		if (av.length > 3)
			filn  = av[3];

		Banner banner = new Banner("Watcher");
		banner.show();

		// process

		try {
			System.out.println("scanning segments in " + filn + " file");
			Watcher x = new Watcher(filn);
			x.run(minL,nSelL,nSelH);
//			System.out.print(String.format("%d segments to show\n",n));
			x.showSelection();
		} catch (AWException e) {
			e.printStackTrace();
		}
	}

}
