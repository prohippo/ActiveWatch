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
// AnalyzerBase.java : 03Mar00 CPM
// functions broken up for analysis of one item at a time

package aw.phrase;

import aw.AWException;
import aw.ResourceInput;
import java.io.*;

public class AnalyzerBase {

	private static final int NL = 64; // maximum number of lines to process

	private LiteralType lty;
	private Reparser    rps;
	
	private byte[] analysis; // results of analysis
	
	private int wlm; // word limit in phrase
	
	// initialize language tables

	public AnalyzerBase (
	
		int nb,
		int wlm
		
	) throws AWException {
		
		try {
			PhraseSyntax.loadDefinitions();
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
		}

		// even out length of analysis by padding
		
		analysis[lAnalysis] = Parsing.Pad;
		if ((lAnalysis%2) > 0)
			lAnalysis++;

		return new Parsing(nPhrase,lAnalysis,analysis);
	}
	
	// special-case adjustments of analysis
	
	public void reparse (
	
		String text,
		Parsing parse
	
	) {
		rps.reparse(text,parse);
	}
		
}
