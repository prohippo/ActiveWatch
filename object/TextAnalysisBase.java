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
// AW file TextAnalysisBase.java : 19sep2022  CPM
// class for basic tokenization

package object;

import aw.*;
import stem.*;

public class TextAnalysisBase extends TextAnalysisFile {

	protected Tokenizer tokenizer; // produce stemmed tokens from text

	// initialize for lexical text analysis

	public TextAnalysisBase (

	) throws AWException{

		StemStart.load(suffixFile,stopFile,stopPatternFile);
		setTokenizer();

	}

	// allow for override

	protected void setTokenizer (

	) {

		tokenizer = new Tokenizer(StemStart.tree,StemStart.table,StemStart.list);

	}

	// specify input text

	public final void setText ( String text ) { tokenizer.set(text); }

	// get next token

	public final Token getToken ( ) { return tokenizer.get(); }

	// allow access to morphological stemming
	 
	public final Stem stem ( ) { return StemStart.tree; }

	// get current position in analyzing a text segment

	public final int getNextPosition ( ) { return tokenizer.getOffset(); }

}
