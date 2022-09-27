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
// CompoundPhraseAnalysis.java : 06aug2022 CPM
// for subsegmented text

package aw.phrase;

import aw.AWException;
import java.io.*;

public class CompoundPhraseAnalysis extends PhraseAnalysis {

	int limit; // limit of subsegment in text

	// start with alignment to first phrase within subsegment

	public CompoundPhraseAnalysis (

		String  tx, // full text of item
		Parsing an, // full analysis
		int     os, // offset of subsegment
		int     ln  // length

	) throws IOException, AWException {
		super(tx,an);
		limit = os + ln;
		int n = count();
		for (int k = 0; k < n; k++) {
			initialize(k);
			if (getPhraseOffset() >= os)
				break;
		}
	}

	// for phrases in subsegment

	public int getNextPhrase (

		PhraseElement[] phe

	) throws AWException {
		int n = super.getNextPhrase(phe);
		return (getPhraseOffset() > limit) ? 0 : n;
	}

}
