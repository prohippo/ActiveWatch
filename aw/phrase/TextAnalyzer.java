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
// TextAnalyzer.java : 25oct2022 CPM
// process an entire text item by paragraphs, sentences, and phrases

package aw.phrase;

import aw.AWException;
import aw.ByteTool;
import aw.Format;
import aw.ResourceInput;
import aw.phrase.Syntax;
import test.TestText;
import java.io.*;

public class TextAnalyzer {

	private LiteralType     lty;    // logic for recognizing LITERAL syntax types
	private TextParagrapher tpg;    // for extracting next paragraph

	private static boolean defined = false;  // flag for language tables being loaded

	private CharArrayWithTypes cha; // writeable copy of text

	public TextAnalyzer (

		String     text,  // input text to analyze
		int[]    lining,  // line index for text
		int      nlines,  // line count
		LiteralType lty   // for literal recognition

	) throws IOException {
		if (!defined) {
			System.out.println("define symbols and words");
			CombinedSymbolTable stb = PhraseSyntax.getSymbolTable();
//			stb.dump();
			WordType.load(stb);
			defined = true;
		}
//		System.out.println("text= [" + text + "]");
//		System.out.println(nlines + " lines");
		int n = text.length();   // total text length
		int k = lining[nlines];  // how many chars lined out
		if (n > k)
			n = k;
		cha = new CharArrayWithTypes(text);
//		System.out.println("text=" + cha);
		tpg = new TextParagrapher(cha,lining,nlines);
		this.lty = lty;
	}

	private int    Pp;  // parse pointer
	private int    Pps; // end of last content atom
	private int    Ps;  // start of atoms for phrase
	private int    Sp;  // last sentence start
	private byte[] Pb;  // buffer for analysis is external

	private int addedSkip  = 0;
	private int itemNumber = 0;
	private SyntaxSpec previousSyntax = new SyntaxSpec();

	private int phraseCount;

	// store skip in two bytes (maximum of 32,767)

	private void storeSkip (

		short offset

	) {
//		System.out.println(" ** store= " + offset);
		ByteTool.shortToBytes(Pb,Pp,offset);
		Pp += 2;
	}

	// reset for analyses

	public final void restart ( ) { addedSkip = 0; }

	// get number of phrases extracted

	public final int countPhrases ( ) { return phraseCount; }

	// get phrases from next paragraph

	public int analyze (

		byte[] parse,
		int    start,
		int    wordlimit

	) {
		Pb = parse;
		Pp = start;
		phraseCount = 0;

		// end of text check

		CharArrayWithTypes tx = tpg.next();
//		System.out.println("tx= " + tx);
		if (tx == null || tx.length() == 0)
			return 0;

		short o = (short) tpg.offset();

//		System.out.println("offset= " + o);
//		System.out.println("tx= " + tx);
		LexicalAtomStream las = new LexicalAtomStream(tx,lty);

		short n = (short) las.find();

//		System.out.println("n= " + n + ", Pp= " + Pp);
		Pb[Pp++] = Parsing.Paragraph;
		storeSkip((short)(n + o));
		int length = 3;
		if (!las.end()) {
			Sp = Pp;
			Pb[Pp++] = Parsing.Sentence;
			storeSkip((short) 0);
//			System.out.println("Sent Pp= " + Pp);
			length += 3;
		}

		// process each phrase and store its analysis

		while (!las.end()) {
//			System.out.println("(  ) Pp= " + Pp);
			int Po = Pp;
			extractPhrase(las,wordlimit);
			int np = Pp- Po;

			if (np > 0) {
				length += np;
				phraseCount++;
			}
		}
//		dumpb();
		return length;
	}

	// extract next phrase from lexical atom stream

