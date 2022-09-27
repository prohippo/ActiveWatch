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
// TextParagrapher.java : 27jul2022 CPM
// divide a text into paragraphs

package aw.phrase;

public class TextParagrapher {

	protected static final String Spaces = " \f\t\013\177";
	protected static final String Indent = " \t\377";

	protected static final int W = 32; // nominal width for filled out line

	protected CharArrayWithTypes text;

	protected int[] lining;
	protected int   nlines;

	protected int    start;

	protected Rewriter rwr;

	// initialize with text and line index

	public TextParagrapher (

		CharArrayWithTypes text,
		int[] lining,
		int   nlines

	) {
		this.text = text;
		this.lining = lining;
		this.nlines = nlines;

		rwr = new Rewriter();

		trimTrailingBlanks();
		recognizeStopPunctuation();
	}

	// trim trailing blanks from each line

	private void trimTrailingBlanks (

	) {
		for (int i = 0; i < nlines; i++) {
			int k = lining[i];
			int n = lining[i+1] - 1;
			if (n >= k)
				if (text.charAt(n) == '\n' || text.charAt(n) == '\r')
					while (--n >= k && Character.isWhitespace(text.charAt(n)))
						text.setCharAt(n,LexicalStream.empty);
		}
	}

	// special handling of periods colons

	private static final String brack = "\"[";

	private void recognizeStopPunctuation (

	) {
		int Tp = 1;
		int Tl = lining[nlines];

		for (; Tp < Tl; Tp++) {
			if (text.charAt(Tp) != '.' && text.charAt(Tp) != ':')
				continue;

			// special case of ellipsis

			if (text.charAt(Tp+1) == '.' && text.charAt(Tp+2) == '.') {
				text.setCharAt(Tp++,LexicalStream.empty);
				text.setCharAt(Tp++,LexicalStream.empty);
				text.setCharAt(Tp++,LexicalStream.empty);
				continue;
			}

			// cannot stop unless followed by space or by certain bracketing

			char c = text.charAt(Tp+1);
			if (!Character.isWhitespace(c) && brack.indexOf(c) < 0)
				if (!Character.isLetterOrDigit(c))
					text.setCharAt(Tp, LexicalStream.empty);

		}
	}

	// return offset on text

	private int nxtr = 0;
	private int incr = 0;

	public final int offset ( ) { return incr; }

	// get next paragraph by scanning up to empty line,
	// but across at least one non-empty line

	public CharArrayWithTypes next (

	) {
//		System.out.println("start= " + start + ", nlines= " + nlines);

		int skip = start;

		// scan for empty line

		int k = start;
		for (int count = 0; k < nlines; k++) {

			int p = lining[k];
			p += spanning(p,Spaces);
//			System.out.println("p= " + p);

			if (p != lining[k+1] && text.charAt(p) != '\r')
				count++;
			else if (count > 0)
				break;
			else
				skip++;

		}

		// rewrite specified text elements

		rwr.rewrite(text);

//		System.out.println("skip= " + skip);
		int bs = lining[skip];
		int ln = lining[k] - bs;
		incr = bs - lining[start] + nxtr;

		for (nxtr = 0; ln > 0; --ln, nxtr++)
			if (text.charAt(bs+ln-1) != LexicalStream.empty &&
				text.charAt(bs+ln-1) != '\r')
				break;

//		System.out.println("bs= " + bs + ", ln= " + ln);
		CharArrayWithTypes paragraph = (CharArrayWithTypes) text.subarray(bs,bs+ln);

		start = k;

		return paragraph;
	}

	// equivalent of strspn() in C library

	private int spanning (

		int    pos,
		String set

	) {
		int bas = pos;
		for (; text.charAt(pos) > 0; pos++)
			if (set.indexOf(text.charAt(pos)) < 0)
				break;
		return pos - bas;
	}

	// equivalent of strcspn() in C library

	private int spanningNot (

		int    pos,
		String set

	) {
		int bas = pos;
		for (; text.charAt(pos) > 0; pos++)
			if (set.indexOf(text.charAt(pos)) >= 0)
				break;
		return pos - bas;
	}

}
