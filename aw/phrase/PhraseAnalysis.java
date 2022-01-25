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
// PhraseAnalysis.java : 24jan2022 CPM
// interpret a byte array as a phrase description for a text item

package aw.phrase;

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
	
		String  tx,
		Parsing an
		
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
	
	private int input; // index into parsing
	
	// sets up iteration at a given phrase number and
	// returns remaining number of phrases

	public int initialize (
	
		int phraseNumber // starting phrase
		
	) {
		if (an.count == 0)
			return 0;
		
		if (phraseIndex >= phraseNumber) {	
			paragraphIndex = sentenceIndex = phraseIndex = -1;
			textSkip = addedSkip = 0;
			input = 0;
			aligned = false;
		}

		while (input < an.length) {
			addedSkip += alignToPhrase();
			if (phraseNumber == phraseIndex)
				break;
			addedSkip += skipElements();
		}
		
		textSkips = textSkip + addedSkip;

		return (input == an.length) ? 0 : (an.count - phraseNumber);
	}
	
	// move to start of next phrase in parsing

	private int alignToPhrase (
	
	) {
		int n;
		int k = 0;
		if (aligned)
			return k;

		// to first occurrence of phrase tag
		
		while (input < an.length) {
			n = an.buffer[input++];
			if (n == Parsing.Overflow)
				k += Parsing.OverflowValue;
			else if (n == Parsing.Paragraph)
				paragraphIndex++;
			else if (n == Parsing.Sentence)
				sentenceIndex++;
			else if (n == Parsing.Phrase) {
				phraseIndex++;
				break;
			}
			else
				k += n;
		}
		
		// add in its offset
		
		while (input < an.length) {
			n = an.buffer[input++];
			if (n == Parsing.Overflow)
				k += Parsing.OverflowValue;
			else {
				k += n;
				break;
			}
		}
		
		aligned = true;
		return k;
	}

	// skip elements to end of phrase
	
	private int skipElements (
	
	) {
		int k = 0;
		while (input < an.length && an.buffer[input] >= 0) {
			input++;
			byte modifiers = an.buffer[input++];
			if ((modifiers & Syntax.functionalFeature) == 0) {
				input++;
				k += an.buffer[input++];
				k += an.buffer[input++];
			}
		}
		aligned = false;
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

	) {
		if (input == an.length)
			return 0;
			
		textSkip += addedSkip;

		// skip over paragraph and sentence markers

		textSkip += alignToPhrase();
		int text = textSkip;

		// scan analysis to identify phrase elements

		int n = 0;
		for (; input < an.length && n < phe.length; n++) {

			if (an.buffer[input] < 0)
				break;

			PhraseElement e = phe[n];
			SyntaxSpec syntax = e.syntax;
			syntax.type = an.buffer[input++];
			syntax.modifiers = an.buffer[input++];
			if ((syntax.modifiers & Syntax.functionalFeature) != 0) {
				syntax.semantics = 0;
				e.offset = text;
				e.length = 0;
				e.word[0] = 0;
			}
			else {
				syntax.semantics = an.buffer[input++];
				text += an.buffer[input++];
				int m = an.buffer[input++];
				e.offset = text;
				e.length = m;
				if (e.length > PhraseElement.WL)
					e.length = PhraseElement.WL;
				tx.getChars(text,text + e.length,e.word,0);
				text += m;
			}
		}
		textSkip = text;
		
		addedSkip = skipElements();

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
			
		// trim grammatical words from both ends
		
		int nn = 0;
		for (; nn < ne; nn++)
			if (phe[nn].length > 0)
				break;
		while (nn < --ne)
			if (phe[ne].length > 0)
				break;
		if (nn > ne)
			return "";
		
		// allocate string for phrase
				
		int n  = phe[nn].offset;
		int nl = phe[ne].offset + phe[ne].length;
			
		StringBuffer sb = new StringBuffer(nl - n);
		
		// fill phrase with single spaces as separators
		
		while (n < nl) {
			char c = tx.charAt(n++);
			if (!Character.isWhitespace(c))
				sb.append(c);
			else {
				sb.append(' ');
				while (Character.isWhitespace(tx.charAt(n)))
					n++;
			}
		}
		return sb.toString();
	}
	
}

