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
// CharArrayWithTypes.java : 30jan2022 CPM
// with methods to support recognizing of basic types of lexical entitiess

package aw.phrase;

import aw.CharArray;

public class CharArrayWithTypes extends CharArray {

	// inherits
	// char[] array;
	// int   offset;
	// int    limit;

	// create array from substring

	public CharArrayWithTypes (
		String t
	) {
		super(t);
	}

	// create empty array

	protected CharArrayWithTypes (

	) {
	}

	// analog to substring(), overrides CharArray

	public CharArray subarray (
		int offset,
		int count
	) {
		return getSubarray(offset,count);
	}

	// implement date check

	public final boolean dateType (

	) {
		return update(DateType.match(array,offset));
	}

	// implement time check

	public final boolean timeType (

	) {
		return update(TimeType.match(array,offset));
	}

	// implement state of union check

	public final boolean stateType (

	) {
		return update(StateType.match(array,offset));
	}

	// common code for checks

	private boolean update (
		int k
	) {
		if (k == 0)
			return false;
		else {
			offset += k;
			return true;
		}
	}

	////////
	//////// class methods for string matching
	////////

	// check for nonalphabetic lexical terminator

	public static boolean end (
		char[] s,
		int    o
	) {
		if (s[o] == 0)
			return true;
		else
			return !Character.isLetterOrDigit(s[o]);
	}

	// drop single space only at current position

	public static int trim (
		char[] s,
		int    o
	) {
		if (s[o] == 0)
			return 0;
		int n = o;
		if (s[n] == ' ')
			n++;
		if (s[n] == '\r')
			n++;
		if (s[n] == '\n')
			n++;
		return n - o;
	}

}
