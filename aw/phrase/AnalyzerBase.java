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
// AnalyzerBase.java : 08set2022 CPM
// methods for phrase analysis of one text item at a time

package aw.phrase;

import aw.AWException;
import aw.ResourceInput;
import java.io.*;

public class AnalyzerBase {

	private static final int NL = 64; // maximum number of lines to process
	private static final int RN = 20; // for length of debugging output

	private LiteralType lty;
	private Reparser    rps;

	private byte[] analysis; // results of analysis

	private int wlm;         // word limit in phrase

	// initialize language tables

	public AnalyzerBase (

		int nb, // byte limit for output
		int wlm // word limit for any phrase

	) throws AWException {

		try {
			PhraseSyntax.loadDefinitions();
			System.out.println(PhraseSyntax.getSymbolTable());
			DataInputStream is = ResourceInput.openStream(LiteralPattern.file);
			lty = new LiteralType(is);
			is.close();
			rps = new Reparser(PhraseSyntax.getSymbolTable());
		} catch (IOException e) {
			throw new AWException(e);
		}

		this.wlm = wlm;
		analysis = allocate(nb);
	}

	// allow for overriding

	public byte[] allocate (

		int n

	) {
		return new byte[n];
	}

	// process a lined item by paragraphs

	public Parsing analyze (

		LinedText lt

	) throws AWException {
		int nPhrase = 0;
		int lAnalysis = 0;

		int nl = (lt.nl < NL) ? lt.nl : NL;

		TextAnalyzer ta;
		try {
			ta = new TextAnalyzer(lt.ts,lt.lx,nl,lty);
		} catch (IOException e) {
			throw new AWException(e);
		}

		// parse each paragraph

		int n;
		while ((n = ta.analyze(analysis,lAnalysis,wlm)) > 0) {
			lAnalysis += n;
			nPhrase += ta.countPhrases();
//			System.out.println("lAnalysis= " + lAnalysis + ", nPhrase= " + nPhrase);
			int j = lAnalysis - RN;
			if (j < 0) j = 0;
			for (; j < lAnalysis; j++)
				System.out.print(String.format(" %02x",analysis[j]));
			System.out.println(String.format(" | %02x %02x",analysis[j],analysis[j+1]));
		}

		// even out length of analysis by padding

		analysis[lAnalysis] = Parsing.Pad;
		if ((lAnalysis%2) > 0)
			lAnalysis++;

		Parsing parsg = new Parsing(nPhrase,lAnalysis,analysis);
//		System.out.println(parsg);
		return parsg;
	}

	// special-case adjustments of analysis

	public void reparse (

		String text,
		Parsing parse

	) {
		rps.reparse(text,parse);
	}

	// show phrase analysis

	public void showPhrases (

		String text,
		Parsing parse

	) {
		try {
			PhraseDump.show(text,parse,PhraseSyntax.getSymbolTable(),System.out);
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
