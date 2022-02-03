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
// Reparser.java : 26jan2022 CPM
// for joining or splitting of phrases

package aw.phrase;

import aw.AWException;
import java.io.*;

public class Reparser {

	class Rule {
		boolean afl,afr;    // match anchor flags
		short   start;      // index into patterns
		short   left,right; // pattern element counts
	}

	class RuleElement {
		boolean    sense;   // = false to negate
		SyntaxPatt syntax = new SyntaxPatt(); // what to match
	}

	private static final String file = "rules";
	
	private static final int PHBFLM = 8192; // buffer size

	private static final int Nrules = 64;
	private static final int Nelements = Nrules*4;

	private Rule[] rule = new Rule[Nrules];
	private RuleElement[] element = new RuleElement[Nelements];

	private int nr,ne; // rule and element counts
	private int nn;    // join rule subcount

	private int mw;    // minimum width for split
	
	private SymbolTable stb;

	// initialization for file
	
	public Reparser (
		SymbolTable stb
	) {
	
		this.stb = stb;
		
		for (int i = 0; i < Nrules; i++)
			rule[i] = new Rule();
			
		for (int i = 0; i < Nelements; i++)
			element[i] = new RuleElement();
		
		try {
		
			BufferedReader in = new BufferedReader(new FileReader(file));
			String b;
			
			// read in joining rules from file

			while ((b = in.readLine()) != null) {
				b = b.trim();
				if (b.charAt(0) == '.')
					break;
				processRule(b);
			}
			nn = nr;

			// read in splitting rules from file

			mw = 10000;
			while ((b = in.readLine()) != null) {
				int w = processRule(b.trim());
				if (w > 0)
					if (mw > w)
						mw = w;
			}
			
			in.close();
		
		} catch (IOException e) {
		}

	}
	
	// parse rule string from file

	private static final byte WILD = (byte) 0xFF;

	private int processRule (
		String b
	) {
	
		if (b.length() == 0)
			return 0;
			
		char ch = b.charAt(0);

		if (ch == ';')
			return 0;

		boolean lo = (ch == '|');
		if (lo)
			 b = b.substring(1);

		int ns = -1; // where to separate left and right of rule

		// split input line into syntactic patterns to match

		int n = 0;
		for (; ; n++) {

			b = b.trim();
			if (b.length() == 0)
				break;
				
			if (b.charAt(0) == '^') {
				b = b.substring(1); ns = n;
			}

			b = b.trim();
			if (b.length() == 0 || b.charAt(0) == '|')
				break;
			
			RuleElement e = element[ne + n];
			if (b.charAt(0) != '~')
				e.sense = true;
			else {
				e.sense = false; b = b.substring(1);
			}

			int k = b.length();
			int j = 0;
			for (; j < k; j++) {
				char x = b.charAt(j);
				if (Character.isWhitespace(x) || x == '^' || x == '|')
					break;
			}

			try {
				stb.symbolToSyntax(b.substring(0,j),e.syntax);
			} catch (AWException x) {
			}
			if (b.charAt(0) == '?')
				e.syntax.type = WILD;

			b = b.substring(j);
		}

		// each pattern must have a ^ somewhere

		if (ns < 0 || n == 0 || nr == Nrules || n >= Nelements)
			return 0;
		rule[nr].afl   = lo;
		rule[nr].afr   = (b.length() > 0);
		rule[nr].start = (short) ne;
		rule[nr].left  = (short) ns;
		rule[nr].right = (short)(n - ns);
		nr++;

		ne += n;
		return n;
	}

	// ---------------------------- SPECIAL SYNTAX CHECK

	private SyntaxSpec ss = new SyntaxSpec();
	
	private boolean compareSyntax (
		RuleElement e,
		byte[] a,
		int    n
	) {
		ss.type = a[n];
		ss.modifiers = a[n+1];
		ss.semantics = ((a[n+1] & Syntax.functionalFeature) != 0) ? 0 : a[n+2];

		if (e.syntax.type != WILD && e.syntax.matchSyntaxType(ss) != e.sense)
			return false;
		else
			return e.syntax.matchSyntaxFeatures(ss);
	}

	// ---------------- COPY AND OPTIONALLY JOIN PHRASES


	private static final int M = 8;    // extra phrases allowed for
	private static final int N =12;    // elements per phrase

	private static final int NM =1000; // minimum phrase element count

	private short[] phx; // temporary buffering of text analysis
	private short[] elx;
	private static byte[] pb = new byte[PHBFLM];

	private int mphx;    // phrase and phrase element limits
	private int melx;

	private int nphx;    // phrase and phrase element counts
	private int nelx;
	private int  pbk;

	private boolean nwf; // indicate start of new paragraph

	private int scanPhrase (
		byte[] a,
		int   an,
		int    n
	) {
		if (n == 0)
			return 0;

		// look for next phrase element in analysis

		nwf = false;
		int as = an;     // start of analysis
		int al = as + n; // end
		int ai = as;
		while (ai < al && a[ai] != Parsing.Phrase) {
			if (a[ai++] == Parsing.Paragraph)
				nwf = true;
			ai++;
		}
		if (ai >= al)
			return 0;

		ai++; ai++; // skip over phrase marker and offset

		// copy analysis up to first element

		int k = ai - as;
		System.arraycopy(a,as,pb,pbk,k);
		pbk += k;

		// copy all phrase elements

		while (ai < al && a[ai] >= 0) {
			elx[nelx++] = (short) pbk;
			pb[pbk++] = a[ai++];
			pb[pbk++] = a[ai];
			if ((a[ai++] & Syntax.functionalFeature) == 0) {
				pb[pbk++] = a[ai++];
				pb[pbk++] = a[ai++];
				pb[pbk++] = a[ai++];
			}
		}
		pb[pbk] = Parsing.Pad; // for sentinel

		// count up phrase

		phx[++nphx] = (short) nelx;
		return ai - as;
	}
	