	private void extractPhrase (

		LexicalAtomStream las,
		int wordlimit

	) {
		int nContent  = 0;  // count of content    elements in phrase
		int nFunction = 0;  //       of noncontent elements
		int nNumber   = 0;  //       of number     elements

		previousSyntax.modifiers = Syntax.moreFeature;

		int n = las.find(); // locate next atom in text

		int phraseSkip = addedSkip + n;
//		System.out.println("phraseSkip= " + phraseSkip);
		addedSkip = 0;

		Pb[Pp++] = Parsing.Phrase;     // indicate start of new phrase
		storeSkip((short) phraseSkip); //     at this relative skip
		Ps = Pps = Pp;                 // remember start of phrase elements

		LexicalAtom a = null;          // pointer to next atom

		for (int k = 0; k < wordlimit; k++) {
			if (las.end()) {
//				System.out.println("end of " + las);
				break;
			}

			// get next atom in phrase

//			System.out.println(las);
			a = las.next();

			if (a == null) {
				System.err.println("input failure");
				break;
			}

//			System.out.println("atom from stream= " + a + ", Pp= " + Pp);

			addedSkip += a.skip;

			if (a.span == 1 && a.length == 0) {  // for simple punctuation
//				System.out.println(">>>> " + las);
//				System.out.println("addedSkip= " + addedSkip);
				int nf = las.find();
//				System.out.println("nf= " + nf);
				addedSkip += nf + 1;         // skip chars for next sentence or phrase
				previousSyntax.type = Syntax.unknownType;
				previousSyntax.modifiers = 0;
//				System.out.println("stop: " + a.stopp + ", " + a.stops);
//				System.out.println("addedSkip= " + addedSkip);
				if (a.stopp || a.stops)
					break;
				else
					continue;
			}

			// get syntactic type for atom

//			System.out.println("Typing");
			if (a.spec.type == Syntax.unknownType)
				a.getSyntax(previousSyntax);   // infer syntax contextually
			previousSyntax.copy(a.spec);           // update context for new atom
//			System.out.println(": " + a);

			// note atom in phrase analysis

//			System.out.println("@" + Pp + " >>a = " + a);
			Pb[Pp++] = a.spec.type;
			Pb[Pp++] = a.spec.modifiers;
			Pb[Pp++] = a.spec.semantics;
			storeSkip((short) addedSkip); // skip before atom
			addedSkip = 0;
			Pb[Pp++] = (byte) a.span;     // length of atom

//			dumpb();

			if (Syntax.functional(a.spec)) { // function word
//				System.out.println("NOT content type");
				nFunction++;
			}
			else {                           // content word
//				System.out.println("content type");
				nContent++;
				Pps = Pp;
				if ((a.spec.type == Syntax.numberType) &&
					(a.spec.modifiers & Syntax.moreFeature) == 0)
					nNumber++;
			}

			// check for end of phrase from Break feature of a word

			if (a.stopp)
				break;
//			System.out.println("break nContent= " + nContent);
			if (nContent > 0 || a.spec.type == Syntax.pronounType) {
//				System.out.println("** " + a);
//				System.out.println(":" + Syntax.breakFeature);
				if ((a.spec.modifiers & Syntax.breakFeature) != 0)
					break;
			}
//			dumpb();
		}

//		System.out.println("terminate phrase with addedSkip= " + addedSkip);

		// back up past trailing non-content words

		trimParse();

		// check for all elements being simple numbers

		if (nContent > 0)
			if (nFunction == 0 && nContent == nNumber) {
				for (int i = 0; i < nNumber; i++) {
//					System.out.println("nNumber= " + nNumber);
//					System.out.println("addedSkip= " + addedSkip);
//					int np = Pp - 6;
//					for (int j = 0; j < 6; j++)
//						System.out.println(String.format(" %02x",Pb[np+j]));
//					System.out.println();
					addedSkip += Pb[--Pp];
					Pp -= 2;
					addedSkip += ByteTool.bytesToShort(Pb,Pp);
					Pp -= 3;
				}
				nContent = nNumber = 0;
			}

//		System.out.println("nContent= " + nContent + ", Pp= " + Pp + " : " + a);
//		System.out.println("addedSkip= " + addedSkip + "*");

//		dumpb();

//		System.out.println("last atom in phrase= " + a);

		if (a != null && a.stops) {

			// new sentence starting

			n = las.find();
//			System.out.println("sent n= " + n);
			if (las.end() || Pp == Sp + 2)
				addedSkip += n;
			else {
//				System.out.println("Phr  Pp= " + Pp);
				Sp = Pp;
				Pb[Pp++] = Parsing.Sentence;
				storeSkip((short)(addedSkip + n));
				addedSkip = 0;
			}

		}
	}

	// strip to core phrase for reporting

	private void trimParse (

	) {
//		System.out.println("trim @" + Pps);
//		dumpb();

		// drop trailing non-content atoms (6 bytes for each)

		for (int Ppt = Pps + 3; Ppt < Pp; Ppt += 6) {
			addedSkip += ByteTool.bytesToShort(Pb,Ppt); // save skip
			addedSkip += Pb[Ppt+2];                     // plus atom length
		}
		Pp = Pps;

		// if empty, drop phrase entirely (3 more bytes)

		if (Pp == Ps) {
			addedSkip += ByteTool.bytesToShort(Pb,Ps-2);
			Pp = Ps - 3;
		}
//		System.out.println("new addedSkip= " + addedSkip);
	}

	//// for debugging
	////

	// show phrase analysis so far

	public void dumpb ( ) {
		PhraseDump.showBytes(new Parsing(phraseCount,Pp,Pb),System.out);
	}

	private static final int N = 6;
	private static final int W = 80;
	private static final int M = 4000;

	public static void main ( String[] a ) {

		String filn = (a.length > 0) ? a[0] : "text";
		try {
			PhraseSyntax.loadDefinitions();
//			WordType.dumpKeys();

			LinedText ll;
			try {
				TestText tt = new TestText(filn);
				ll = new LinedText(tt.getString(),W);
			} catch (AWException e) {
				e.printStackTrace();
				return;
			}
			System.out.println("text loaded");
			int[]  lx = ll.lx;
			int    nl = ll.nl;
			String ss = ll.ts;
			ll.dump();
			System.out.println("test text= [" + ss + "]");

			System.out.println("get literal logic");
			DataInputStream is = ResourceInput.openStream(LiteralPattern.file);
			LiteralType lt = new LiteralType(is);
			is.close();

			TextAnalyzer ta = new TextAnalyzer(ss,lx,nl,lt);
			byte[] pp = new byte[M];
			int lp = 0, no;
			System.out.println("--------");
			lp = ta.analyze(pp,lp,N);
			no = ta.countPhrases();
			System.out.println("======== lp= " + lp);
			ta.dumpb();
			Parsing pa = new Parsing(no,lp,pp);
			PhraseDump.show(ss,pa,PhraseSyntax.getSymbolTable(),System.out);
			System.out.println("DONE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
