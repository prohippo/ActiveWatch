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
// Dixs.java

package test;

import aw.*;
import java.io.*;
import java.net.*;

public class Dixs {

	// dump index-- files
	
	public static void main ( String[] a ) {
		
		byte[] buf = new byte[8192];
		InputStream data;
		int  sis = -1;
		long sln =  0;
		String sfn = "";
		Index ix = null;
		Source src = null;
		Control x = new Control();	
		try {
			int bn = 0;
			int lm = 0;
			if (a.length > 0)
				bn = Integer.parseInt(a[0]);
			else if (x.nobs > 0)
				bn = x.last();
			if (a.length > 1)
				lm = Integer.parseInt(a[1]);
			else if (x.nobs == 0)
				lm = 24;
			else
				lm = x.getBatchCount(bn);
			System.out.println("dumping batch " + bn);
			for (int i = 0; i < lm; i++) {
				ix = new Index(bn,i);
				if (sis != ix.si) {
					sis = ix.si;
					src = new Source(bn,sis);
					sfn = src.getName();
					sln = 1000000000;
					System.out.print(sfn + ": ");
					if (a.length < 3)
						System.out.println();
					else {
						try {
							URL u = new URL(sfn);
							data = u.openStream();
							sln = 0;
							for (;;) {
								int nn = data.read(buf);
								if (nn <= 0)
									break;
								sln += nn;
							}
							data.close();
							System.out.println(sln + " bytes");
						} catch (IOException e) {
							System.out.println("UNAVAILABLE");
						}
					}
				}
				System.out.println(Format.it(i,4) + ") @" + ix.os);
				System.out.println("      sx=" + ix.sx + ", ns=" + ix.ns);
				System.out.println("      sj=" + ix.sj);
				System.out.println("      hs=" + ix.hs);
				System.out.println("      tl=" + ix.tl);
				if (ix.os + ix.hs + ix.tl > sln) {
					System.err.println("source=" + sfn);
					System.err.println("bad text: file length= " + sln);
				}
                                if (ix.hs + ix.tl < ix.sj) {
                                	System.err.println("source=" + sfn);
                                        System.err.println("bad subject offset: " + i);
                                }
			}
		} catch (EOFException e) {
			System.out.println("E-O-F");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ix != null)
			ix.close();
		if (src != null)
			src.close();
			
	}
	
}
