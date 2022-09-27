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
// AW file HashTable.java : 25sep2022 CPM
// special hash table for string keys with
// values for keys kept elsewhere
//
// (NOT the standard Java Hashtable class!!!)

package object;

import aw.ByteTool;
import java.util.Arrays;
import java.io.*;

public class HashTable extends ByteTool {

	//// saved string keys for hash search (must be a prime number)

	public String[] array; // mapped into only ASCII chars for matching

	//// default parameters for multiplicative hashing from Knuth, v III

	protected static final int MLTPLR = 27479; // prime multiplier for hash
	protected static final int NSHIFT =    15; // shift for bit selection
	protected static final int ENCDLN =     6; // maximum encoding length
	protected static final int FIVE   =     5;

	//// only alphanumeric 32 codes are unique (Q->K, X->K , Y->J, Z->S),
	//// allowing each code to be packed in only FIVE bits!

	protected static final byte[] packing =
		{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
		 10,11,12,13,14,15,16,17,18,19,
		 20,21,22,23,24,25,19,26,27,28,29,
		 30,31,19,18,27
		};

	// initialize

	public HashTable (

		int n  // size of array (must be prime!)

	) {
		array = new String[n];
	}

	// reinitialize hash table

	public final void clear (

	) {
		if (array == null)
			return;
		for (int i = 0; i < array.length; i++)
			array[i] = null;
	}

	// look up a char[] up to a limit in a hash table

	public final int lookUp (

		char[] txt,  // word in text to look up
		int    lmt   // how may chars to take
 
	) {
		if (lmt <= 0) return 0;
		if (lmt > txt.length) lmt = txt.length;
		char[] t = new char[lmt];
		System.arraycopy(txt,0,t,0,lmt);
		return lookUp(t);
	}

	// look up a char[] in a hash table

	public final int lookUp (

		char[] txt // word to look up
 
	) {
		hNew(txt);
		return hFind();
	}

	// look up a string in a hash table

	public final int lookUp (

		String strg // word to look up
 
	) {
		hNew(strg.toCharArray());
		return hFind();
	}

	////
	//// default hashing methods, which can be overridden
	////

	//   work area for encoding numerical hash for alphanumeric string key

	protected long   numeric;  // buffer for 64-bit hash arithmetic
	protected String key;      // canonic key form
	protected int    slot;     // where key or first empty was found

	// begin new hashing

	protected final boolean hNew ( char[] a ) {
		numeric = 0;
		StringBuffer sb = new StringBuffer();
		int ln = a.length;
		int j = 0, n = 0;
		for (; j < ln; j++) {
			char x = a[j];
			if (x < nc.length) {
				int cn = nc[x];
				if (cn < 0)
					sb.append('_');
				else {
					sb.append(unmapping[cn]); // to uppercase!
					if (n < ENCDLN) {
						hShift(cn);
						n++;
					}
				}
			}
		}
		if (n == 0)
			return false;
		else {
			key = sb.toString();
			return true;
		}
	}

	// add char to coding

	protected final void hShift ( int cn ) {
		numeric = (numeric<<FIVE) + (packing[cn]);
	}

	// compute actual starting hash code

	protected final int hCode ( ) {
		return ((int)((MLTPLR*numeric)>>NSHIFT)%(array.length));
	}

	// actual hash lookup

	protected final int hFind ( ) {

		int hk = hCode();  // initial probe
		int in = 500;      // interval for reprobe

		// compare key against table entries
 
 		int hs = array.length;
		for (int i = 0; i < hs; i++) {
			String p = array[hk];
			if (p == null) {
				slot = hk;
				return -(hk+1); // < 0, if not found
			}
			else if (p.equals(key)) {
				slot = hk;
				return  (hk+1); // > 0, if found
			}
			else {
				hk += in;
				if (hk >= hs)
					hk -= hs;
			}
		}
		return 0; // table full
	}


	// store key in empty slot found

	public final void store ( ) {
		hStore();
	}

	protected final void hStore ( ) {
		if (array[slot] == null)
			array[slot] = key;
	}

	////
	//// for debugging

	// dump table

	public void dump ( ) {
		for (int i = 0; i < array.length; i++)
			if (array[i] != null)
				System.out.println((i+1) + ") [" + array[i] + "]");
	}

	public static void main ( String[] a ) {
		String line,word,star;
		HashTable ht = new HashTable(4999);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while ((line = br.readLine()) != null) {
				word = line.trim();
				star = "";
				int n = ht.lookUp(word);
				if (n == 0) {
					System.out.println("table is full");
					break;
				}
				else if (n < 0) {
					n = -n;
					ht.hStore();
					star = " NEW";
				}
				System.out.println("[" + word + "] @" + n + star);
			}
			System.out.println("  !!!!");
			ht.dump();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
