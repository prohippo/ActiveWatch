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
// Dkyw.java : 19feb2023 CPM
// get descriptive keys for profile

package test;

import aw.*;
import object.KeyDerivation;
import object.FullProfile;
import object.ProfileToUse;

public class Dkyw {

	private static final int M = 16;

	public static void main ( String[] a ) {
		int n = a.length;
		if (n == 0) {
			System.out.println("usage: do DKYW id [...]");
			return;
		}
		try {
			Item[] it = new Item[n];
			for (int i = 0; i < n; i++)
				it[i] = Reference.to(a[i]);
			ProfileToUse pp = new ProfileToUse("profile");
			FullProfile  fp = new FullProfile(pp);
			KeyDerivation kd = new KeyDerivation();
			kd.set(fp.vector(),it,n,M);
			String[] ks = kd.rest();
			int m = ks.length;
			for (int j = 0; j < m; j++) {
				if (j%4 == 0)
					System.out.println();
				System.out.print(Format.it(ks[j],19));
			}
			System.out.println();
			System.out.println("DONE");
		} catch (AWException e) {
			e.printStackTrace();
		}
	}

}
