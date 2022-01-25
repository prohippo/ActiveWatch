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
// PhraseExtractor.java : 20Nov98 CPM
// get phrases from a text sample

package aw.phrase;

import aw.AWException;
import object.KeyTextAnalysis;
import java.io.*;

class TaggedPhrase {

	String phrase;    // normalized as string
	int[]  signature; // of weighted words
	int    score;     // total phrase score
	int    order;     // number of non-zero entries in signature
	
	public TaggedPhrase (
	
		int n
		
	) {
		signature = new int[n];
	}

}

public class PhraseExtractor {

	private int   nwd; // how big to make signature
	
	private PhraseScorer    sr;
	private PhraseAnalysis  an;
	private KeyTextAnalysis ka;
	
	private PhraseElement[] pe; // actual phrase

	public PhraseExtractor (
	
		int nwd
	
	) throws AWException {
		this.nwd = nwd;
		ka = new KeyTextAnalysis();
		sr = new PhraseScorer(ka);
		pe = new PhraseElement[nwd];
		for (int i = 0; i < nwd; i++)
			pe[i] = new PhraseElement();
	}
	
	public void reset (
	
		String  tx,
		Parsing pp,
		byte[]  wv
		
	) throws IOException {
		an = new PhraseAnalysis(tx,pp);
		an.initialize(0);
		sr.set(wv);
	}
	
	public TaggedPhrase next (
	
	) {
		int ne = an.getNextPhrase(pe);
		if (ne == 0)
			return null;
		TaggedPhrase ph = new TaggedPhrase(nwd);
		sr.score(pe,ne,ph);
		ph.phrase = an.toPhraseString(pe,ne);
		return ph;
	}


}