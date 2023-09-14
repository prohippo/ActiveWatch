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
// Dktg.java : 14sep2023 CPM
// get keys for describing individual items

package test;

import aw.*;
import stem.*;
import object.TextSubsegment;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.*;

public class Dktg {

	private static final int K = 8000; // nominal Hashtable size
	private static final int N =   40; // selection limit

	private static class Rec {
		String key;
		int    count;
		Rec () {}
		Rec (String str,int cnt) {
			key   = str;
			count = cnt;
		}
	}

	private static Hashtable<String,Integer> ht  = new Hashtable<String,Integer>();
	private static Hashtable<String,Integer> hts = new Hashtable<String,Integer>();

	private static Rec[] reco  = new Rec[N+1]; // for aorting keys in descending order of frequency
	private static int   recon = 0;            // how many sorted keys

	public static void main (String[] a) {

		if (a.length < 2) {
			System.out.println("usage: DKTG keys id [id ...]");
			System.exit(0);
		}
		int nc = 0;
		int no = 0;
		String s,ss,st;

		try {
			BufferedReader in = new BufferedReader(new FileReader(a[0]));
			while ((s = in.readLine()) != null) {	
				s = s.trim();
				int k = s.indexOf(' ');
				if (k < 0) {
					ss = s;
					no = --nc;
				}
				else {
					ss = s.substring(0,k);
					st = s.substring(k,s.length()).trim();
//					System.out.println("k=" + k + " " +st);
					no = Integer.parseInt(st);
				}
				ht.put(ss,no);
			}
			in.close();
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}

		try { 
			Stem   mor = new Stem(new DataInputStream(new FileInputStream("sufs")));
			Stop   stp = new Stop(new DataInputStream(new FileInputStream("stps")));
			Stopat pat = new Stopat(new BufferedReader(new FileReader("stpats")));
			Tokenizer tkzr = new Tokenizer(mor,stp,pat);

			for (int i = 1; i < a.length; i++) {
				int b,n;
				int k = a[i].indexOf(':');
				if (k < 0) {
					System.err.print("bad ID: " + a[i]);
					System.exit(1);
				}
				b = Integer.parseInt(a[i].substring(0,k));
				n = Integer.parseInt(a[i].substring(k+2));

				TextSubsegment ts = new TextSubsegment(b,n);
				ss = ts.getText();
				tkzr.set(ss);
				Token t;
				hts.clear();
				while ((t = tkzr.get()) != null) {
					s = t.toString();
					if (ht.containsKey(s)) {
						int m = ht.get(s).intValue();
						if (!hts.containsKey(s))
							hts.put(s,m);
					}
                                }

				// sort keys and values from hash table

				recon = 0;

				Enumeration<String> ken = hts.keys();
				while (ken.hasMoreElements()) {
					String ks = ken.nextElement();
					int ct = hts.get(ks).intValue();
					Rec r = new Rec(ks,ct);

					int j;
					for (j = recon; j > 0; --j) {
						Rec or = reco[j-1];
						if (or.count >= r.count)
							break;
						reco[j] = or;
					}	
					reco[j] = r;
					if (recon < N)
						recon++;
				}

				// print results

				System.out.printf("---- %d:%s",b,n);

				for (int jk = 0; jk < recon; jk++) {
					if (jk%10 == 0) System.out.println();
					Rec r = reco[jk];
					System.out.printf(" %-10.10s",r.key);
				}
				System.out.println();
			}
		} catch (AWException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (EOFException e) {
			System.err.println(e);
		}

	}

}
