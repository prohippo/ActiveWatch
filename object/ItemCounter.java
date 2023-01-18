// Copyright (c) 2023, C. P. Mah
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
// ItemCounter.java : 17jan2023 CPM
// count up items and report on the ones seen most

package object;

import aw.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;

public class ItemCounter {

	class Record { // for value in hash table
		int count;    // how many times seen
		double minSg; // minimum significance
		double maxSg; // maximum

		public Record () {
			count = 0;
			minSg = 1000;
			maxSg = 0;
		}
	}

	class Entry {
		public String key;
		public int  count;
	}

	private Hashtable<String,Record> ht;

	private static final int Ne = 100;
	private Entry[] se; // selection array
	private int ne = 0; // how many selected

	private double  th; // minimum significance for counting

	// constructor

	public ItemCounter (
		double thr
	) throws AWException {
		ht = new Hashtable<String,Record>(5000);
		se = new Entry[Ne+1];
		th = thr;
	}

	// put item into hash tablel land count up

	public void tally (
		int bn,    // batch
		int xn,    // subsegment
		double sg  // significance
	) {
		if (sg < th) return;
		String key = String.valueOf(bn) + "::" + String.valueOf(xn);
		Record r = ht.get(key);
		if (r == null) {
//			System.out.println("NEW");
			r = new Record();
		}
		r.count++;
//		System.out.println("count= " + r.count);
		if (r.minSg > sg) r.minSg = sg;
		if (r.maxSg < sg) r.maxSg = sg;
		ht.put(key,r);
	}

//	how to initialize hash table quickly

	public void insert (
		int bn,     // batch
		int xn,     // subsegment
		int count,  // 
		double lSg, // significance
		double hSg
	) {
		String key = String.valueOf(bn) + "::" + String.valueOf(xn);
		Record r = new Record();
		r.count = count;
		r.minSg = lSg;
		r.maxSg = hSg;
		ht.put(key,r);
	}

//	report on frequency of various item types

	public void report (
		int min
	) {
		int k;
		int n = 0;
		Enumeration<String> e = ht.keys();
		while (e.hasMoreElements()) {
			n++;
			String key = (String) e.nextElement();
			Record r = ht.get(key);
			if (r.count < min) continue;
			Entry er = new Entry();
			er.key   = key;
			er.count = r.count;

			for (k = ne; k > 0; --k) {
				if (se[k-1].count >= er.count)
					break;
				se[k] = se[k-1];
			}
			se[k] = er;
			if (ne < Ne) ne++; 
		}

		for (int i = 0; i < ne; i++) {
			String key = se[i].key;
			Record r = ht.get(key);
			System.out.printf("%3d) %-10.10s %2d %5.2f %5.2f\n",
					i+1,key,r.count,r.minSg,r.maxSg);
		}

		System.out.println("selected " + ne + " out of " + n);
	}

	//// unit testing
	////

	public static void main ( String[] a ) {
		int    cnt = 1;
		double thr = 6;
		if (a.length > 0) cnt = Integer.parseInt(a[0]);
		if (a.length > 1) thr = Double.parseDouble(a[1]);
		System.out.println("cnt= " + cnt + ", thr= " + thr);

		String ll;
		try {
			InputStreamReader is = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(is);

			ItemCounter x = new ItemCounter(thr);
			System.out.print("> ");
			while ((ll = br.readLine()) != null) {
				ll = ll.trim();
				System.out.println(ll + " (" + ll.length() + ")");

				if (ll.length() < 4) break;
				int xn = Integer.parseInt(ll.substring(0,2));
				double sg = Double.parseDouble(ll.substring(2).trim());
				x.tally(0,xn,sg);
				System.out.print("> ");
			}
			System.out.println();
			x.report(cnt);
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
