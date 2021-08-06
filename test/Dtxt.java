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
// Dtxt.java : 17jul2021 CPM

package test;

import aw.*;
import object.*;
import java.io.*;

public class Dtxt {
	
	public static void main ( String[] a ) {
		System.out.println("dumping text");
		if (a.length == 0)
			return;
		int nit = (a.length > 1) ? Integer.parseInt(a[1]) : 1;
		int b,n;
		int k = a[0].indexOf(':');	
		if (k < 0 || a[0].charAt(k+1) != ':') {
			if (k >= 0) {
				b = Integer.parseInt(a[0].substring(0,k));
				n = Integer.parseInt(a[0].substring(k+1));
			}
			else {
				b = 0;
				n = Integer.parseInt(a[0]);
			}
			int ni = 0;
			for (int i = 0; i < nit; i++) {
				try {
					ni = n + i;
					Index id = new Index(b,n);
					TextItem itm = new TextItem(b,ni);
					System.out.print("** item " + b + ":" + ni);
					String txt = itm.getFullText();
					int fl = txt.length();
					System.out.print(" full length=" + fl + " chars");
					String bdy = itm.getBody();
					int bl = bdy.length();
					System.out.print(", text length=" + bl + " chars");
					System.out.println(" versus " + id.tl + " bytes stored");
					if (a.length < 3) {
						System.out.println("--HEAD");
						print(txt.substring(0,fl - bl));
						System.out.println();
						System.out.println("--BODY");
						print(bdy);
						System.out.println("--");
					}
					else if (a[2].charAt(0) == 's') {
						String sbj = itm.getSubject();
						System.out.println("=" + sbj + "!");
					}
				} catch (Exception e) {
					System.err.println(b + ":" + ni + " " + e);
				}
			}
		}
		else {
			b = Integer.parseInt(a[0].substring(0,k));
			n = Integer.parseInt(a[0].substring(k+2));
			int ni = 0;
			for (int i = 0; i < nit; i++) {
				try {
					ni = n + i;
					TextSubsegment ssg = new TextSubsegment(b,ni);
					System.out.print("** subsegment " + b + "::" + ni);
					String txt = ssg.getText();
					System.out.println(" length=" + txt.length());
					if (a.length < 3)
						print(txt);
				} catch (Exception e) {
					System.err.println(b + "::" + ni + " " + e);
				}
			}
		}
		System.out.println("DONE");
	}
	
	private static void print ( String txt ) throws IOException {
		System.out.println();
		BufferedReader in = new BufferedReader(new StringReader(txt));
		for (String r; (r = in.readLine()) != null; ) {
			int rl = r.length();
			while (--rl > 0 && Character.isWhitespace(r.charAt(rl)));
			r = r.substring(0,rl + 1);
			System.out.println(r);
		}
	}
	
}
			
