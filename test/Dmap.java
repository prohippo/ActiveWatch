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
// Dmap.java : 21nov2021 CPM
// show current profile allocation map

package test;

import aw.*;
import object.*;
import java.io.*;

public class Dmap {

	static private char unused = '_'; // unused slot
	static private char actusr = 'U'; //   active user-defined
	static private char inausr = 'u'; // inactive
	static private char actnew = 'N'; // new AW-defined (always active)
	static private char actold = 'O'; // old   active AW-defined
	static private char inaold = 'o'; // old inactive
	static private int N = 10; // blocking size

	static private int block (
		Map mp, // allocation map
		int mi  // index into map array
	) {
		char[] b = new char[N];
		int i = 0;
		int im = mi + i;
		if (im == 0) {
			b[i++] = ' ';
			im++;
		}
		for (; i < N; i++, im++) {
			if (im > mp.limit() || !mp.defined(im))
				b[i] = unused;
			else if (mp.userType(im))
				if (mp.activeType(im))
					b[i] = actusr;
				else
					b[i] = inausr;
			else if (mp.newType(im))
				b[i] = actnew;
			else if (mp.activeType(im))
				b[i] = actold;
			else
				b[i] = inaold;
		}
		System.out.print(new String(b));
		return i;
	}
	
	static public void main ( String[] a ) {
		Map m = new Map();  // map of current profiles
		
		System.out.println("dumping profile allocation map");
		System.out.println(m.countAll() + " profiles allocated");
		System.out.println(m.countUser() + " user-defined");
		System.out.println("highest allocated= " + m.limit());

		int jm = 0;
		int M = Map.MXSP/(2*N);
		for (int j = 1; j <= M; j++) {
			System.out.print(String.format("%03d ",jm));
			jm += block(m,jm);
			System.out.print(" ");
			jm += block(m,jm);
			System.out.println();
			if (jm > m.limit()) break;
		}
		
	}
	
}
