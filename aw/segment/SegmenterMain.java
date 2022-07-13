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
// AW File SegmenterMain.java : 30jun2022 CPM
// class for text segmenting application

package aw.segment;

import aw.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;

public class SegmenterMain {
	
	// initialize and process
	
	public static void main (
		String[] a
	) {

		String delims = "delims";
		if (a.length > 1 && a[0].equals("-d")) {
			delims = a[1];
			a = Arrays.copyOfRange(a,2,a.length);
		}
		int lvl = 0;
		if (a.length > 1 && a[0].equals("-l")) {
			lvl = Integer.parseInt(a[1]);
			a = Arrays.copyOfRange(a,2,a.length);
		}

		String[] items = (a.length > 0) ? a : new String[] { "text" };
		
		Banner banner = new Banner("Segmenter");
		banner.show();

		System.out.println("item delimiters= \"" + delims + "\"");
			
		try {
		
			Segmenter x;
			int n = 0;
			try {
				BufferedReader r = new BufferedReader(new FileReader(delims));
				x = new Segmenter(r,lvl);
				r.close();
			
				for (int i = 0; i < items.length; i++) {
					String file = items[i];
					String u = null;
					try {
						URL url = new URL(file);
						u = file;
					} catch (MalformedURLException me) {
						File f = new File(file);
						u = "file:" + f.getAbsolutePath();
					}
					System.out.println("reading \"" + u + "\"");
					n += x.run(u);
				}
			} catch (IOException e) {
				System.err.println("bad item DELIMITERS");
 				System.exit(1);
			}

			System.out.println(n + " new items processed");
		
		} catch (AWException e) {
			e.printStackTrace();
		}
	}
}
