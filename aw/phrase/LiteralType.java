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
//  LiteralType.java : 20Apr99 CPM
// look up text segments to be treated as single token

package aw.phrase;

import aw.Letter;
import java.io.*;

public class LiteralType {

	private LiteralPatternMatcher lmp;
	
	// initialize
	
	public LiteralType (
		DataInputStream is
	) throws IOException {
		lmp = new LiteralPatternMatcher(is);
	}
	
	// method for pattern matching
	
	public boolean match (
		CharArrayWithTypes s,
		SyntaxSpec x
	) {
		
		// check special patterns first
		
		if (s.dateType() || s.timeType()) {
			x.type = Syntax.timeType;
			x.modifiers = Syntax.functionalFeature;
			return true;
		}
		if (s.stateType()) {
			x.type = Syntax.nameType;
			x.modifiers = (byte)(Syntax.functionalFeature | Syntax.capitalFeature);
			return true;
		}
		
		// encode first character in text
		
		int chN = Letter.toByte(s.charAt(0));
		if (chN < 0)
			chN = Letter.NAN;
			
		if (lmp.match(s,chN,x) ||
			lmp.match(s,Letter.NAN + 1,x))
			return true;
		
		return false;
	}
	
}