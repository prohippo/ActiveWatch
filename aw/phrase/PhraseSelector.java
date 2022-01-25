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
// PhraseSelector.java : 20Nov98 CPM
// maintain a selection list

package aw.phrase;

public class PhraseSelector {

	// intentional package access for selection array
	
	int limit;
	int count;
	TaggedPhrase[] array;
	
	private int nwd; // phrase width in words
	
	// initialize
	
	public PhraseSelector (
	
		int nph,
		int nwd
		
	) {
		limit = nph;
		array = new TaggedPhrase[limit+1];
		this.nwd = nwd;
	}
	
	// set selection array to empty
	
	public void reset (
	
	) {
		count = 0;
	}
	
	// add phrase to selection array
	
	public void add (
	
		TaggedPhrase ph
		
	) {
		// check whether phrase has any significant content

		if (ph.order == 0)
			return;

		// compare signature of new phrase against old ones

		boolean squeeze = false;
		
		for (int i = 0; i < count; i++) {
			if (subsume(array[i].signature,ph.signature)) {
			
				// found previous, possibly more specific, version of phrase

				if (subsume(ph.signature,array[i].signature))
					if (ph.phrase.length() > array[i].phrase.length())
						array[i] = ph;
				return;
			}
			else if (subsume(ph.signature,array[i].signature)) {

				// found previous, but more general, version

				array[i].score = 0; // tag for removal on insertion
				squeeze = true;
			}
		}
		
		// remove any more general phrases
		
		if (squeeze) {
			int k = 0;
			for (int i = 0; i < count; i++)
				if (array[i].score > 0)
					array[k++] = array[i];
			count = k;
		}
		
		// insert new phrase
		
		int i = count;
		for (; i > 0; --i) {
			if (array[i-1].order > ph.order)
				break;
			if (array[i-1].order == ph.order)
				if (array[i-1].score >= ph.score)
					break;
			array[i] = array[i-1];
		}
		array[i] = ph;
		if (count < limit)
			count++;
		
	}

	// checks whether signature vector s includes vector t
	
	private boolean subsume (
	
		int[] s,
		int[] t 

	) {
		int n;

		for (int j = 0, k = 0; t[k] > 0;) {
			if (j == nwd || (n = s[j]) == 0 || n < t[k])
				return false;
			if (n >= t[k]) j++;
			if (n == t[k]) k++;
			if (k == nwd)  break;
		}
		return true;
	}
	
}

