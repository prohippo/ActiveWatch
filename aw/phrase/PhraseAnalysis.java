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
// PhraseAnalysis.java : 21sep2022 CPM
// interpret a byte array as a phrase description for a text item

package aw.phrase;

import aw.AWException;
import aw.ByteTool;
import aw.phrase.Syntax;
import java.io.*;

public class PhraseAnalysis {

	private static boolean syntax = false;

	private String  tx; // the text for phrases
	private Parsing an; // the analysis

	private int paragraphIndex; // which paragraph of item
	private int sentenceIndex;  // which sentence
	private int phraseIndex;    // which phrase

	private int addedSkip; // accumulated skip for next seek
	private int textSkip;  // offset into text of item
	private int textSkips; // saved offset

	private boolean aligned;

	// initialize with text and phrase analysis

	public PhraseAnalysis (

		String  tx, // source text
		Parsing an  // its prior analysis

	) throws IOException {
		this.tx = tx;
		this.an = an;
		if (!syntax) {
			syntax = true;
			Syntax.initialize(new CombinedSymbolTable());
		}
	}

	// get total phrase count

	public final int count ( ) { return an.count; }

	private int bix; // index into parsing buffer

	// sets up iteration at a given phrase number and
	// returns remaining number of phrases

	public int initialize (

		int phraseNumber // starting phrase

	) throws AWException {
		if (an.count == 0)
			return 0;
//		System.out.println("starting at phrase " + phraseNumber);

		if (phraseIndex >= phraseNumber) {	
			paragraphIndex = sentenceIndex = phraseIndex = -1;
			textSkip = addedSkip = 0;
			bix = 0;
			aligned = false;
		}

		while (bix < an.length) {
			addedSkip += alignToPhrase();
			if (phraseNumber == phraseIndex)
				break;
			addedSkip += skipElements();
		}

		return (bix == an.length) ? 0 : (an.count - phraseNumber);
	}

	// move to start of next phrase elements in parsing

	private int alignToPhrase (

	) throws AWException {
		int n;
		int k = 0;        // how many bytes of parsing skipped
		if (aligned)
			return k;

		// go past parsing markers to first phrase

		while (bix < an.length - 3) {
			n = an.buffer[bix++];
			if (n == Parsing.Pad) // Pad has no skip
				continue;
			k += ByteTool.bytesToShort(an.buffer,bix);
			bix += 2;

			if (n == Parsing.Paragraph)
				paragraphIndex++;
			else if (n == Parsing.Sentence)
				sentenceIndex++;
			else if (n == Parsing.Phrase) {
				phraseIndex++;
				break;
			}
			else
				throw new AWException("bad parse marker: " + n);  // error
		}
		aligned = true;
		return k;
	}

	// skip elements to end of phrase

	private int skipElements (

	) {
		int k = 0;
		while (bix < an.length && an.buffer[bix] >= 0) {
			bix += 3;
			k += ByteTool.bytesToShort(an.buffer,bix);;
			bix += 2;
			k += an.buffer[bix++];
		}
		aligned = false;
//		System.out.println(k + " parse bytes SKIPPED");
		return k;
	}

	// returns text offset for next phrase

	public int getPhraseOffset (

	) {
		return textSkips;
	}

	// get phrase elements for next phrase in analysis
	// and return their number

	public int getNextPhrase (

		PhraseElement[] phe

	) throws AWException {
		if (bix == an.length)
			return 0;
//		System.out.println("bix= " + bix + ", blm= " + an.length);

		textSkip += addedSkip;
		textSkips = textSkip;  // save for start of phrase

		// skip over paragraph and sentence markers

		textSkip += alignToPhrase();
		int tb = textSkip;

//		System.out.println("tb= " + tb);
//		for (int i = 0; i < 10; i++)
//			System.out.print(String.format(" %02x",an.buffer[i]));
//		System.out.println();

		// scan analysis to collect phrase elements

		int n = 0;
		for (; bix < an.length && n < phe.length; n++) {
			if (an.buffer[bix] < 0)
				break;
			PhraseElement e = phe[n];
			e.clear();
			SyntaxSpec syntax = e.syntax;
			syntax.type = an.buffer[bix++];
			syntax.modifiers = an.buffer[bix++];
			syntax.semantics = an.buffer[bix++];
			tb += ByteTool.bytesToShort(an.buffer,bix);
			bix += 2;
			int m = an.buffer[bix++];
			e.offset = tb;
			e.length = m;
			if (e.length > PhraseElement.WL)
				e.length = PhraseElement.WL;
			tx.getChars(tb,tb + e.length,e.word,0);
//			System.out.println("gotten word= [" + e.word() + "]");
			tb += m;
		}
		textSkip = tb;

		addedSkip = skipElements();

//		System.out.println(n + " elements");
		return n;
	}

