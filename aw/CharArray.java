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
// CharArray.java : 02feb2022 CPM
// a less restrictive, memory-efficient equivalent to String class

package aw;

import aw.SortableObject;
import stem.Token;

public class CharArray implements SortableObject {

	private static final String ellipsis = "\u2026";

	protected char[] array;  // string buffer
	protected int    offset; // offset for start of string
	protected int    limit;  // end of string buffer

	private static final char[] empty = { 0 }; // null-terminated

	// create from regular string class

	public CharArray (
		String text
	) {
		this(text.length());
		text.getChars(0,limit,array,0);
	}

	// for empty array of specified length

	public CharArray (
		int length
	) {
		offset = 0;
		limit = length;
		array = new char[limit];
	}

	// for empty array to be filled out later

	public CharArray (

	) {
		array = empty;
	}

	// analog to substring()

	public CharArray subarray (
		int offset,
		int limit
	) {
		return getSubarray(offset,limit);
	}

	// assign from another CharArray

	public final void assign (
		CharArray ca
	) {
		array  = ca.array;
		offset = ca.offset;
		limit  = ca.limit;
	}

	// assign from Token

	public final void assign (
		Token to
	) {
		offset = 0;
		limit  = to.length();
		if (limit > array.length) limit = array.length;
		System.arraycopy(to.array,0,array,0,limit);
	}

	// assign to null

	public final void assignNull (

	) {
		offset = 0;
		limit  = 0;
	}

	// reset offset to start

	public final void reset (

	) {
		offset = 0;
	}

	// create CharArray substring sharing buffer of CharArray string

	protected final CharArray getSubarray (
		int offset,
		int limit
	) {
		CharArray ca = new CharArray();
		ca.array  = array;
		ca.offset = (this.limit < offset) ? this.limit : offset;
		ca.limit  = (this.limit < limit)  ? this.limit : limit;
		return ca;
	}

	// fill array from char array

	public final CharArray fillChars (
		char[] b,
		int    o,
		int    n
	) {
		if (n >= array.length)
			n = array.length;
		System.arraycopy(b,o,array,0,n);
		offset = 0;
		limit  = n;
		return this;
	}

	// copy chars to another char array

	public final void copyChars (
		char[] b,
		int    n
	) {
		int m = array.length - offset;
		if (n > m) n = m;
		System.arraycopy(array,offset,b,0,n);
	}

	// copy chars to another char array

	public final int copyChars (
		char[] b,
		int    n,
		int    m
	) {
		int bo = offset - n;
		if (bo < 0 || m >= b.length) return 0;
		System.arraycopy(array,bo,b,0,m);
		return m;
	}

	// analogous to String method

	public final int indexOf (
		char x
	) {
		for (int o = offset; o < limit; o++)
			if (array[o] == x)
				return o - offset;
		return -1;
	}

	// analogous to String method, but with CharArray

	public final int indexOfIgnoringCase (
		char x
	) {
		for (int o = offset; o < limit; o++)
			if (array[o] == x)
				return o - offset;
		return -1;
	}

	// current position in buffer

	public final int position ( ) { return offset; }

	// like for String or StringBuffer

	public final int length ( ) { return limit - offset; }

	// anything left in buffer

	public final boolean notEmpty ( ) { return (limit > offset); }

	// move ahead in char array as buffer

	public void skip ( int n ) {
		offset += n;
	}

	// just like for String

	public final char charAt ( int  n ) {
		if (offset + n < limit)
			return array[offset + n];
		else
			return '\0';
	}

	// like for StringBuffer, but with no explicit check

	public final void setCharAt ( int  n, char x ) { array[offset + n] = x; }

	// move a character with no explicit check

	public final void moveChar ( int n, int m ) { array[offset + n] = array[offset + m]; }

	// put character back with no explicit check

	public final void putCharBack ( char x ) { array[--offset] = x; }

	// format as String for printing out

	public final String toString (

	) {
		String pre = (offset > 0) ? ellipsis : "";
		return pre + new String(array,offset,limit - offset);
	}

	// get portion as String without changes

	public final String getSubstring (
		int os,
		int ln
	) {
		if (ln <= 0)
			return "";
		int m = offset + os;
		int k = m + ln;
		if (m > limit) m = limit;
		if (k > limit) k = limit;
		if (m >= k)
			return "";
		else {
			int n = k - m;
			return new String(array,m,n);
		}
	}

	// identify text to match

	public final void set (
		int n
	) {
		set(array,offset,n);
	}

	// comparison for SortableObject

	public final int compareTo (
		SortableObject o
	) {
		CharArray a = (CharArray) o;
		int n = limit - offset;
		for (int k = offset, i = 0; i < n; k++, i++) {
			int ao = a.array[a.offset+i];
			if (array[k] > ao)
				return  1;
			if (array[k] < ao)
				return -1;
		}
		return (n == a.limit - a.offset) ? 0 : -1;
	}

	// convert to ASCII upper case only

	public final void remap (
	) {
		Transform.map(array);
	}

	////////
	//////// implement strspn()
	////////

	// check for sequence of specified chars, but for CharArray

	public final boolean span (
		int    n, // characters to check
		String p  // characters to match
	) {
		for (int i = 0; i < n; i++)
			if (p.indexOf(array[offset+i]) < 0)
				return false;
		return true;
	}

	////////
	//////// class methods for string matching
	////////

	private static final int N = 100; // maximum string match
	private static char[] ss = new char[N+1]; // save substring to match against

	// what text to match 

	public static void set (
		char[] s,
		int o,
		int n
	) {
		int k = s.length - o - 1;
		if (n > k)
			n = k;
		if (n > N)
			n = N;
		System.arraycopy(s,o,ss,0,n);
		ss[n] = 0;
	}

	// match uppercase string in char array

	public static boolean match (
		String p
	) {
		return match(p,p.length());
	}

	// match uppercase string in char array

	public static boolean match (
		String p,
		int n
	) {
		for (int i = 0; i < n; i++)
			if (ss[i] != p.charAt(i))
				return false;
		return true;
	}

}
