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
// AW File Tokenizer.java : 30sep2021 CPM
// extract tokens from a segment of English text

package stem;

import aw.FileAccess;
import aw.AWException;
import java.io.*;


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
//		System.out.println("token: " + token);
		suffix.stem(token);
//		System.out.println("token: " + token);
		ts.substitute(token);
//		System.out.println("token: " + token);

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
		String s;

		if (in == null)
			return null;

		for (;;) {

			// extract next lexical segment
			 
			s = in.get();

			if (s == null)
				return null;

			// save where token came from in stream

			length = s.length();
			offset = in.os;

			// get reduced token

			token.set(s);
			if (!stopped(token))
				break;

		}

		return token;
	}

	// unit test

	public static final void main ( String[] as ) {

		String src = (as.length > 0) ? as[0] : "input";
		int ip = (as.length > 1) ? Integer.parseInt(as[1]) : -1;
		int nn = (as.length > 2) ? Integer.parseInt(as[2]) : 10;
		System.out.println("ip= " + ip + ", nn= " + nn);
		try {
			Stem   mor = new Stem(new DataInputStream(new FileInputStream("sufs")));
			Stop   stp = new Stop(new DataInputStream(new FileInputStream("stps")));
			Stopat pat = new Stopat(new BufferedReader(new FileReader("stpats")));
			Tokenizer tkzr = new Tokenizer(mor,stp,pat);

			BufferedReader in = new BufferedReader(new FileReader(src));
			String line;
			Token t;
			while ((line = in.readLine()) != null) {
				tkzr.set(line);
				System.out.println(">> " + line);

				while ((t = tkzr.get()) != null) {
					System.out.println(t);
				}
			}
			System.out.println("ip= " + ip + ", nn= " + nn);
			mor.dump(ip,ip+nn-1);
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		} catch (AWException e) {
			System.err.println(e);
                        System.exit(2);
		}

	}

}
