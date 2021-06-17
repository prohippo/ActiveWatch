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
// AW File Letter.java : 2May00 CPM
// letter definitions for AW analyses

package aw;

public class Letter {

	// define consonants and vowels in alphabet of 26 letters
	// plus 10 digits and 2 punctuation

	public final static int  NA   =  26; // size of alphabet
	public final static int  NAN  =  NA + 10;
	
	public final static int  NONE = 127; // high code > NAN
	
	public final static char APO  =  39; // ASCII apostrophe
	public final static char DOT  =  46; // ASCII period
	
	private final static boolean t = true, f = false;
	
	public final static boolean[] cns = { // consonants
		f,t,t,t,f,t,t,t,f,t,t,t,t,
		t,f,t,t,t,t,t,f,t,t,t,t,t,
		f,f,f,f,f,f,f,f,f,f,f,f
	};
 
	public final static boolean[] cnx = { // consonants - Y
		f,t,t,t,f,t,t,t,f,t,t,t,t,
		t,f,t,t,t,t,t,f,t,t,t,f,t,
		f,f,f,f,f,f,f,f,f,f,f,f
	};
 
	public final static boolean[] vws = { // vowels
		t,f,f,f,t,f,f,f,t,f,f,f,f,
		f,t,f,f,f,f,f,t,f,f,f,f,f,
		f,f,f,f,f,f,f,f,f,f,f,f
	};
	
	public final static boolean[] vwx = { // vowels - U
		t,f,f,f,t,f,f,f,t,f,f,f,f,
		f,t,f,f,f,f,f,f,f,f,f,f,f,
		f,f,f,f,f,f,f,f,f,f,f,f
	};
	
	public final static boolean[] spc = { // consonants + U - W - H
		f,t,t,t,f,t,t,f,f,t,t,t,t,
		t,f,t,t,t,t,t,t,t,f,t,t,t,
		f,f,f,f,f,f,f,f,f,f,f,f
	};

	public final static char[] from = {   // character equivalents
		'a','b','c','d','e','f','g','h','i','j','k','l','m',
		'n','o','p','q','r','s','t','u','v','w','x','y','z',
		'0','1','2','3','4','5','6','7','8','9',APO,DOT
	};
	
	// convert character to internal code
	
	public final static byte toByte ( char x ) {
		if (x > 127)
			return (byte) -1;
		else if (Character.isLetter(x))
			return (byte) (Character.toLowerCase(x) - 'a');
		else if (Character.isDigit(x))
			return (byte) (x - '0' + NA);
		else if (x == from[NAN])
			return (byte) (NAN);
		else if (x == from[NAN + 1])
			return (byte) (NAN + 1);
		else
			return (byte) -1;
	}
	
	// convert inner code to character
	
	public final static char toChar ( int b ) {
		return (b >= 0 && b < from.length) ? from[b] : DOT;
	}
	
	// check if alphabetic
	
	public final static boolean alphabetic ( int b ) {
		return (b < NA);
	}

	// check if numeric
	
	public final static boolean numeric ( int b ) {
		return (b >= NA && b < NAN);
	}

	// check if punctuation
	
	public final static boolean punctuating ( int b ) {
		return (b >= NAN);
	}

}

