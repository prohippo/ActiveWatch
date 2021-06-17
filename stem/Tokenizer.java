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
// AW File Tokenizer.class : 29Apr99 CPM
// extract tokens from a segment of English text

package stem;

import aw.FileAccess;

public class Tokenizer {

	private static final char NL = '\n';
	
	private static final String file = "substitutions";

	protected Extracter in; // to divide up source text
	
	private Stem   suffix;  // for language tables
	private Stop   table;
	private Stopat pattern;
	
	private static TokenSubstitution ts;
	
	public  static void reset ( ) { ts = null; }
	
	// constructor
	
	public Tokenizer (

		Stem   suffix, // suffixes
		Stop   table,  // stops
		Stopat pattern // stop patterns
		
	) {
		this.suffix  = suffix;
		this.table   = table;
		this.pattern = pattern;
		
		if (ts == null)
			ts = new TokenSubstitution(FileAccess.to(file));
	}
	
	private String text; // save for context
	
	// set input for tokens
	
	public void set (
	
		String text // source text
		
	) {
		in = new Extracter(text);
		this.text = text;
		pattern.reset();	
	}

	// put token into stemmed form and indicate stopping
	
	protected boolean stopped (
	
		Token token
		
	) {
		if (token.length() <= 0)
			return true;
			
		Inflex.inflex(token);
		suffix.stem(token);
		ts.substitute(token);
		
		int start = offset - length;
		char left,right;
		if (text == null)
			left = right = ' ';
		else {
			left  = (start == 0) ? NL : text.charAt(start-1);
			right = (offset == text.length()) ? NL : text.charAt(offset);
		}
		
		return (table.stop(token) > 0 || pattern.stopat(token,left,right));
	}
	
	public int offset; // position of token
	public int length; // original length

	protected Token token = new Token(); // preallocate
		
	// obtain next token, if any
	
	public Token get (
	
	) {
		String t;

		if (in == null)
			return null;
			
		for (;;) {

			// extract next lexical segment
			 
			t = in.get();
			
			if (t == null)
				return null;
			
			// record token source
			
			length = t.length();
			offset = in.os;
			
			// get reduced token
			
			token.set(t);
			if (!stopped(token))
				break;
			
		}
		
		return token;
	}
	
}