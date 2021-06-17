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
// RawVectorFromWords.java : 03Mar00 CPM
// text analysis starting from individual words

package aw.index;

import aw.AWException;
import object.TextSubsegment;
import object.FullTextAnalysis;
import java.io.*;

public class RawVectorFromWords implements VectorDerivation {

	private FullTextAnalysis an; // to break text down to n-grams
	private TextSubsegment   ts;
	
	private int six; // subsegment index
	private int lng; // its length
	
	public RawVectorFromWords ( ) throws AWException {
		an = new FullTextAnalysis();
	}
	
	public boolean set ( int bn, int xn ) throws AWException {
		try {		
			ts = new TextSubsegment(bn,xn);
			String tx = ts.getText();
			six = ts.getIndex();
			lng = tx.length();
			an.setText(tx);
			return true;
		} catch (EOFException e) {
			return false;
		}
	}
	
	public byte[] derive ( ) throws AWException {
		an.run();
		return an.getVector();
	}
	
	public final int index  ( ) { return six; }
	
	public final int length ( ) { return lng; }
	
	public final void close ( ) {
		if (ts != null)
			ts.close();
	}

}