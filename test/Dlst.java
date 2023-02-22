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
// AW file Dlst.java : 22feb2022 CPM

package test;

import aw.*;
import object.*;

public class Dlst {

	public static void main ( String[] av ) {

		if (av.length == 0)
			return;
		int n = Integer.parseInt(av[0]);

		try {
			GappedProfileList ls = new GappedProfileList(n);
			int nit = ls.getCount();
			Item[] it = ls.getList();
			for (int i = 0; i < nit; i++) {
				if (i == ls.gap)
					System.out.println("     ----------------");
				IndexedItem x = new IndexedItem(it[i]);
				System.out.print(Format.it(i+1,3) + ") ");
				System.out.print(Format.it(x.bn,2) + "::");
				String t = "" +  x.index;
				System.out.print(Format.it(t,6) + " ");
				System.out.println(Format.it(x.score(),6,2));
			}
		} catch (AWException e) {
			e.printStackTrace();
		}

	}

}
