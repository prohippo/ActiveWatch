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
// AW file UpdaterMain.java : 11aug2021 CPM
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
		boolean add;
		boolean old = true;
		UpdaterMain x;
		
		Banner banner = new Banner("Updater");
		banner.show();

		// drop batch if argument is '-', otherwise add

		if (av.length > 0 && av[0].charAt(0) == '-') {
			add = false;
			if (av[0].length() == 1)
				old = true;
			else
				old = (av[0].charAt(1) != 'n' && av[0].charAt(1) != 'N');
		}
		else
			add = true;
			
		try {
		
			x = new UpdaterMain();
			nm = x.co.totalCount();
			n = x.run(add,old);

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
