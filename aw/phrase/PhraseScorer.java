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
// PhraseScorer.java : 26sep2022 CPM
// compute composite n-gram score for phrase

package aw.phrase;

import aw.*;
import stem.*;
import gram.SortedList;
import object.HashTable;
import object.AnalyzedToken;
import object.KeyTextAnalysis;
import java.util.List;

// to computer aggregate phrase score from element contributions

public class PhraseScorer {

	private static final int htSZ = 4999;  // prime number

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

	// set new profile weights for scoring

	public void setWeighting (

		byte[] wv

	) {
//		System.out.println("new weighting");
		this.wv = wv;
		ht.clear();
	}

	// building up phrase signatures

	private static short[] siga = new short[12];
	private static int sigx;

	// score a phrase bu itss elements and set a signature

	public TaggedPhrase score (

		PhraseElement[] pe,  // elements of new phrase
		int             ne,  // element count
		TaggedPhrase    phs  // where to put scoring

	) {
//		System.out.println("scoring " + ne + " elements");

		// compile phrase score from its tokens and get hash codes for contribulting tokens

		sigx = 0;      // initialize for tagging
		phs.score = 0; //

		for (int i = 0; i < ne; i++) {          // look at each phrase element
			PhraseElement phe = pe[i];
//			System.out.println(i + ")) " + phe.word());
			if (phe.length > 0) {
				an.setText(phe.word()); // parse its text into tokens
				AnalyzedToken to;       // for receiving next token
				while ((to = an.next()) != null) {
					int score = to.score(1,wv);
//					System.out.println("token [" + to + "]= " + score);
//					Short[] gm = to.indices();
//					System.out.print("grams= ");
//					int gml = to.count();
//					for (int j = 0; j < gml; j++)
//						System.out.print(" " + gm[j]);
//					System.out.println();
					if (score > 0) {
						phs.score += score;

						// look for word in current text item hash table

						String w = to.toString().trim();
						int k = ht.lookUp(w);
						if (k == 0)
							continue;   // hash table full!
						else if (k < 0) {
							k = -k;
							ht.store(); // put canonical key into table
						}
//						System.out.println("## " + w + " , hash= " + k);
//						System.out.println("ne= " + ne);
						siga[sigx++] = (short) k;
//						System.out.println("signature size= " + sigx);
					}
				}
				phs.signature = new short[sigx];
				System.arraycopy(siga,0,phs.signature,0,sigx);
			}
		}

		phs.order = phs.signature.length;
//		System.out.println("tag: " + phs);
		return phs;
	}

	public void hashdump () {
		ht.dump();
	}

}
