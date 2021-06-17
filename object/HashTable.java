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
// AW file HashTable.java : 24Apr99 CPM
// hash table for string keys

package object;

public class HashTable extends Hashing {

	public String[] array; // for saving hashed strings
	
	// initialize
	
	public HashTable (
	
		int n, // size of array (must be prime!)
		int m  // hash modulus
		
	) {
		super(m);
		array = new String[n];
	}
	
	// initialize with modulus same as size
	
	public HashTable (
	
		int n  // size of array (must be prime!)
		
	) {
		this(n,n);
	}
	
	// reinitialize hash table
	
	public final void clear (

	) {
		if (array == null)
			return;
		for (int i = 0; i < array.length; i++)
			array[i] = null;
	}

	// look up a string in a hash table
	
	public final int lookUp (

		String str // word to look up
 
	) {
		int hk = code(str);
		this.str = str;
		return hFind(hk);
	}
	
	////
	//// allow use with non-String key
	////
	
	private String str; // key as String
	
	// shared hash lookup
	
	protected final int hFind ( int hk ) {
	
		// set inverval for reprobe
		
		int in = hk + hk%2;
		if (in == 0)
			in = 1;
			
		// compare key against table entries
 
 		int hs = array.length;
		for (int i = 0; i < hs; i++) {
			String p = array[hk];
			if (p == null)
				return -(hk+1); // < 0, if not found
			else if (hCompare(p))
				return  (hk+1); // > 0, if found
			else {
				hk += in;
				if (hk >= hs)
					hk -= hs;
			}
		}
		return 0; // table full
	}
	
	// overrideable comparison of key against hash entry
	
	protected boolean hCompare ( String p ) {
		return p.equals(str);
	}
 
}


