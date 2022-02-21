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
// TextParagrapher.java : 10feb2022 CPM
// divide a text into paragraphs

package aw.phrase;

public class TextParagrapher {

	private static final String Spaces = " \f\t\013\177";
	private static final String Indent = " \t\377";

	private static final int W = 32; // nominal width for filled out line

	private CharArrayWithTypes text;

	private int[] lining;
	private int   nlines;

	private int    start;

	private Rewriter rwr;

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

	// special handling of periods

	private void recognizeStopPunctuation (

	) {
		int Tp = 1;
		int Tl = lining[nlines];

		for (; Tp < Tl; Tp++) {
			if (text.charAt(Tp) != '.')
				continue;

			// special case of ellipsis

			if (text.charAt(Tp+1) == '.' && text.charAt(Tp+2) == '.') {
				text.setCharAt(Tp++,LexicalStream.empty);
				text.setCharAt(Tp++,LexicalStream.empty);
				text.setCharAt(Tp++,LexicalStream.empty);
				continue;
			}

			// cannot stop unless followed by space

			if (!Character.isWhitespace(text.charAt(Tp+1)))
				if (!Character.isLetterOrDigit(text.charAt(Tp+1)))
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
		int skip = start;

		// scan for empty line

		int k = start;
		for (int count = 0; k < nlines; k++) {

			int p = lining[k];
			p += spanning(p,Spaces);

			if (p != lining[k+1] && text.charAt(p) != '\r')
				count++;
			else if (count > 0)
				break;
			else
				skip++;

		}

		// rewrite specified text elements

		rwr.rewrite(text);

		// check for tabular formatting

		interpretIndentation(skip,k);

		delineateColumns(skip,k);

		int bs = lining[skip];
		int ln = lining[k] - bs;
		incr = bs - lining[start] + nxtr;

		for (nxtr = 0; ln > 0; --ln, nxtr++)
			if (text.charAt(bs+ln-1) != LexicalStream.empty &&
				text.charAt(bs+ln-1) != '\r')
				break;

		CharArrayWithTypes paragraph = (CharArrayWithTypes) text.subarray(bs,bs+ln);

		start = k;

		return paragraph;
	}

	private static final char Tab = '\t';

	// to tell when indentation starts a paragraph

	private void interpretIndentation (

		int skip,
		int limit

	) {
		int Tp = lining[skip];
		int in = spanning(Tp,Indent);

		// compare subsequent lines with first

		for (int i = skip + 1; i < limit; i++) {
			Tp = lining[i];
			if (!Character.isWhitespace(text.charAt(Tp)))
				continue;

			int ln = Tp - lining[i-1];

			// find bounds of first continuous element of line

			int k = spanning(Tp,Indent);
			Tp += k;
			int n = spanningNot(Tp,Spaces);

			if (ln + n - in + k > W)

				// if previous line filled out, treat as indentation

				while (Tp > lining[i])
					text.setCharAt(--Tp,LexicalStream.empty);

			else {

				// otherwise, treat as tabular break

				Tp = lining[i] - 1;
				if (Character.isWhitespace(text.charAt(Tp)))
					text.setCharAt(Tp,Tab);
			}
			in = k;
		}
	}

	// detect next possible column break

	private int scanForBreak (

		int is

	) {
		int il = lining[nlines] - 3;
		for (; is < il; is++) {
			if (text.charAt(is) == ' ')
				if (text.charAt(is+1) == ' ')
					return is;
				else
					is++;
		}
		return -1;
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

	private static final int NPOS = 6; // maximum number of columns in text
	private static final int MINL = 4; // minimum line count

	private int  npos = 0;
	private int[] pos = new int[NPOS];
	private int[] poe = new int[NPOS];

	// detect table formatting

	private void delineateColumns (

		int skip,
		int limit

	) {
		if (nlines - skip < MINL)
			return;

		boolean pattern = false;

		for (int i = skip; i <limit; i++) {

			// look for columns aligned with spaces

			int tp = scanForBreak(lining[i]);
			if (tp < 0)
				break;

			for (tp += 2; text.charAt(tp) == ' '; tp++);
			if (text.charAt(tp) == 0)
				break;

			for (; lining[i] <= tp; i++); --i;

			// uncorroborated by preceding line?

			if (!pattern) {

				if (i == nlines - 1)
					break;

				// find all extra spaces in current line
				// (already found one such)

				int lp = lining[i];
				int ln = lining[i + 1] - lining[i];
				npos = 0;
				for (int j = 0; j < ln; ) {
					while (j < ln && text.charAt(lp + j++) != ' ');
					if (j == ln) break;
					int k = 1;
					for (; text.charAt(lp + j) == ' '; j++, k++);
					if (k > 1) {
						pos[npos] = j - k;
						poe[npos] = j - 1;
						if (++npos == NPOS)
							break;
					}
				}

				if (npos == 1 && pos[0] == 0)
					continue;

				if (corroborateSpacing(i + 1)) {
					markSpacing(i);
					pattern = true;
				}
			}
			else if (corroborateSpacing(i))
				markSpacing(i);
			else {
				pattern = false; --i;
			}
		}
	}

	// put in explicit tabbing for spaces in specified line

	private void markSpacing (

		int k

	) {
		int pp;
		int lp = lining[k];
		for (int j = 0; j < npos; j++) {
			pp = scanForBreak(lp + pos[j]);
			if (pp < 0)
				break;
			text.setCharAt(pp,Tab);
		}
		pp = lining[k + 1] - 1;
		if (Character.isWhitespace(text.charAt(pp)))
			text.setCharAt(pp,Tab);
	}

	// column alignments need to show up on consecutive lines

	private boolean corroborateSpacing (

		int   k

	) {
		int lp = lining[k];
		int ln = lining[k + 1] - lining[k];
		int j = 0;
		for (; j < npos; j++) {
			if (pos[j] >= ln || poe[j] >= ln)
				break;
			int m,n;
			for (m = poe[j], n = pos[j]; m > n; n++)
				if (text.charAt(lp + n) == ' ' && text.charAt(lp + n + 1) == ' ')
					break;
			if (m == n)
				break;
		}
		return (j == npos);
	}

}
