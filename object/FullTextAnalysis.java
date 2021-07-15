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
// AW file FullTextAnalysis.java : 14jul2021 CPM
// analyze an entire text segment

package object;

import aw.*;
import stem.*;
import gram.*;

public class FullTextAnalysis extends TextAnalysis {

	protected byte[] vb; // arrayed n-gram occurrence counts
	
	// initialize
	
	public FullTextAnalysis (
	
	) throws AWException {
	
		super();
		
	}
	
	// process text data
		
	public void run (
	
	) throws AWException {
	
		Token tok;
		int     n; // list length
		Short[] g; // actual list
		
		vb = new byte[Parameter.MXI+2];
		
		while ((tok = tokenizer.get()) != null) {
			characterizer.set(tok);
			g = characterizer.list();
			n = g.length;

			for (int j = 0; j < n; j++)
				vb[g[j]]++;
		}
		
	}
	
	// get results as vector of counts
	
	public final byte[] getVector ( ) { return vb; }
	
}
