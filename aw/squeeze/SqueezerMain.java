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
// AW file SqueezerMain.java : 09Mar00 CPM
// create run squeezed vector file

package aw.squeeze;

import aw.*;

public class SqueezerMain {

	static float minimum=  10F; // lower threshold multiplier
	static float maximum= 0.5F; // upper threshold divisor
	static int   mvsum  =   25; // minimum assigned vector sum

	public static void main (
		String[] av
	) {

		// convert any arguments
				
		if (av.length > 0)
			minimum = Float.valueOf(av[0]).floatValue();
		if (av.length > 1)
			maximum = Float.valueOf(av[1]).floatValue();
		if (av.length > 2)
			mvsum   = Integer.parseInt(av[2]);
		
		Banner banner = new Banner("Squeezer");
		banner.show();

		// process
					
		try {
			Squeezer x = new Squeezer(minimum,maximum);
			x.run(mvsum);
		} catch (AWException e) {
			e.printStackTrace();
		}
	}

}