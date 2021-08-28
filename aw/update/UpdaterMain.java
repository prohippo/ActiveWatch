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
// AW file UpdaterMain.java : 26aug2021 CPM
// top-level class for computing n-gram statistics

package aw.update;

import aw.*;
import java.io.*;

public class UpdaterMain extends Updater {

	public UpdaterMain ( ) throws AWException { super(); }

	public static void main (
		String[] av
	) {
		int   n = 0;
		int  nm = 0;
		boolean add;        // add items of newest batch to statistics
		boolean keep=true;  // keep oldest batch in statistics
		UpdaterMain x;

		Banner banner = new Banner("Updater");
		banner.show();

		// optional argument determines handling of batches

		if (av.length == 0)
			add = true;
		else if (av[0].equals("+-") || av[0].equals("-+")) {
			add  = true;
			keep = false;
		}
		else if (av[0].equals("-")) {
			add  = false;
			keep = false;
		}
		else
			add = true;

		try {

			x = new UpdaterMain();
			nm = x.co.totalCount();
			n = x.run(add,keep);

		} catch (AWException e) {
			e.printStackTrace();
			return;
		}

		int no = x.co.noms[bno];
		if (add)
			System.out.print("\nadd "  + no + " items of batch " + bno + " to ");
		else
			System.out.print("\ndrop " + no + " items of batch " + bno + " from ");

		System.out.println(nm + " items already in collection");

		System.out.print  ("\n" + String.format("entropy= %5.2f bits",x.sso.Hbits));
		System.out.println("\n(" + x.sso.rng.entropy + " percent)");
	}

}
