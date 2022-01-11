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
// AW file SimpleList.java : 14jul2021 CPM
// general n-gram listing utility

package gram;

public class SimpleList {

        private static final int LL = 128; // minimum list length

	protected short[] list; // ordered or unordered values
	protected short  count; // current count

	// create list of specified size, but not below a practical minimum
		
	public SimpleList (
		int n
	) {
		if (n < LL)
			n = LL;
		list = new short[n+1];
		count = 0;
	}

	// create a list of minimum size
		
	public SimpleList (
	
	) {
		this(LL);
	}

	// put a value at a specified list position, making room for it if needed
	
	public void insert (
		short g,
		int   k
	) {
		if (k > count)
			k = count;		
		for (int i = count; i > k; --i)
			list[i] = list[i-1];
		list[k] = g;
		count++;
	}
	
	// put value at current end of list
	
	public int insert (
		short g
	) {
		list[count] = g;
		return count++;
	}

	// clear out listed values
	
	public final void clear ( ) { count = 0; }

	// return number of values in list
		
	public final int length ( ) { return count; }

	// get the actual array of values
		
	public final short[] array ( ) { return list; }
	
	// get specified value in list
	
	public final short at ( int n ) { return (n >= count) ? null : list[n]; }
	
	// terminate list
	
	public final void terminate ( ) { if (count < list.length) list[count] = 0; }
	
}
