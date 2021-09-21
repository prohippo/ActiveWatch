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
// AW File Token.java : 13sep2021 CPM
// convert string to AW token format

package stem;

import aw.Letter;

public class Token implements TokenForm {

	protected short length;        // actual  token size
	public static final int MXW = 48; // maximum token size
	public byte[] array = new byte[MXW]; // encoded characters

	// constructor for subclassing
	
	public Token (
	) {
	}
	
	// create token from string
	
	public Token (
		String s
	) {
		set(s);
	}
	
	// set token to string
	
	public void set (
		String s
	) {
		int k = s.length();
		length = 0;

		for (int i = 0; i < k; i++)
			append(s.charAt(i));
	}
	
	// set token to char array
	
	public void set (
		char[] a,
		int    k
	) {
		length = 0;

		for (int i = 0; i < k; i++)
			append(a[i]);
	}
	
	// add character to token
	
	private void append (
		char c
	) {
		if (c > 127)
			if (Translation.from(this,c))
				return;
		byte x = Letter.toByte(c);
		if (x >= 0)
			if (length < MXW)
				array[length++] = x;
	}
	
	// add coded byte to token
		
	public final void append (
		byte x
	) {
		if (0 <= x && x < Letter.from.length)
			if (length < MXW)
				array[length++] = x;
	}

	// reset token length
		
	public final void setLength (
		int n
	) {
		if (length > n)
			length = (short) n;
	}

	// get token length
		
	public final int length (
	) {
		return length;
	}
	
	// copy other token
	
	public final void set (
		Token to
	) {
		length = to.length;
		System.arraycopy(to.array,0,array,0,array.length);
	}

	// convert token back to string
	
	private static char[] a = new char[MXW];
	
	public final String toString (
	) {
		for (int i = 0; i < length; i++)
			a[i] = Letter.toChar(array[i]);
		return new String(a,0,length);
	}
        
        // get array
        
        public final byte[] toArray ( ) {
                return array;
        }
	
}
