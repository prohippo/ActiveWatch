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
// TokenSubstitution.java : 10jan2023 CPM
// pre-analysis substitution from a table of patterns

package stem;

import aw.Letter;
import java.io.*;

class SubstitutionEntry {

	private static final String ditto = "\"";
	private static       Token  last  = new Token();

	private TokenPattern cx; // pattern to match
	private TokenPattern fm; // pattern to match
	private Token  to;       // its substitution

	// create table entry

	public SubstitutionEntry (
		String fms,
		String tos
	) {
		if (tos == null)
			tos = "";
		int k = fms.indexOf(' ');
		if (k >= 0) {
			cx = new TokenPattern(fms.substring(k+1).trim());
			fms = fms.substring(0,k);
		}
		fm = new TokenPattern(fms);
		to = last = (tos.equals(ditto)) ? last : new Token(tos);
	}

	public final Token token (
	) {
		return to;
	}

	public final boolean matchable (
		int n
	) {
		return fm.matchable(n);
	}

	public final boolean comparePattern (
		Token tk
	) {
		return fm.match(tk);
	}

	public final boolean compareContext (
		Token tk
	) {
		return (cx == null) ? true : cx.match(tk);
	}

}

public class TokenSubstitution {

	private static final int M = 64;

	private static SubstitutionEntry[] table = new SubstitutionEntry[M];
	private static int count;

	// load table from file

	public TokenSubstitution (
		String file
	) {
		count = 0;
		try {

			Token fm;
			String entry;
			BufferedReader in = new BufferedReader(new FileReader(file));
			while ((entry = in.readLine()) != null) {
				int k = entry.indexOf('=');
				if (k < 0)
					continue;
				String sub = entry.substring(k+1).trim();
				String pat = entry.substring(0,k).trim();
				if (count == M) {
					System.err.println("substitution overflow");
					break;
				}
				table[count++] = new SubstitutionEntry(pat,sub);
			}
			in.close();

		} catch (IOException e) {
			System.err.println(e);
			count = 0;
		}
	}

	// make substitution

	public final void substitute (
		Token t
	) {
//		System.out.println("substitute " + t);
		int n = t.length();
		for (int i = 0; i < count; i++) {
			SubstitutionEntry se = table[i];
			if (se.matchable(n))
				if (se.comparePattern(t) && se.compareContext(t)) {
//					System.out.println("done: " + t);
					t.set(se.token());
				}
		}
	}

}
