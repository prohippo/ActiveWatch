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
// Match.java : 03Jul2021 CPM
// string pattern matching class

// a disjunctive pattern is stored internally as a list
// of alternate subpatterns to match against, with each
// consisting of a list of match components and a count

package match;

import aw.*;
import aw.CharArray;

public class Match {
	private static final int nn=8; // maximum disjunction
	private Pattern[]        pp;   // pattern list

	// simple initialization

	public Match (
		String s  // pattern string
	) throws AWException {
		this(s,nn);
	}
	
	// set up match patterns

	public Match (
		String s, // pattern string
		int    m  // maximum disjunction
	) throws AWException {
		int k,n;
		String sp;
		Pattern[] p = new Pattern[m];

		// parse disjunctive clauses separated by |
		
		for (n = 0; (k = s.indexOf('|')) >= 0; n++) {
			if (n == m)
				break;
			p[n] = new Pattern(s.substring(0,k));
			s = s.substring(k+1);
		}
		if (n == m)
			throw new AWException("match overflow");
		p[n++] = new Pattern(s);

		// save Pattern array of length n
		
		pp = new Pattern[n];		
		for (int i = 0; i < n; i++)
			pp[i] = p[i];
		return;
	}

	// match string in CharArray against a pattern
	// return offset >= 0 on match, otherwise < 0
	
	public final int matchUp (
		CharArray s  // string to match
	) {
		for (int i = 0, m = 0; i < pp.length; i++)
			if ((m = pp[i].match(s)) >= 0)
				return m;
		return -1;
	}
}
