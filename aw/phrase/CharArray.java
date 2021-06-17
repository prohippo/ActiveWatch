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
// CharArray.java : 21Jul00 CPM
// a less restrictive, memory-efficient string class

package aw.phrase;

import aw.Letter;
import aw.SortableObject;
import stem.Token;

public class CharArray implements SortableObject {

	protected char[] array; // string buffer
	protected int offset;   // offset for start of string
	protected int limit;    // end of string
	
	protected String text;  // original source
	
	private static final char[] empty = { 0 }; // null-terminated

	// create from regular string class
	
	public CharArray (
		String text
	) {
		this(text.length());
		text.getChars(0,limit,array,0);
		array[limit] = 0;
		this.text = text;
	}
	
	// for array of specified length
	
	public CharArray (
		int length
	) {
		offset = 0;
		limit = length;
		array = new char[limit + 1];
	}
	
	// for empty array
	
	public CharArray (
	
	) {
		array = empty;
		text = "";
	}
	
	// analog to substring()
	
	public CharArray subarray (
		int offset,
		int limit
	) {
		CharArray ca = new CharArray();
		setSubarray(ca,offset,limit);
		return ca;
	}
	
	// assign from another CharArray
	
	public final void assign (
		CharArray ca
	) {
		array  = ca.array;
		offset = ca.offset;
		limit  = ca.limit;
		text   = ca.text;
	}
	
	// assign from Token
	
	public final void assign (
		Token to
	) {
		offset = 0;
		limit = to.length();
		for (int i = 0; i < limit; i++)
			array[i] = Letter.toChar(to.array[i]);
		array[limit] = 0;
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
	
	// shared code for creating substring
	
	protected final void setSubarray (
		CharArray ca,
		int offset,
		int limit
	) {
		ca.array = array;
		ca.offset = (this.limit < offset) ? this.limit : offset;
		ca.limit = (this.limit < limit) ? this.limit : limit;
		ca.text = text;
	}
	
	// fill array from bytes
	
	public final CharArray fillBytes (
		byte[] b,
		int    o,
		int    n
	) {
		for (int i = 0; i < n; i++)
			array[i] = (char) b[o++];
		array[n] = 0;
		offset = 0;
		limit = n;
		return this;
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
	
	// analogous to String method
	
	public final int indexOfIgnoringCase (
		char x
	) {
		char xu = Character.toUpperCase(x);
		for (int o = offset; o < limit; o++)
			if (Character.toUpperCase(array[o]) == xu)
				return o - offset;
		return -1;
	}
	
	// current position in buffer
	
	public final int position ( ) { return offset; }
	
	// advance position in buffer
	
	public final void skip ( int n ) { offset += n; }
	
	// advance position in buffer
	
	public final void skip ( ) { offset++; }
	
	// like for String or StringBuffer
	
	public final int length ( ) { return limit - offset; }
	
	// anything left in buffer
	
	public final boolean notEmpty ( ) { return (limit > offset); }
	
	// like for String, but with no explicit check
	
	public final char charAt ( int  n ) { return array[offset + n]; }
	
	// like for StringBuffer, but with no explicit check
	
	public final void setCharAt ( int  n, char x ) { array[offset + n] = x; }
	
	// move a character with no explicit check
	
	public final void moveChar ( int n, int m ) { array[offset + n] = array[offset + m]; }
	
	// put character back with no explicit check
	
	public final void putCharBack ( char x ) { array[--offset] = x; }
	
	// get as String with changes
	
	public final String toString (
	
	) {
		return new String(array,offset,limit - offset);
	}
	
	// get portion as String without changes
	
	public final String getSubstring (
		int os,
		int ln
	) {
		return text.substring(offset + os,offset + ln);
	}
	
	// copy segment into another array without changes
	
	public final void copyChars (
		char[] a,
		int    n
	) {
		int o = offset - n;
		for (int i = 0; i < n; i++)
			a[i] = text.charAt(o++);
		a[n] = 0;
	}
	
	// skip non-alphanumeric at start of segment
	
	public final int align (
		int n
	) {
		int o = offset - n;
		for (; o < offset; o++)
			if (Character.isLetterOrDigit(text.charAt(o)))
				break;
		return offset - o;
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
	
	////////
	//////// implement strspn()
	////////
	
	// check for sequence of specified chars
	
	public final boolean span (
		int    n, // characters to check
		String p  // characters to match
	) {
		for (int i = 0; i < n; i++)
			if (p.indexOf(Character.toUpperCase(array[offset+i])) < 0)
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
		for (int i = 0; i < n; i++)
			ss[i] = Character.toUpperCase(s[o++]);
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