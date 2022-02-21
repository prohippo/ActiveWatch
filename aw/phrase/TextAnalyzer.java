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
// TextAnalyzer.java : 11feb2022 CPM
// process an entire text item by paragraphs

package aw.phrase;

import java.io.*;

public class TextAnalyzer {

	private LiteralType lty;
	private TextParagrapher tpg;

	private static boolean defined = false;

	private CharArrayWithTypes cha; // writeable copy of text

	public TextAnalyzer (

		String text,
		int[]  lining,
		int    nlines,
		LiteralType lty

	) throws IOException {
		if (!defined) {
			SymbolTable stb = PhraseSyntax.getSymbolTable();
			WordType.load(stb);
			defined = true;
		}
		int n = text.length();
		int k = lining[nlines];
		if (n > k)
			n = k;
		cha = new CharArrayWithTypes(text);
		tpg = new TextParagrapher(cha,lining,nlines);
		this.lty = lty;
	}

	private int Pp; // parse pointer
	private int Sp; // last sentence start
	private byte[] Pb;

	private int addedSkip  = 0;
	private int itemNumber = 0;
	private SyntaxSpec previousSyntax = new SyntaxSpec();

	private int phraseCount;

	// store offset, possibly require multiple bytes

	private int setPosition (

		int offset

	) {
		while (offset > Parsing.OverflowValue) {
			Pb[Pp++] = Parsing.Overflow;
			offset -= Parsing.OverflowValue;
		}
		return offset;
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
		if (tx == null || tx.length() == 0)
			return 0;

		int o = tpg.offset();

		System.out.println("offset= " + o);
		LexicalAtomStream las = new LexicalAtomStream(tx,lty);

		int n = las.find();
		n = setPosition(n);

		System.out.println("n= " + n);
		Pb[Pp++] = Parsing.Paragraph;
		Pb[Pp++] = (byte)(n + o);
		int length = 2;
		if (!las.end()) {
			Sp = Pp;
			Pb[Pp++] = Parsing.Sentence;
			Pb[Pp++] = 0;
			length += 2;
		}

		// process each phrase and store analyses

		while (!las.end()) {
			int Pps = Pp;
			extractPhrase(las,wordlimit);

			if (Pp > Pps) {
				length += (Pp - Pps);
				phraseCount++;
			}
		}
		return length;
	}

	// extract next phrase from lexical atom stream

	private void extractPhrase (

		LexicalAtomStream las,
		int wordlimit

	) {
		int nContent  = 0; // count of content elements in phrase
		int nFunction = 0; //       of noncontent elements
		int nNumber   = 0; //       of number elements

		previousSyntax.type = Syntax.unknownType;
		previousSyntax.modifiers = Syntax.moreFeature;

		int n = las.find();

		int phraseSkip = addedSkip + n;
		addedSkip = 0;

		int Pps,Ppt,Ppz;

		Pps = Ppz = Pp;
		phraseSkip = setPosition(phraseSkip);
		Ppt = Pp;
		Pb[Pp++] = Parsing.Phrase;
		Pb[Pp++] = (byte) phraseSkip;

		LexicalAtom a = null;

		for (int k = 0; k < wordlimit; k++) {
			if (las.end())
				break;

			// get next word in phrase

			a = las.next();
			System.out.println("atom= " + a);
			if (a == null)
				break;

			addedSkip += a.skip;

			if (a.length == 0) {
				previousSyntax.type = Syntax.unknownType;
				previousSyntax.modifiers = 0;
				addedSkip += a.span;
				if (a.stopp || a.stops)
					break;
				else
					continue;
			}

			// get syntactic type for atom

			if (a.spec.type == Syntax.unknownType)
				a.getSyntax(previousSyntax);
			previousSyntax.copy(a.spec);

			// check for syntactic end of phrase

			if (nContent > 0)
				if ((a.spec.modifiers & Syntax.breakFeature) != 0) {
					if (!Syntax.positionalType(a.spec))
						addedSkip += a.span;
					else {
						a.skip = 0;
						las.backUp(a);
					}
					break;
				}

			// note token syntax type in phrase

			Pb[Pp++] = a.spec.type;
			Pb[Pp++] = a.spec.modifiers;

			if (!Syntax.positionalType(a.spec)) {
				addedSkip += a.span;
				nFunction++;
			}
			else {
				addedSkip = setPosition(addedSkip);
				Pb[Pp++] = a.spec.semantics;
				Pb[Pp++] = (byte) addedSkip; // skip
				Pb[Pp++] = (byte) a.span;    // length
				Pps = Pp;
				addedSkip = 0;
				nContent++;
				if ((a.spec.type == Syntax.numberType) &&
					(a.spec.modifiers & Syntax.moreFeature) == 0)
					nNumber++;
			}
		}

		// back up past trailing non-content words

		Pp = Pps;

		// check for all elements being simple numbers

		if (nContent > 0)
			if (nFunction == 0 && nContent == nNumber) {
				for (int i = 0; i < nNumber; i++) {
					addedSkip += Pb[--Pp];
					addedSkip += Pb[--Pp];
					Pp -= 3;
				}
				nContent = 0;
			}

		if (nContent == 0) {

			// if no content, eliminate phrase

			addedSkip += phraseSkip;
			while (--Ppt >= Ppz)
				addedSkip += Parsing.OverflowValue;
			Pp = Ppz;

		}

		if (a != null && a.stops) {

			// new sentence starting

			n = las.find();
			if (las.end() || Pp == Sp + 2)
				addedSkip += n;
			else {
				addedSkip = setPosition(addedSkip + n);
				Sp = Pp;
				Pb[Pp++] = Parsing.Sentence;
				Pb[Pp++] = (byte) addedSkip;
				addedSkip = 0;
			}
		}
	}

}
