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
// Dseq.java : 20nov2021 CPM
// show sequence or residual file

package test;

import aw.*;
import java.io.*;

public class Dseq {

	private static final String name = "sequence";

	public static void main ( String[] a ) {
		try {
			String fn = (a.length > 0) ? a[0] : name;
			Sequence sq = new Sequence(fn);
			System.out.print(sq.nmssg + " subsegments in ");
			System.out.print(sq.nmrun + " run");
			System.out.println(((sq.nmrun != 1) ? "s" : ""));
			for (int i = 0; i < sq.nmrun; i++) {
				Run r = sq.r[i];
				System.out.print  ("from " + r.bbn + "::" + r.ssn); 
				System.out.println(" (" + r.nss + " subsegments)");
				System.out.println(" @" + r.rfo + " (" + r.rln + " bytes)");
			}
		} catch (AWException e) {
			e.printStackTrace();
		}
	}

}
