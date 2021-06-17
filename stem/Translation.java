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
// Translation.java : 21Apr00 CPM
// handle non-ASCII letters

package stem;

import aw.Letter;

public class Translation {

	private static final char aUM = (char) 0xe4; // for ISO German
	private static final char oUM = (char) 0xf4;
	private static final char uUM = (char) 0xfc;
	private static final char esT = (char) 0xdf;

	private static final byte aBY = Letter.toByte('a');
	private static final byte oBY = Letter.toByte('o');
	private static final byte uBY = Letter.toByte('u');
	private static final byte sBY = Letter.toByte('s');
	
	public static boolean from ( Token t, char c ) {
		boolean done = true;
		switch (c) {
case aUM:
			t.append(aBY);
			break;
case oUM:
			t.append(oBY);
			break;
case uUM:
			t.append(uBY);
			break;
case esT:
			t.append(sBY);
			t.append(sBY);
			break;
default:
			done = false;
		}
		return done;
	}

}