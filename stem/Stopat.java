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
// Stopat.java : 27May00 CPM
// extended stop pattern matching

package stem;

import aw.*;
import java.io.*;

class PatternQualify {

	boolean lAnchor; // must be at start of line if true
	boolean rAnchor; // must be at end   of line if true
	short  cur;      // state for applicability of match
	short  nxt;      // next state after a match
	char lContext;   // left  context for match
	char rContext;   // right context for match
	
}

public class Stopat {

	private static final int MXPT = 72; // how many patterns
	
	private TokenPattern  [] pa = new TokenPattern  [MXPT]; // stop patterns
	private PatternQualify[] qu = new PatternQualify[MXPT]; // pattern qualifiers
	private int npq = 0;

	// constructor
	
	public Stopat (
	
		BufferedReader in // definitions
		
	) throws AWException {
	
		if (in == null)
			return;
			
		// load stop patterns from text input

		try {
		
			for (String line; (line = in.readLine()) != null; ) {
			
				line = line.trim();
				if (line.length() == 0)
					continue;
			
				// take care of any qualifier first
				
				PatternQualify q = new PatternQualify();
				line = parse(line,q);
				if (npq == qu.length)
					throw new AWException("pattern overflow");
					
				qu[npq] = q;
				
				// encode actual pattern
				
				pa[npq++] = new TokenPattern(line);
				
			}
			
		} catch (IOException e) {
			throw new AWException("cannot get stop patterns");
		}
		
	}
	
	// extract qualifiers from pattern
	
	private String parse (
	
		String s,        // pattern definition
		PatternQualify q // record to be filled
		
	) {
	
		// get state definitions
		
		int k = s.indexOf(' ');
		if (k < 0)
			q.cur = q.nxt = -1;
		else {
			String t = s.substring(k).trim();
			s = s.substring(0,k);
			char c0 = (t.length() < 1) ? '0' : t.charAt(0);
			char c1 = (t.length() < 2) ? '0' : t.charAt(1);
			
			if (!Character.isDigit(c0))
				q.cur = q.nxt = 0;
			else {
				q.cur = (short)(c0 - '0');
				q.nxt = (!Character.isDigit(c1)) ? q.cur : (short)(c1 - '0');
			}
		}
		
		// get left and right contexts
		
		if (s.length() > 1) {
			if (s.charAt(0) == ']') {
				q.lAnchor = true;
				s = s.substring(1);
			}
			else if (s.charAt(1) == ']') {
				q.lContext = s.charAt(0);
				s = s.substring(2);
			}
			k = s.indexOf('[');
			if (k >= 0) {
				if (k + 1 < s.length())
					q.rContext = s.charAt(k+1);
				else
					q.rAnchor = true;
				s = s.substring(0,k);
			}
		}
		
		return s;
	}

	// compare token sequentially against
	// currently defined stop-patterns

	private static int state = 0; // for finite-state automaton (FSA)
	
	public final boolean stopat (
	
		Token token, // to match against
		char  left,  // context char
		char  right  // context char
		
	) {
		for (int p = 0; p < npq; p++) {
			
			// check for match
			
			if (pa[p].match(token)) {
			
				// now check pattern qualifiers
				
				PatternQualify q = qu[p];
				
				if ((q.cur < 0 || q.cur == state) &&
					(!q.lAnchor || left  == '\n' || left  == '\r') &&
					(!q.rAnchor || right == '\n' || right == '\r') &&
					(q.lContext == 0 || q.lContext == left) &&
					(q.rContext == 0 || q.rContext == right)) {
					
					if (q.nxt >= 0)
						state = q.nxt;
					return true;
					
				}
				
			}
			
		}
		return false;
	}
	
	// reinitialize FSA for qualifiers
	
	public final void reset ( ) {
	
		state = 0;
		
	}

}
