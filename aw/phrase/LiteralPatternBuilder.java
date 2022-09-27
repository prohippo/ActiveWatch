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
// LiteralPatternBuilder.java : 21jun2022 CPM
// compile from input stream

package aw.phrase;

import aw.AWException;
import aw.Letter;
import aw.Format;
import java.io.*;

public class LiteralPatternBuilder extends LiteralPattern {

	public  static final String input = "patterns";

	private static final int PAL = LiteralPattern.LIMIT*16; // nominal pattern array allocation

	public LiteralPatternBuilder (
		InputStream  is
	) throws IOException {

		super();
		pattern = new char[PAL];

		// initialize

		CombinedSymbolTable stb = new CombinedSymbolTable();
		Syntax.initialize(stb);

		// read literals and syntax

		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		SyntaxPatt pat = new SyntaxPatt();
		SyntaxSpec syn;
		String r;
		int kprv  = 0;
		int alloc = 0;
		int count = 0;

		while ((r = in.readLine()) != null) {

			// skip commentary

			r = r.trim();
			if (r.length() < 4 || r.charAt(0) == ';')
				continue;

			System.out.println("> " + r);

			// look for literal in line

			int n;
			if (r.charAt(0) != '"' || (n = r.substring(1).indexOf('"')) < 0)
				throw new IOException("bad literal entry: " + r);

			String literal = r.substring(1,n+1);
			if (literal.length() < 2)
				continue;
			if (alloc + literal.length() + 1 > PAL) {
				System.err.println("** table overflow");
				break;
			}

			r = r.substring(n+2).trim();

		 	// any more room in table?

			if (count == LiteralPattern.LIMIT) {
				System.err.println("** too many literal entries");
				break;
			}

			// find and process the syntax specification

			try {
				syn = new SyntaxSpec();
				if (!r.equals("--")) {
					System.out.println("r= " + r);
					stb.symbolToSyntax(r,pat);
					Syntax.patternToSpecification(pat,syn);
				}
				else {
					syn.type = Syntax.unknownType;
					syn.modifiers = 0;
					syn.semantics = 0;
				}
			} catch (AWException e) {
				System.err.println(e);
				throw new IOException("conversion error");
			}

			// show entry and syntax

			System.out.print("[" + literal + "] ");
			System.out.print(Format.hex(syn.type) + ": ");
			System.out.print(Format.hex(syn.modifiers) + " ");
			System.out.println(Format.hex(syn.semantics));

			// check ordering of entry

			int  k;
			char ch = literal.charAt(0);
			if ((k = Letter.toByte(ch)) < 0) {
				if (INITs.indexOf(ch) >= 0)
					k = Letter.NAN + 1;
				else if (ch == '\\' && Character.isLetterOrDigit(literal.charAt(1)))
					k = Letter.toByte(literal.charAt(1));
				else
					k = Letter.NAN;
			}
			if (kprv > k + 1)
				throw new IOException("patterns must be listed alphabetically");

			//  fill out index from last literal entry

			while (kprv <= k)
				index[kprv++] = (short) count;

			// set the offset and syntax for this literal

			offset[count] = (short) alloc;
			syntax[count] = syn;

			int ln = literal.length();
			literal.getChars(0,ln,pattern,alloc);
			alloc += ln;
			pattern[alloc++] = 0;
			count++;

		}

		// fill out offsets

		offset[count] = (short) alloc;

		// fill up end of index table

		while (kprv <= Letter.NAN + 2)
			index[kprv++] = (short) count;

		System.out.println(count + " literal patterns defined");

	}

	public static void main ( String[] a ) {
		try {
			String fn = (a.length > 0) ? a[0] : LiteralPatternBuilder.input;

			System.out.println("build literal patterns from file= " + fn);

			FileInputStream is = new FileInputStream(fn);
			LiteralPatternBuilder lpb = new LiteralPatternBuilder(is);
			is.close();

			System.out.println("saving");

			FileOutputStream os = new FileOutputStream(LiteralPattern.file);
			lpb.save(os);
			os.close();

			System.out.println("DONE");
		} catch (IOException e) {
			System.err.println("error: " + e);
		}
	}
}