	// returns the phrase index for the preceding phrase

	public final int getPhraseIndex ( ) { return phraseIndex; }

	// returns the sentence index for the preceding phrase

	public final int getSentenceIndex ( ) { return sentenceIndex; }

	// returns the paragraph index for the preceding phrase

	public final int getParagraphIndex ( ) { return paragraphIndex; }

	// convert an analysis into a phrase string

	public String toPhraseString (

		PhraseElement[] phe,
		int ne

	) {
		if (ne == 0)
			return "";

//		System.out.println("get string from " + ne + " elements");
//		for (int i = 0; i < ne; i++)
//			System.out.print(" l=" + phe[i].length);
//		System.out.println();

		// trim grammatical words from both ends

		int nn = 0;
		for (; nn < ne; nn++)
			if (!Syntax.functional(phe[nn].syntax))
				break;
		while (nn < --ne)
			if (!Syntax.functional(phe[nn].syntax)) {
				ne++;
				break;
			}
		if (nn == ne)
			ne++;
		if (nn > ne)
			return "";
//		System.out.println("nn= " + nn + ", ne= " + ne);

		// allocate string for phrase

		int n = phe[nn].offset;
		PhraseElement phenm =  phe[ne-1];
		int nl = phenm.offset + phenm.length;
//		System.out.println("n= " + n + ", nl= " + nl);

		StringBuffer sb = new StringBuffer(nl - n);

		// fill phrase with single spaces as separators

		textSkips = textSkip + addedSkip;

		while (n < nl) {
			char c = tx.charAt(n++);
			if (!Character.isWhitespace(c))
				sb.append(c);
			else {
				sb.append(' ');
				while (n < nl && Character.isWhitespace(tx.charAt(n)))
					n++;
			}
		}

		// strip trailing nonalphanumeric

		int ll = sb.length() - 1;
		for (; ll >= 0; --ll) {
			if (Character.isLetterOrDigit(sb.charAt(ll)))
				break;
		}
		sb.setLength(ll+1);

//		System.out.println("phrase= [" + sb + "]");
		return sb.toString();
	}

	////
	//// for debugging

	private static final String txt = "Emmerson Mnangagwa, who fled into a brief exile after losing.  ";

	private static final byte Bxfc = (byte) 0xfc; // definitions required for byte hex
	private static final byte Bxfd = (byte) 0xfd;
	private static final byte Bxfe = (byte) 0xfe;
	private static final byte Bx80 = (byte) 0x80;
	private static final byte Bx88 = (byte) 0x88;

	private static final byte[] phr = {
		Bxfe,0x00,0x00,Bxfd,0x00,0x00,Bxfc,0x00,0x00,0x17,0x00,0x00,0x00,0x00,0x08,0x00,0x00,0x00,0x00,0x01,
		0x09,Bxfc,0x00,0x06,0x14,0x0c,0x00,0x00,0x00,0x04,Bxfc,0x00,0x06,0x40,0x02,0x00,0x00,0x00,0x01,0x00,
		0x00,0x00,0x00,0x01,0x05,0x00,0x00,0x00,0x00,0x01,0x05,Bxfc,0x00,0x07,0x14,0x0c,0x00,0x00,0x00,0x06
	};

	private static final int Nph = 4;
	private static final int Nby = phr.length;

	private static final int Ne  = 16;

	public static void main ( String[] a ) {
		PhraseElement[] pe = new PhraseElement[Ne];
		for (int i = 0; i < Ne; i++)
			pe[i] = new PhraseElement();
		System.out.println("test PhraseAnalysis " + Nph);
		try {
			Parsing prs = new Parsing(Nph,Nby,phr);
			PhraseSyntax.loadDefinitions();
			PhraseDump.show(txt,prs,PhraseSyntax.getSymbolTable(),System.out);
			PhraseAnalysis phs = new PhraseAnalysis(txt,prs);
			int ne;
			for (int k = 0; (ne = phs.getNextPhrase(pe)) > 0; k++) {
				System.out.println("---- " + k);
				for (int j = 0; j < ne; j++)
					System.out.println(pe[j].word());
				System.out.println("[[" + phs.toPhraseString(pe,ne) + "]]");
			}
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		} catch (AWException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