	// check rules for join at location

	private boolean testJoin (
		int where,
		int left,
		int right
	) {
		if (where == 0 || left == 0 || right == 0)
			return false;
			
		int wb;

		// try all joining rules in order of definition

		int i = 0;
		for (; i < nn; i++) {

			int ml = rule[i].left;
			int mr = rule[i].right;
			if (ml > left || mr > right)
				continue;
			if (rule[i].afl && ml != left ||
				rule[i].afr && mr != right)
				continue;

			int en = rule[i].start + rule[i].left;

			// match left in preceding phrase

			int j = 0;
			for (; j < ml; j++) {
				wb = elx[where - j - 1];
				if (!compareSyntax(element[en - j - 1],pb,wb))
					break;
			}
			if (j < ml)
				continue;

			// match right in current phrase

			for (j = 0; j < mr; j++) {
				wb = elx[where + j];
				if (!compareSyntax(element[en + j],pb,wb))
					break;
			}
			if (j == mr)
				break;
		}
		return (i < nn);
	}

	// find first content phrase element

	private int firstOffset (
		byte[] b,
		int   nb
	) {
		int n = 1;
		for (; (b[nb+n] & Syntax.functionalFeature) != 0; n += 2);
		return n + 2;
	}

	// sum up offsets to first phrase element

	private int collectOffsets (
		byte[] b,
		int   nb
	) {
		int n = 0;
		while (b[nb++] < 0)
			n += b[nb++];
		return n;
	}

	// join current phrase to preceding phrase

	private void doJoin (

	) {
		int k = phx[--nphx];
		int t = elx[k-1];
		int a = t + (((pb[t+1] & Syntax.functionalFeature) != 0) ? 2 : 5);
		int b = elx[k];

		pb[b + firstOffset(pb,b)] += collectOffsets(pb,a);

		int n = pbk - b;
		System.arraycopy(pb,b,pb,a,n);
		int m = b - a;
		for (k++; k < nelx; k++)
			elx[k-1] = (short)(elx[k] - m);
		pbk -= m;
		pb[pbk] = Parsing.Pad; // for sentinel

		phx[nphx] = (short) --nelx;
	}

	// ------------------ CHECK FOR SPLITTING IN PHRASE 

	// try rule patterns at each element in phrase

	private int testSplit (
		int where,
		int n
	) {
		for (int i = 0; i < n; i++) {
			if (mw > n - i)
				break;

			for (int r = nn; r < nr; r++) {
				int m = rule[r].left + rule[r].right;
				if (m > n - i)
					continue;

				if (rule[r].afl && i != 0 ||
					rule[r].afr && i != n - m)
					continue;

				int en = rule[r].start;
				int j = 0;
				for (; j < m; j++) {
					int wb = elx[where + i + j];
					if (!compareSyntax(element[en + j],pb,wb))
						break;
				}
				if (j == m)
					return i + rule[r].left;
			}
		}
		return 0;
	}
	
	// find the end of a phrase in an analysis

	private int endPhrase (
		byte[] b,
		int   nb
	) {
		int bs = nb;
		while (b[nb] >= 0)
			nb += ((b[nb+1] & Syntax.functionalFeature) != 0) ? 2 : 5;
		return nb - bs;
	}

	// --- MODIFY AN ANALYSIS WITH SPLITTING AND JOINING

	public void reparse (
		String  text,
		Parsing parse
	) {
		if (nr == 0)
			return;

		int length = parse.length;

		mphx = parse.count + M;
		melx = mphx*N;
		if (melx < NM)
			melx = NM;

		phx = new short[mphx];
		elx = new short[melx];

		phx[0] = 0;

		// copy analysis to buffer with possible phrase joining

		int where = 0, last = 0;
		nphx = nelx = pbk = 0;
		int a = 0, b = 0;
		int k = 0, n = 0;
		for (; length > 0; b += k, length -= k) {
			n = nelx;
			k = scanPhrase(parse.buffer,b,length);
			if (k == 0)
				break;
			if (nwf)
				last = 0;
			int next = nelx - n;
			if (!testJoin(where,last,next))
				last = next;
			else {
				doJoin();
				last += next;
			}
			where = phx[nphx];
		}

		// copy back analysis with possible phrase splitting

		parse.count = 0;
		a = b = 0;
		for (int i = 0; i < nphx; i++) {
			int t = a;
			k = phx[i];
			a = elx[k];
			n = a - t;
			if (n > 0) {
				System.arraycopy(pb,t,parse.buffer,b,n);
				b += n;
			}

			// split off phrases as long as rules apply

			int count = phx[i + 1] - k;
			int ns;
			while ((ns = testSplit(k,count)) != 0) {
			
				n = elx[k + ns] - elx[k];
				System.arraycopy(pb,a,parse.buffer,b,n);
				a += n;
				b += n;
				int no = firstOffset(pb,a);
				parse.buffer[b++] = Parsing.Phrase;
				parse.buffer[b++] = (byte) pb[a+no];
				pb[a+no] = 0;
				k += ns;
				count -= ns;
				parse.count++;
		
			}

			// copy back rest of phrase

			n = endPhrase(pb,a);
			System.arraycopy(pb,a,parse.buffer,b,n);
			a += n;
			b += n;
			parse.count++;
		}

		parse.buffer[b] = Parsing.Pad;
		if ((b & 1) != 0)
			b++;

		parse.length = (short) b;
		return;
	}

}
