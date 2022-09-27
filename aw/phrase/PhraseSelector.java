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
// PhraseSelector.java : 26sep2022 CPM
// maintain a selection list

package aw.phrase;

import aw.phrase.PhraseExtractor;

public class PhraseSelector {

	// for phrase selection array
	
	int limit;  //
	int bound;  //
	int count;  // current phrase account
	TaggedPhrase[] array; // for selection
	
	private int nwd; // maximum phrase width in words
	
	// initialize selection array
	
	public PhraseSelector (
	
		int nph, // how many to select
		int nwd  // limit on phrase elements
		
	) {
		limit = nph;
		array = new TaggedPhrase[limit*2];
		bound = array.length - 1;
		this.nwd = nwd;
		count = 0;
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
//		System.out.println("checking " + ph);

		// check whether phrase has any significant content

		if (ph.score == 0)
			return;
//		System.out.println("adding " + ph);

		// select phrases with top scores

		boolean squeeze = false; // for elimination of subsumed phrases

		int k = count - 1;       // at lowest scoring phrase
		for (; k >= 0; --k) {
			TaggedPhrase phk = array[k];
			if (ph.score < phk.score)
				break;
			if (subsume(ph.signature,ph.order,phk.signature,phk.order)) {
				phk.score = 0;
				squeeze = true;
			}
			array[k+1] = phk;
		}
//		System.out.println("insert at " + (k+1));
		array[k+1] = ph;
		if (count < bound)
			count++;
		for (int j = k; j >= 0; --j) {
			TaggedPhrase phj = array[j];
			if (subsume(phj.signature,phj.order,ph.signature,ph.order)) {
				ph.score = 0;
//				System.out.println("subsume from above @" + j + ") " + phj);
				squeeze = true;
				break;
			}
		}

		// prune subsumed phrases with zeroed scores
		
		if (squeeze) {
//			System.out.print("squeezing " + count);
			int ik = 0;
			for (int i = k + 1; i < count; i++)
				if (array[i].score == 0)
					ik = i;
			for (int i = ik + 1; i < count; i++)
				if (array[i].score > 0)
					array[ik++] = array[i];
			count = ik;
//			System.out.println(" to " + count);
		}

//		System.out.println("updated count= " + count);
	}

	// checks whether signature vector s includes vector t
	
	private boolean subsume (
		short[] s, // first signature
		int    ls, // its actual length
		short[] t, // second
		int    lt  // its actual length
	) {
		if (ls == 0 || lt == 0 || ls < lt)
			return false;
//		System.out.println("ls= " + ls + ", lt= " + lt);
		if (ls == lt) { // bracketing needed here!
			for (int i = 0; i < ls; i++) {
				if (s[i] != t[i])
					return false;
			}
		}
		else if (ls < lt)
			return false;
		else {
			int d = ls - lt + 1;
//			System.out.println("d= " + d);
			int nt = t[0];
			int k = 0;
			for (; k < d; k++) {
				if (nt == s[k])
					break;
			}
//			System.out.println("k= " + k);
			if (k >= d)
				return false;
			for (int j = 1; j < lt; j++) {
				if (s[k+j] != t[j])
					return false;
			}
		}
//		System.out.println("subsumed");
		return true;
	}

	////
	//// for debugging

	public void dump ( ) {
		for (int i = 0; i < count; i++)
			System.out.println(i + ") " + array[i]);
		System.out.println("--------");
	}

	static TaggedPhrase tpr = new TaggedPhrase("rrrrrr",new short[]{7}      ,1,5);
	static TaggedPhrase tps = new TaggedPhrase("ssssss",new short[]{6}      ,1,7);
	static TaggedPhrase tpt = new TaggedPhrase("tttttt",new short[]{6}      ,1,7);
	static TaggedPhrase tpu = new TaggedPhrase("uuuuuu",new short[]{9,1}    ,2,4);
	static TaggedPhrase tpv = new TaggedPhrase("vvvvvv",new short[]{8,9,1}  ,3,6);
	static TaggedPhrase tpw = new TaggedPhrase("wwwwww",new short[]{2,3}    ,2,3);
	static TaggedPhrase tpx = new TaggedPhrase("xxxxxx",new short[]{1,5,3}  ,3,4);
	static TaggedPhrase tpy = new TaggedPhrase("yyyyyy",new short[]{1,2,3,4},4,0);
	static TaggedPhrase tpz = new TaggedPhrase("zzzzzz",new short[]{1,3,4}  ,3,4);

	public static void main ( String[] a ) {
		PhraseSelector ps = new PhraseSelector(4,4); // select 4 phrases of up to 4 elements
		ps.add(tpu);
		ps.dump();
		ps.add(tpv);
		ps.dump();
		ps.add(tpw);
		ps.dump();
		ps.add(tpx);
		ps.dump();
		ps.add(tpy);
		ps.dump();
		ps.add(tpz);
		ps.dump();
		ps.add(tpr);
		ps.dump();
		ps.add(tps);
		ps.dump();
		ps.add(tpt);
		ps.dump();
	}
	
}
