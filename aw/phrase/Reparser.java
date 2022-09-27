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
// Reparser.java : 08sep2022 CPM
// for joining or splitting of phrases in special cases

package aw.phrase;

import aw.AWException;
import aw.ByteTool;
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

	private static final int Nrules    = 64;      // maximum rule count
	private static final int Nelements = Nrules*4;

	private Rule[] rule = new Rule[Nrules];
	private RuleElement[] element = new RuleElement[Nelements];

	private static final int PHBFLM = 9600;       // buffer size for reparsing

	private int nr,ne; // rule and element counts
	private int nn;    // join rule subcount

	private int mw;    // minimum width for split

	private CombinedSymbolTable stb;

	// initialization to load rules from file

	public Reparser (
		CombinedSymbolTable stb
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
				if (b.length() == 0)
					continue;
				if (b.charAt(0) == '.')
					break;
				processRule(b);
			}
			nn = nr;

			// read in splitting rules from file

			mw = 10000;
			while ((b = in.readLine()) != null) {
				b = b.trim();
				if (b.length() == 0)
					continue;
				if (b.charAt(0) == '.')
					break;
				int w = processRule(b);
				if (w > 0 && mw > w)
					mw = w;
			}

			in.close();
//			System.out.println("rules: join= " + nn + ", split= " + (nr-nn));

		} catch (IOException e) {
			System.err.println("cannot read reparsing rules");
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
		for (;; n++) {

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
			else
				e.sense = false; b = b.substring(1);

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

	// ---------------------------- SYNTAX CHECKING

	private SyntaxSpec ss = new SyntaxSpec();

	private boolean compareSyntax (
		RuleElement e,
		byte[] a,
		int    n
	) {
		ss.type = a[n];
		ss.modifiers = a[n+1];
		ss.semantics = a[n+2];

		if (e.syntax.type != WILD && e.syntax.matchSyntaxType(ss) != e.sense)
			return false;
		else
			return e.syntax.matchSyntaxFeatures(ss);
	}

	// ---------------- COPY AND OPTIONALLY JOIN PHRASES


	private static final int M = 8;    // extra phrases allowed for
	private static final int N =12;    // elements per phrase

	private static final int NM =1000; // minimum phrase element count

	private short[] phx; // end of reparsed phrase elements for text
	private short[] elx; //
	private static byte[] pb = new byte[PHBFLM]; // for reparsed phrase analysis

	private int mphx;    // phrase and phrase element limits
	private int melx;    //

	private int nphx;    // phrase and phrase element counts
	private int nelx;    //
	private int  pbk;    // end of reparsed phrase buffer

	private boolean nwB; // indicate start of new paragraph or sentence

	private static final int MM = 40;

	private short nskp = 0;  // total skip before phrase elements

	private int scanPhrase (
		byte[] a, // parsing buffer
		int   an, // starting index
		int    n  // how many to scan
	) {
//		System.out.println("scan buffer an= " + an + ", n= " + n);
		if (n == 0)
			return 0;

		// look for next phrase element in analysis

		nskp = 0;
		nwB = false;
		int as = an;     // start of analysis
		int ai = as;     //
		int al = as + n; // end
		while (ai < al) {
			if (a[ai] == Parsing.Pad)
				ai++;
			if (a[ai] < 0) {
				byte mark = a[ai++];
				pb[pbk++] = mark;
				nskp += ByteTool.bytesToShort(a,ai);
//				System.out.println("mark= " + mark + ", total skip= " + nskp);
				pb[pbk++] = a[ai++];
				pb[pbk++] = a[ai++];
 				if (mark == Parsing.Phrase)
					break;
				else
					nwB = true;
			}
			else 
				return 0;
		}

		// drop any empty phrase with no elements

		while (ai < al) {
			if (a[ai] == Parsing.Pad)           // ignore padding
				ai++;
			else if (a[ai] == Parsing.Phrase) { // drop any empty phrase
//				System.out.println("drop empty phrase");
				short sko = ByteTool.bytesToShort(a,ai+1);
				nskp += sko;
				sko += ByteTool.bytesToShort(pb,pbk-2);
				ByteTool.shortToBytes(pb,pbk-2,sko);
				ai += 3;
			}
			else if (a[ai] < 0) {               // either paragraph or sentence
				System.err.println("unexpected marks");
				pb[pbk-3] = a[ai++];        // put in place of previous marking
				pb[pbk++] = Parsing.Phrase;
				nskp += ByteTool.bytesToShort(a,ai);
				pb[pbk++] = a[ai++];
				pb[pbk++] = a[ai++];
			}
			else                                // should not happen
				break;
		}
		if (ai >= al)
			return 0;

		// copy all phrase elements

		int ais = ai;
		while (ai < al && a[ai] >= 0) {
//			System.out.println("type= " + a[ai]);
			elx[nelx++] = (short) pbk;
			pb[pbk++] = a[ai++]; // syntax type
			pb[pbk++] = a[ai++]; // syntactic features
			pb[pbk++] = a[ai++]; // semantic
			pb[pbk++] = a[ai++]; // a short skip before atom
			pb[pbk++] = a[ai++]; //   (two bytes)
			pb[pbk++] = a[ai++]; // atom length
		}
		int k = ai - ais;
//		System.out.println("copy " + k + "bytes of parse"); 
//		if (k > MM) {
//			for (int j = 0; j < MM; j++) {
//				if (j%20 == 0)
//					System.out.println();
//				System.out.print(String.format(" %2x",a[ais+j]));
//			}
//			System.out.println();
//		}
		pb[pbk] = Parsing.Pad; // for sentinel
//		System.out.println("** sentinel n= " + pb[pbk-1]);

		// count up phrase

		phx[++nphx] = (short) nelx;
//		System.out.println("@" + as + ", element byte count= " + k + ", total bytes= " + (ai-as));
		return ai - as;  // how many total bytes copied
	}

	// check rules for join at location

	private boolean testJoin (
		int where,
		int lft,
		int rht
	) {
		if (where == 0 || lft == 0 || rht == 0)
			return false;

		int wb;

		// try all joining rules in order of definition

		int i = 0;
		for (; i < nn; i++) {

			int ml = rule[i].left;
			int mr = rule[i].right;
			if (ml > lft || mr > rht)
				continue;
			if (rule[i].afl && ml != lft ||
				rule[i].afr && mr != rht)
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
		for (; (b[nb+n] & Syntax.functionalFeature) != 0; n += 6);
		return n + 2;
	}

	// sum up offsets to first phrase element

	private int collectOffsets (
		byte[] b,
		int   nb
	) {
		int n = 0;
		while (b[nb++] < 0) {
			n += ByteTool.bytesToShort(b,nb);
			nb += 2;
		}
//		System.out.println("collect n= " + n);
		return n;
	}

	// join current phrase to preceding phrase

	private void doJoin (

	) {
		dumps(0);
		System.out.println("JOINING @" + nphx);
		int k = phx[--nphx];
		int t = elx[k-1];
		int a = t + 6;
		int b = elx[k];
//		System.out.println("a= " + a + ", from b= " + b);

		int ib = b + firstOffset(pb,b);
		short os = ByteTool.bytesToShort(pb,ib);
		os += collectOffsets(pb,a);
		ByteTool.shortToBytes(pb,ib,os);

		int n = pbk - b;
//		System.out.println("0 pbk= " + pbk);
		System.arraycopy(pb,b,pb,a,n);
		int m = b - a;
		for (k++; k < nelx; k++)
			elx[k-1] = (short)(elx[k] - m);
		pbk -= m;
//		System.out.println("1 pbk= " + pbk);
		pb[pbk] = Parsing.Pad; // for sentinel

		phx[nphx] = (short) --nelx;
	}

	// ------------------ CHECK FOR SPLITTING OF PHRASE 

	// try rule patterns at each element in phrase

	private int testSplit (
		int where,
		int n
	) {
//		System.out.println("split? where= " + where + ", n= " + n);
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
		int   nb,
		int   nl
	) {
		int bs = nb;
		while (nb < nl && b[nb] >= 0)
			nb += 6;
		return nb - bs;
	}

	//
	// --- MODIFY AN ANALYSIS WITH SPLITTING AND JOINING
	//

	public int reparse (
		String  text,
		Parsing parse
	) {
		if (nr == 0)
			return 0;

		int length = parse.length;

		mphx = parse.count + M;
		melx = mphx*N;
		if (melx < NM)
			melx = NM;

		phx = new short[mphx]; // index into parse buffer
		elx = new short[melx]; // index into element buffer

		phx[0] = 0;

		// copy analysis to element buffer with possible phrase joining

		int where = 0, last = 0;
		nphx = nelx = pbk = 0;
		int a = 0, b = 0;
		int k = 0, n = 0;
		for (; length > 0; b += k, length -= k) {
//			System.out.println("reparser k= " + k + ", length= " + length);
			n = nelx;
			k = scanPhrase(parse.buffer,b,length); // align to next phrase mark
//			System.out.println("scanned elements= " + k);
			if (k == 0)
				break;
			if (nwB)
				last = 0;
			int next = nelx - n;
//			System.out.println("test: last= " + last + ", next= " + next);
			if (nskp < 3 && testJoin(where,last,next))
				doJoin();
			last = next;
			where = phx[nphx];
		}
//		dumps(0);

		// copy back analysis with possible phrase splitting

		parse.count = 0;
		a = b = 0;
//		System.out.println("back: nphx= " + nphx);
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
				System.out.println("split: ns= " + ns);
				n = elx[k + ns] - elx[k];
				System.arraycopy(pb,a,parse.buffer,b,n);
				a += n;
				b += n;
				int no = firstOffset(pb,a);
				parse.buffer[b++] = Parsing.Phrase;
				parse.buffer[b++] = (byte) pb[a+no];
				parse.buffer[b++] = (byte) pb[a+no+1];
				pb[a+no]   = 0;
				pb[a+no+1] = 0;
				k += ns;
				count -= ns;
				parse.count++;
			}

			// copy back rest of phrase

			n = endPhrase(pb,a,parse.length); // should be > 0 for split or no split
			if (n%6 != 0) {
				System.err.println("incomplete phrase element, n= " + n);
				return -1;
			}
//			System.out.println("copy to buffer, b= " + b + ", n= " + n);
			System.arraycopy(pb,a,parse.buffer,b,n);
			a += n;
			b += n;
			parse.count++;
		}
//		dumps(0);
//		System.out.println("b= " + b + ", last n= " + n + ", length= " + length);

		if ((b & 1) != 0)
			parse.buffer[b++] = Parsing.Pad;
//		dumps(0);

		parse.length = (short) b;
//		PhraseDump.showBytes(parse,System.out);
		System.out.println("--------------------------------");
		return b;
	}

	//// for debugging
	////

	private static final int C  = 100;
	private static final int XX =  20;

	private void dumps ( int n ) { // show final portion of scanned parsing
		int bi = 0;
		System.out.println("................");
		if (n <= 0 || n >= pbk)
			bi = pbk - C;
		else
			bi = pbk - n;
		if (bi < 0) bi = 0;
		for (int i = 0; bi < pbk; bi++, i++) {
			if (i%XX == 0) System.out.println();
			System.out.print(String.format(" %02x",pb[bi]));
		}
		System.out.println();
		System.out.println("---- " + pbk);
		System.out.println(String.format(" %02x %02x %02x",pb[pbk],pb[pbk+1],pb[pbk+2]));
		System.out.println("---- ");
		System.out.println("nphx= " + nphx + ", phx[nphx]= " + phx[nphx]);
		System.out.print  ("nelx= " + nelx);
		int elxm = (nelx > 0) ? elx[nelx-1] : 0;
		System.out.println(", elx[nelx-1]= " + elxm);
		System.out.println("================");
	}

	private static final String txn =             // sample text string for parse
		"benefit high earners and corporations. still, middle-class earners would fare better";


	private static final byte Bxfc = (byte) 0xfc; // definitions required for byte hex
	private static final byte Bxfd = (byte) 0xfd;
	private static final byte Bxfe = (byte) 0xfe;
	private static final byte Bx80 = (byte) 0x80;
	private static final byte Bx88 = (byte) 0x88;

	private static int noP = 5, psL = 85;

	private static byte[] pbn = { // sample parsing for test (cannot be final!)
		Bxfe,0x00,0x00,Bxfd,0x00,0x00,Bxfc,0x00,0x00,0x18,0x00,0x00,0x00,0x00,0x07,0x00,0x00,0x00,0x00,0x01,
		0x04,0x18,0x0c,0x00,0x00,0x01,0x07,Bxfc,0x00,0x05,0x17,0x0c,0x00,0x00,0x00,0x0c,Bxfd,0x00,0x02,Bxfc,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x05,Bxfc,0x00,0x02,0x00,0x00,0x00,0x00,0x00,0x06,0x00,0x00,0x00,
		0x00,0x01,0x05,0x18,0x0c,0x00,0x00,0x01,0x07,Bxfc,0x00,0x07,0x00,0x00,0x00,0x00,0x00,0x04,0x12,0x00,
		0x00,0x00,0x01,0x06,Bx80,
        	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0   // analysis expansion
	};

	public static void main (String[] a) {
		try {
			PhraseSyntax.loadDefinitions();
			CombinedSymbolTable cst = PhraseSyntax.getSymbolTable();
			Reparser rp = new Reparser(cst);
			Parsing  ps = new Parsing(noP,psL,pbn);
			System.out.println("sample text length= " + txn.length() + " chars");
			PhraseDump.showBytes(ps,System.out);
			PhraseDump.show(txn,ps,cst,System.out);
			System.out.println("--------");
			int n = rp.reparse(txn,ps);
			if (n < 0) System.err.println("reparsing fails");
			System.out.println(ps);
			PhraseDump.showBytes(ps,System.out);
			PhraseDump.show(txn,ps,cst,System.out);
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
	}

}

