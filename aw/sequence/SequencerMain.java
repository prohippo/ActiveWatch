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
// AW file SequencerMain.java : 10aug2021 CPM
// create run sequence file for clustering

package aw.sequence;

import aw.*;

public class SequencerMain extends Sequencer {

	public SequencerMain (
	) throws AWException {
		super();
	}

	public static void main (
		String[] av
	) {
		SequencerMain x;
		
		Banner banner = new Banner("Sequencer");
		banner.show();

		try {
			x = new SequencerMain();
			if (av.length == 0)
				x.run();
			else
				x.run(Integer.parseInt(av[0]),0);
			System.out.print(x.sq.nmssg + " subsegments in ");
			System.out.print(x.sq.nmrun + " run");
			if (x.sq.nmrun != 1)
				System.out.print("s");
			System.out.println("");
		} catch (AWException e) {
			e.show();
			e.printStackTrace();
		}
	}

}
