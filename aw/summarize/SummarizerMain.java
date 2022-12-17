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
// AW file SummarizerMain.java : 09dec2022 CPM
// derive n-gram profiles from cluster seeds

package aw.summarize;

import aw.*;

public class SummarizerMain {

	public static final float ASGTHR = 6.0F; // default significance threshold for match
	public static final int   MULTPL = 10;   // minimum probability as multiple of lowest
	public static final int   AMNPSZ = 16;   // 

	public static void main (
		String[] av
	) {
		float thr = (av.length > 0) ? Float.valueOf(av[0]).floatValue() : ASGTHR;
		int   mlp = (av.length > 1) ? Integer.parseInt(av[1]) : MULTPL;
		int   lpm = (av.length > 2) ? Integer.parseInt(av[2]) : AMNPSZ;

		Banner banner = new Banner("Summarizer");
		banner.show();
   // 
		try {
			Summarizer x = new Summarizer();
			x.run(thr,mlp,lpm);
			System.out.println("DONE");
		} catch (AWException e) {
			e.printStackTrace();
		}
	}

}
