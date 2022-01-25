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
// PhraseScorer.java : 19Jun00 CPM
// compute composite n-gram score for phrase

package aw.phrase;

import aw.*;
import stem.*;
import gram.SortedList;
import object.HashTable;
import object.AnalyzedToken;
import object.KeyTextAnalysis;

public class PhraseScorer {

	private static final int htSZ = 997;

	private byte[] wv; // n-gram weights for scoring
	private HashTable ht;  // to keep track of distinct words
	private KeyTextAnalysis an; // to analyze phrase elements

	private SortedList list;
	
	// initialize
	
	public PhraseScorer (
	
		KeyTextAnalysis an
		
	) {
		this.an = an;
		ht = new HashTable(htSZ);
		list = new SortedList(Token.MXW+4);
	}
	
	// set new weights for scoring
	
	public void set (
	
		byte[] wv
	
	) {
		this.wv = wv;
		ht.clear();
	}
	
	// for signature building
	
	private static final int nR = 16;
	
	private Record[] sl = new Record[nR+1];
	private int     nsl;
	
	// score a phrase string

	public TaggedPhrase score (
	
		PhraseElement[] pe,
		int             ne,
		TaggedPhrase    phs
		
	) {
		int lm = phs.signature.length;
		if (lm >= sl.length)
			sl = new Record[lm + 1];
			
		nsl = 0;
		
		// compile score from tokens and get hash those with non-zero scores
		
		phs.score = 0;
		
		for (int i = 0; i < ne; i++) {
			PhraseElement phe = pe[i];
			if (phe.length > 0) {
				an.setText(phe.word());
				
				// analyzed each phrase element separately
				
				int n = 0;
				AnalyzedToken to;
				for (; (to = an.next()) != null; n++) {
					int score = to.score(1,wv);
					if (score > 0) {
						phs.score += score;
						
						// get hash for word in data set
						
						String w = to.toString();
						int k = ht.lookUp(w);
						if (k == 0)
							continue;
						else if (k < 0) {
							k = -k;
							ht.array[k-1] = w;
						}
						
						// save unique key for signature
						
						int j = nsl;
						for (; j > 0; --j)
							if (sl[j-1].hash >= k)
								break;
								
						if (j == 0 || sl[j-1].hash != k) {
							if (j < nsl)
								System.arraycopy(sl,j,sl,j+1,nsl-j);
							Record r = new Record();
							r.hash  = k;
							r.score = score;
							sl[j] = r;
							if (nsl < nR)
								nsl++;
						}
					}
				}
				if (n == 0)
					pe[i].length = 0;
			}
		}
		
		// sort keys by score
		
		Record r;
		
		for (int i = 1; i < nsl; i++) {
			r = sl[i];
			int j = i;
			for (; j > 0; --j) {
				if (sl[j-1].score >= r.score)
					break;
				sl[j] = sl[j-1];
			}
			sl[j] = r;
		}
		
		// resort by weight
		
		if (lm > nsl)
			lm = nsl;
			
		for (int i = 1; i < lm; i++) {
			r = sl[i];
			int j = i;
			for (; j > 0; --j) {
				if (sl[j-1].hash < r.hash)
					break;
				sl[j] = sl[j-1];
			}
			sl[j] = r;
		}
		
		// copy phrase signature
		
		for (int i = 0; i < lm; i++)
			phs.signature[i] = sl[i].hash;
			
		phs.order = lm;
			
		return phs;
	}
	
}

// for selecting signature words

class Record {

	int hash;
	int score;
	
}