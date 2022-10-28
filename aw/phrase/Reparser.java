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
// Reparser.java : 28oct2022 CPM
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
		boolean    sense;   // = false for negated matching
		SyntaxPatt syntax = new SyntaxPatt(); // what to match
	}

	private static final String file = "rules";   // name of rules file

	private static final int Nrules    = 64;      // maximum rule count
	private static final int Nelements = Nrules*4;

	private Rule[] rule = new Rule[Nrules];
	private RuleElement[] element = new RuleElement[Nelements];

	private static final int PHBFLM = 9600;       // buffer size for reparsing

	private int nr,ne; // rule and element counts
	private int nn;    // join rule subcount

	private CombinedSymbolTable stb;

	// initialization to load rules from file

	public Reparser (
		CombinedSymbolTable stb
	) throws AWException {

		this.stb = stb;

		for (int i = 0; i < Nrules; i++)
			rule[i] = new Rule();

		for (int i = 0; i < Nelements; i++)
			element[i] = new RuleElement();

		try {

			BufferedReader in = new BufferedReader(new FileReader(file));
			String b;

			// read in joining rules from file first

			while ((b = in.readLine()) != null) {
				b = b.trim();
				if (b.length() == 0)
					continue;
				char c = b.charAt(0);
				if (c == ';')
					continue;
				if (c == '.') // check for end of joining rules
					break;
				if (processRule(b) == 0)
					throw new AWException("** joining rule error: " + b);
					
			}
			nn = nr;

			// read in splitting rules from same file

			while ((b = in.readLine()) != null) {
				b = b.trim();
				if (b.length() == 0)
					continue;
				char c = b.charAt(0);
				if (c == ';')
					continue;
				if (c == '.') // check for end of joining rules
					break;
				int w = processRule(b);
				if (w <= 0)
					throw new AWException("** splitting rule error: " + b);
			}

			in.close();
//			System.out.println("rules: join= " + nn + ", split= " + (nr-nn));

		} catch (IOException e) {
			System.err.println("cannot read reparsing rules");
		}

	}

	// parse rule string from file

	private static final byte WILD = CombinedSymbolTable.WILD;

	private int processRule (
		String b
	) {

		if (b.length() == 0)
			return 0;

		char ch = b.charAt(0);

		System.out.println("rule= " + b);

		boolean lo = (ch == '|'); // check for left anchor preceding patterns
		if (lo) {
//			System.out.println("left anchor");
			b = b.substring(1).trim();
		}
//		System.out.println("b= " + b);

		int ns = -1; // where to separate left and right of rule

		// split input line into syntactic patterns to match with action point

		int n = 0;
		for (; b.length() > 0; n++) {

			if (b.charAt(0) == '^') {                  // start of right half of pattern?
				if (ns >= 0) return 0;
				b = b.substring(1); ns = n;
			}
//			System.out.println("b= " + b);

			b = b.trim();
			if (b.length() == 0 || b.charAt(0) == '|') // end of rule?
				break;
//			System.out.println("b= " + b);

			RuleElement e = element[ne + n];           // allocate new rule element
			if (b.charAt(0) != '~')
				e.sense = true;
			else {
				e.sense = false; b = b.substring(1);
			}
//			System.out.println("b= " + b);

			int k = b.length();
			int j = 0;
			for (; j < k; j++) {                      // scan to end of element
				char x = b.charAt(j);
				if (Character.isWhitespace(x) || x == '^' || x == '|')
					break;
			}

			try {
				stb.parseSyntax(b.substring(0,j),e.syntax);
			} catch (AWException x) {
				return 0;                         // error
			}

			b = b.substring(j);                       // go to next element
		}

		// each pattern must have a ^ somewhere

		if (ns < 0 || n == 0 || nr == Nrules || n >= Nelements)
			return 0;
		rule[nr].afl   = lo;               // left  anchor
		rule[nr].afr   = (b.length() > 0); // right anchor
		rule[nr].start = (short) ne;       // where rule starts
		rule[nr].left  = (short) ns;       // where its right side starts
		rule[nr].right = (short)(n - ns);  // where side ends
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

	private static final int M =24;    // extra phrases allowed for
	private static final int N =12;    // elements per phrase

	private static final int NM =1000; // minimum phrase element count

	private short[] phx; // start of reparsed phrase elements for phrase
	private short[] elx; // index to individual phrase elements in a reparse
	private static byte[] pb = new byte[PHBFLM]; // reparsed phrase analysis

	private int  pbk;    // end of reparsed phrase buffer

	private int mphx;    // phrase and phrase element limits
	private int melx;    //

	private int nphx;    // phrase and phrase element counts
	private int nelx;    //

	private static final int MM = 40; // how parse bytes to dump for debugging

	private short nskp = 0;  // total skip before phrase elements

	// copy analysis up to next phrase into work area for rewriting

	private int copyPhraseWithMarking (
		byte[] a, // parsing buffer
		int   an, // starting index
		int    n  // scan limit
	) {
//		System.out.println("scan buffer an= " + an + ", n= " + n);
		if (n == 0)
			return 0;

		// look for next phrase element in analysis

		nskp = 0;
		byte mark = 0;   // for last marking in analysis

		int pbks = pbk;  // save to compute marking length

		int as = an;     // start of analysis
		int ai = as;     //
		int al = as + n; // end
		while (ai < al) {
			if (a[ai] == Parsing.Pad)
				ai++;
			if (a[ai] < 0) {
				mark = a[ai++];
				pb[pbk++] = mark;
				nskp += ByteTool.bytesToShort(a,ai);
//				System.out.println("mark= " + mark + ", total skip= " + nskp);
				pb[pbk++] = a[ai++];
				pb[pbk++] = a[ai++];
 				if (mark == Parsing.Phrase)
					break;
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
//		System.out.println("ai= " + ai + ", al= " + al);
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

//		System.out.println("nphx= " + nphx + "/" + phx.length);
		phx[++nphx] = (short) nelx;
//		System.out.println("@" + as + ", element byte count= " + k + ", total bytes= " + (ai-as));
//		dumps(0);
		return ai;  // current position in scanning
	}


	// check rules for joining at sentence or phrase boundary

	private boolean testJoin (
		int lft,  // index of elements left  of phrase boundary
		int mid,  //                   right of
		int rht   //          end of   right
	) {
		int nl = mid - lft;  // left  element count
		int nr = rht - mid;  // right
		if (nl == 0 || nr == 0)
			return false;

		// try every joining rule in order of definition

		int bas,rub; // iteration bases for phrase and rule elements

		int i;
		for (i = 0; i < nn; i++) { // iterate on joining rules
			Rule ru = rule[i];
			if (ru.left > nl || ru.right > nr)
				continue;  // left or right phrase too short

			int j;
			bas = elx[mid - ru.left];
			rub = ru.start;
			for (j = 0; j < ru.left; j++) {
				if (!compareSyntax(element[rub + j],pb,bas + 6*j))
					break;
			}
			if (j <  ru.left)
				continue; // mismatch on left, skip right
			bas = elx[mid];
			rub += ru.left;
			for (j = 0; j < ru.right; j++) {
				if (!compareSyntax(element[rub + j],pb,bas + 6*j))
					break;
			}
			if (j >= ru.right)
				break;    // rule match on right, can quit
		}

//		System.out.println("joining rule i= " + i + " out of " + nn);
		return (i < nn);  // success if any rule matched both left and right
	}

	// find first content phrase element

	private int skipToFirstContent (
		byte[] b,
		int   nb
	) {
		int n = 1;
		for (; (b[nb+n] & Syntax.functionalFeature) != 0; n += 6);
		return n + 2;
	}

	// join current phrase to preceding phrase

	private void doJoin (
		int lft,  // index of elements left  of boundary
		int mid,  // index of boundary between left and right
		int rht   // index of elements right of boundary
	) {
		System.out.println("JOINING @" + mid);
		int nl = mid - lft;  // left  element count
		int nr = pbk - rht;  // right

		short skps = 0;
		
		for (int ip = mid; ip < rht; ip += 3) {
			skps += ByteTool.bytesToShort(pb,ip+1);
		}
		System.arraycopy(pb,rht,pb,mid,pbk-rht);
		int delta = rht - mid;
		pbk -= delta;
		skps += ByteTool.bytesToShort(pb,pbk-3);
		ByteTool.shortToBytes(pb,pbk-3,skps);

		int k = phx[--nphx];
		int t = elx[k-1];

		for (k++; k < nelx; k++)
			elx[k-1] = (short)(elx[k] - delta);
		pb[pbk] = Parsing.Pad; // for sentinel

		phx[nphx] = (short) --nelx;
		System.out.println("joined nphx= " + nphx + ", elx= " + elx);
//		dumps(0);
		nJoin++;
	}

	// ------------------ CHECK FOR SPLITTING OF PHRASE WHEN COPYING BACK

	// try rule patterns at each element in phrase

	// try splitting rules at each element in phrase

	private int testSplit (
		int first,  // index of first element
		int count   // count of elements
	) {
//		System.out.println("split: count= " + count);

		for (int i = 0; i < count; i++) {

			for (int r = nn; r < nr; r++) {
				Rule ru = rule[r];
//				System.out.println(r + ") " + ru.left + " | " + ru.right);
				if (ru.left > i || ru.right > count - i)
					continue;
				int m = ru.left + ru.right;
				if (rule[r].afl && i != 0 || rule[r].afr && i != count - m)
					continue;

				int en = rule[r].start;
//				System.out.println("en= " + en);
				int j = 0;
				int k = i - ru.left;
				for (; j < m; j++) {
					int wb = elx[j + k];
					if (!compareSyntax(element[en + j],pb,wb))
						break;
				}
				if (j == m)       // entire pattern matched
					return i; // where to split
			}
		}
		return -1; // no splitting
	}

	// split phrase at position in parse

	private int doSplit (
		int first,     // index of first element
		int count,     // count of elements
		int where,     // position for split
		int bp,        // output pointer for reparsing
		byte buffer[]  // original phrase parsing
	) {
//		System.out.println("split @" + where);
		int n = where - first;
		if (n == 0) return bp; // nothing to split

		int bs = elx[first];
		int nb = elx[where] - bs;
		System.arraycopy(pb,bs,buffer,bp,nb); // left of split phrase
		bs += nb;
		bp += nb;
		int no = skipToFirstContent(pb,bs);
		buffer[bp++] = Parsing.Phrase;
		buffer[bp++] = (byte) pb[bs+no];
		buffer[bp++] = (byte) pb[bs+no+1];
//		dumps(0);
		nSplt++;
		return bp;
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
	// --- MODIFY AN ANALYSIS WITH SPLITTING AND JOINING OF PHRASES
	//

	private int nJoin; // to report edit counts
	private int nSplt; //

	// edit raw phrase parsing

	public int reparse (
		String  text,  // source text
		Parsing parse  // current parsing
	) {
		if (nr == 0)
			return 0;

		nJoin = nSplt = 0; // initialize counts

		int length = parse.length;

		mphx = parse.count + M;
		melx = mphx*N;
		if (melx < NM)
			melx = NM;
		pbk = 0;

		phx = new short[mphx]; // index into parse buffer
		elx = new short[melx]; // index into element buffer
//		System.out.println("phrase index size= " + phx.length);

		phx[0] = 0;

		int last = 0, where = 0, next = 0; // for checking patterns
		nphx = nelx = 0;                   // no copied phrases or elements yet
		int a = 0, b = 0;
		int k = 0, n = 0;

		// copy first phrase unchanged to element buffer

		int ab = 0;       // scan base for analysis

		ab = copyPhraseWithMarking(parse.buffer,ab,length);
		if (ab == 0)
			return 0; // error
//		dumps(0);
		next = nelx;

		// continue copying to element buffer with possible phrase joining

		while (ab < length) {
//			System.out.println("reparser ab= " + ab + ", length= " + length);
			int limit = length - ab;
			last = where;
			where = next;
			ab = copyPhraseWithMarking(parse.buffer,ab,limit); // align to next phrase mark
//			System.out.println("new scan point= " + abn);
			if (ab <= 0)
				break; // error
			next = nelx;
//			System.out.println("test: nskp= " + nskp + ", last= " + last + ", next= " + next);
			if (nskp < 4 && testJoin(last,where,next))
				doJoin(last,where,next);
		}
//		dumps(0);

		// copy back analysis with possible phrase splitting

		parse.count = 0;
		a = b = 0;
//		System.out.println("back: nphx= " + nphx);
		for (int i = 0; i < nphx; i++) {
			int t = a;
			k = phx[i];  // index of first element in phrase
			a = elx[k];  // its offset in parse of texts
			n = a - t;
			if (n > 0) { // copy over paragraph markings
				System.arraycopy(pb,t,parse.buffer,b,n);
				b += n;
			}

			// split off phrases as long as rules apply

			int count = phx[i + 1] - k; // how many elements
			int ns; // where to split
			while ((ns = testSplit(k,count)) > 0) {
//				System.out.println("split: ns= " + ns);
				b = doSplit(k,count,ns,b,parse.buffer);
				k += ns;
				a = elx[k];
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
//		System.out.println("b= " + b + ", last n= " + n + ", length= " + length);

		if ((b & 1) != 0)
			parse.buffer[b++] = Parsing.Pad;
//		dumps(0);

		parse.length = (short) b;
		PhraseDump.showBytes(parse,System.out);
		System.out.println(nJoin + " joins, " + nSplt + " splits");
		System.out.println("--------------------------------");
		return b;
	}

	//// for debugging
	////

	private static final int C  = 100; // Roman numerals
	private static final int XX =  20; //

	private void dumps ( int n ) { // show current final portion of parsing being copied
		int bi = 0;
		if (n <= 0 || n >= pbk)
			bi = pbk - C;
		else
			bi = pbk - n;
		if (bi < 0) bi = 0;
		System.out.print("-- current reparsing with " + bi + " chars skipped");
		for (int i = 0; bi < pbk; bi++, i++) {
			if (i%XX == 0) System.out.println();
			System.out.print(String.format(" %02x",pb[bi]));
		}
		System.out.println();
		System.out.print  ("-- @pbk= " + pbk);
		System.out.println(String.format(" [%02x %02x %02x]",pb[pbk],pb[pbk+1],pb[pbk+2]));
		System.out.println("nphx= " + nphx + ", phx[nphx]= " + phx[nphx]);
		System.out.print  ("nelx= " + nelx);
		int elxm = (nelx > 0) ? elx[nelx-1] : 0;
		System.out.println(", elx[nelx-1]= " + elxm);
		System.out.println("========================");
	}

	// data for unit testing
	//

	private static final byte Bxfc = (byte) 0xfc; // definitions required for byte hex
	private static final byte Bxfd = (byte) 0xfd;
	private static final byte Bxfe = (byte) 0xfe;
	private static final byte Bx80 = (byte) 0x80;
	private static final byte Bx88 = (byte) 0x88;

	private static final String txn =             // sample text string for parse
		" President Donald Trump stayed on script for more than a week as he crisscrossed" +
		" through Asia - and then Russian President Vladimir Putin showed up." +
		" After chatting with Putin on the sidelines of the Asia Pacific Economic" +
		" Cooperation summit here, Trump abandoned the diplomatic tone the White House" +
		" had carefully scripted for his five-country tour, once again contradicting the" +
		" overwhelming consensus among current and former U.S. officials that the Russian" +
		" leader manipulated the 2016 election." +
		" In a 26-minute question-and-answer session with reporters aboard Air Force One," +
		" the president managed to dismiss probes into whether his campaign colluded with" +
		" Russia as an  artificial Democratic hit job,  said he believed Putin's insistence" +
		" that Russia did not attempt to meddle in the 2016 electon, and warned that the" +
		"  continued focus on Russian election meddling risks lives.";


	private static byte[] pbn = {                 // phrase analysis for sample text
		Bxfe,0x00,0x01,Bxfd,0x00,0x00,Bxfc,0x00,0x00,0x21,0x01,0x00,0x00,0x00,0x09,0x12,0x0d,0x00,0x00,0x01,
		0x06,Bxfc,0x00,0x01,0x12,0x0d,0x00,0x00,0x00,0x05,Bxfc,0x00,0x01,0x12,0x0c,0x00,0x00,0x00,0x06,Bxfc,
		0x00,0x04,0x12,0x00,0x00,0x00,0x00,0x06,Bxfc,0x00,0x11,0x11,0x00,0x00,0x00,0x00,0x04,Bxfc,0x00,0x07,
		0x12,0x0c,0x00,0x00,0x00,0x0c,Bxfc,0x00,0x09,0x11,0x01,0x00,0x00,0x00,0x04,Bxfc,0x00,0x07,0x18,0x02,
		0x00,0x00,0x00,0x04,0x01,0x01,0x00,0x00,0x01,0x07,0x21,0x01,0x00,0x00,0x01,0x09,0x24,0x01,0x00,0x00,
		0x01,0x08,0x12,0x0d,0x00,0x00,0x01,0x05,Bxfc,0x00,0x01,0x12,0x0c,0x00,0x00,0x00,0x06,Bxfd,0x00,0x05,
		Bxfc,0x00,0x06,0x12,0x0c,0x00,0x00,0x00,0x08,Bxfc,0x00,0x06,0x12,0x0d,0x00,0x00,0x00,0x05,Bxfc,0x00,
		0x08,0x11,0x0c,0x00,0x00,0x00,0x09,Bxfc,0x00,0x08,0x11,0x01,0x00,0x00,0x00,0x04,0x04,0x01,0x00,0x00,
		0x01,0x07,0x04,0x01,0x00,0x00,0x01,0x08,0x01,0x01,0x00,0x00,0x01,0x0b,0x00,0x00,0x00,0x00,0x01,0x06,
		Bxfc,0x00,0x07,0x12,0x0d,0x00,0x00,0x00,0x05,Bxfc,0x00,0x01,0x12,0x0c,0x00,0x00,0x00,0x09,Bxfc,0x00,
		0x05,0x04,0x00,0x00,0x00,0x00,0x0a,0x00,0x00,0x00,0x00,0x01,0x04,Bxfc,0x00,0x05,0x12,0x0d,0x00,0x00,
		0x00,0x05,Bxfc,0x00,0x01,0x12,0x0d,0x00,0x00,0x00,0x05,Bxfc,0x00,0x05,0x08,0x00,0x00,0x00,0x00,0x09,
		0x12,0x0c,0x00,0x00,0x01,0x08,Bxfc,0x00,0x05,0x31,0x12,0x00,0x00,0x00,0x03,0x38,0x00,0x00,0x00,0x01,
		0x04,0x01,0x00,0x00,0x00,0x01,0x07,0x00,0x00,0x00,0x00,0x01,0x04,Bxfc,0x00,0x0d,0x12,0x0c,0x00,0x00,
		0x00,0x0d,Bxfc,0x00,0x05,0x12,0x0c,0x00,0x00,0x00,0x0c,Bxfc,0x00,0x01,0x00,0x00,0x00,0x00,0x00,0x09,
		Bxfc,0x00,0x07,0x00,0x00,0x00,0x00,0x00,0x07,Bxfc,0x00,0x05,0x00,0x00,0x00,0x00,0x00,0x06,0x00,0x01,
		0x00,0x00,0x01,0x04,0x04,0x0c,0x00,0x00,0x01,0x09,Bxfc,0x00,0x0a,0x01,0x01,0x00,0x00,0x00,0x07,0x21,
		0x00,0x00,0x00,0x01,0x06,0x12,0x0c,0x00,0x00,0x01,0x0b,Bxfc,0x00,0x05,0x38,0x00,0x00,0x00,0x00,0x04,
		0x01,0x00,0x00,0x00,0x01,0x08,Bxfd,0x00,0x02,Bxfc,0x00,0x05,0x38,0x00,0x00,0x00,0x00,0x02,0x00,0x00,
		0x00,0x00,0x01,0x06,0x01,0x00,0x00,0x00,0x01,0x08,Bxfc,0x00,0x0c,0x01,0x00,0x00,0x00,0x00,0x07,Bxfc,
		0x00,0x06,0x11,0x0c,0x00,0x00,0x00,0x09,Bxfc,0x00,0x08,0x12,0x0d,0x00,0x00,0x00,0x03,Bxfc,0x00,0x01,
		0x12,0x0d,0x00,0x00,0x00,0x05,Bxfc,0x00,0x01,Bxfc,0x00,0x09,0x21,0x00,0x00,0x00,0x00,0x09,0x12,0x0c,
		0x00,0x00,0x01,0x07,Bxfc,0x00,0x04,0x00,0x00,0x00,0x00,0x00,0x07,0x11,0x0c,0x00,0x00,0x01,0x06,Bxfc,
		0x00,0x0e,0x31,0x12,0x00,0x00,0x00,0x03,0x00,0x00,0x00,0x00,0x01,0x08,0x12,0x0c,0x00,0x00,0x01,0x08,
		Bxfc,0x00,0x06,0x11,0x01,0x00,0x00,0x00,0x06,Bxfc,0x00,0x04,0x40,0x02,0x00,0x00,0x00,0x02,0x04,0x00,
		0x00,0x00,0x02,0x0a,0x04,0x01,0x00,0x00,0x01,0x0a,0x12,0x04,0x00,0x00,0x01,0x03,Bxfc,0x00,0x01,0x00,
		0x00,0x00,0x00,0x00,0x03,Bxfc,0x00,0x0b,0x12,0x0c,0x00,0x00,0x00,0x08,Bxfc,0x00,0x01,0x04,0x0d,0x00,
		0x00,0x00,0x07,Bxfc,0x00,0x01,0x01,0x00,0x00,0x00,0x00,0x0a,Bxfc,0x00,0x06,0x11,0x01,0x00,0x00,0x00,
		0x06,Bxfc,0x00,0x09,0x01,0x00,0x00,0x00,0x00,0x07,Bxfc,0x00,0x04,0x00,0x00,0x00,0x00,0x00,0x06,Bxfc,
		0x00,0x08,0x38,0x00,0x00,0x00,0x00,0x04,0x00,0x00,0x00,0x00,0x01,0x07,Bxfc,0x00,0x06,0x12,0x0c,0x00,
		0x00,0x00,0x06,Bxfc,0x00,0x0a,0x12,0x0c,0x00,0x00,0x00,0x09,Bxfc,0x00,0x01,0x00,0x00,0x00,0x00,0x00,
		0x05,Bxfc,0x00,0x04,0x01,0x01,0x00,0x00,0x00,0x07,0x01,0x00,0x00,0x00,0x01,0x08,0x12,0x0c,0x00,0x00,
		0x01,0x08,Bxfc,0x00,0x01,0x00,0x0c,0x00,0x00,0x00,0x05,Bxfc,0x00,0x01,0x11,0x0c,0x00,0x00,0x00,0x05,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0         // extra space for any extension of analysis by reparsing
	};

	private static final int noP =   5;           // phrase count for analysis
	private static final int psL =  57;           // length of analysis in bytes

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
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}

