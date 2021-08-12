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
// AW file Dclu.java : 11aug2021 CPM
// dump cluster seeds

package test;

import aw.*;
import object.LinkMapping;
import java.io.*;

public class Dclu {

	private static final int  MX = 32; // maximum cluster seed size

	public static void main ( String[] av ) {

		int   mm; // link index number
		int nitm; // item count
		int[] its = new int[MX]; // items of cluster seed
		int[] vw  = new int[MX]; // cluster weights

		Member      cm  = null;  // cluster member record
		LinkMapping lmp = null;  // conversion of link index numbers

		int nix = 0;             // item count
		int[] indx = null;

		try {
			lmp  = new LinkMapping();
			int count = lmp.countLinkIndex();
			indx = new int[count];
		} catch (AWException e) {
			System.err.println(e);
			System.exit(1);
		}

		try {

			int cn = 1;
			for (;;) {
				cm = new Member();  // reads clusters file
				mm = cm.index;
				if (mm > 0) indx[nix++] = mm;

				// collect items in cluster

				for (nitm = 0; mm > 0; nitm++) {
					if (nitm == MX) {
						System.err.println("cluster overflow");
						break;
					}

					its[nitm] = mm;

					vw[nitm] = cm.strength;

					try {
						cm = new Member();
						mm = cm.index;
						if (mm > 0) indx[nix++] = mm;
					} catch (EOFException e) {
						mm = -1;
					}
				}

				// show cluster seed members

				System.out.print(" cluster " + (cn++) +":");
				for (int j = 0; j < nitm; j++) {
					if (j%9 == 0)
						System.out.println();
					String x = " " + its[j];
					System.out.print(x);
					for (int k = x.length(); k < 6; k++)
						System.out.print(" ");
				}
				System.out.println();
			}

		} catch (EOFException e) {
			System.out.println();
			for (int j = 0; j < nix; j++) {
				int ix = indx[j];
				Item it = lmp.fromLinkIndex(ix);
				System.out.println(ix + " = " + it.bn + "::" + it.xn);
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
