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
// AW file Ranking.java : 09Aug99 CPM
// extract keys from text and rank for selection as descriptors

package object;

import aw.*;
import stem.*;
import gram.*;
import object.*;

// queue for computation of context weight

class QueueNode {

	String ws; // word string
	short  rw; // raw weight
	short  cw; // context weight
	
}

class RankingQueue {

	private static final int L = 3;
	
	QueueNode[] queue = new QueueNode[L]; // context window
	
	int qix = 0; // start of queue

	final int qixBACK (int K) { return ((qix + L - K)%L); }
	final int qixNEXT ( ) { return ((qix + 1)%L); }

	// initialize queue array
		
	public RankingQueue (
	
	) {
		for (int i = 0; i < L; i++)
			queue[i] = new QueueNode();
	}
	
	// put into queue for context weighting
	
	public void insert (
	
		String word,
		int    wght
		
	) {
		queue[qix].ws = word;
		queue[qix].cw = queue[qix].rw = (short) wght;
		if (word.length() > 0) {
			reweight(1);
			reweight(2);
		}
	}

	// compute context weight
	
	private void reweight (
	
		int n
		
	) {
		int qkx = qixBACK(n);
		
		if (queue[qkx].ws == null)
			return;
			
		queue[qix].cw += (queue[qkx].rw >> (n + 1));
		queue[qkx].cw += (queue[qix].rw >> (n + 1));
	}

	// get next word off queue and save its weight
	
	public short wwt;
	
	public String next (
	
	) {
		qix = qixNEXT();
		String p = queue[qix].ws;
		
		wwt = queue[qix].cw;
		queue[qix].ws = null;
		queue[qix].cw = queue[qix].rw = 0;
		
		return p;
	}
	
	// clear out queue

	public void flush (
	
	) {
		qix = 0;
		for (int i = 0; i < L; i++)
			queue[i].ws = null;
	}

}

// ranking classes

class RankRecord {

	byte lsx; // index of last segment where counted
	byte frq; // segment frequency for key
	
}

class RankWord {

	String ws; // word string
	short  wt; // highest weight
	short  hx; // hash index
	
}

public class Ranking {

	private static final int  M = 8; // maximum root length
	private static final int TH = 3; // n-gram hit threshold

	private RankRecord[] wfs; // word frequency
	private HashTable htb;    // word hash table
	private int hsz;    // hash table size
	private int nht;    // entries in table

	private int count;  // how many words now ranked
	private int next;   // next ranked word to return

	private int thr;    // hit threshold
	private int smx;    // maximum to select
	
	private RankWord[] sls; // selection list
	private RankingQueue q; // for computing context weights

	private short six;  // index of text segment processed
	
	KeyTextAnalysis an; // to break text to tokens and their n-grams
	
	// derives ranking of keys within a segment
	// according to their n-gram weights and context
	
	public Ranking (
	
		int nr,  // how many to rank
		int sz,  // hash table size
		int th   // n-gram hit threshold
		
	) throws AWException {
		an = new KeyTextAnalysis();
		
		smx = nr;
		hsz = sz;
		htb = new HashTable(hsz);
		wfs = new RankRecord[hsz];
		sls = new RankWord[smx+1];

		for (int i = 0; i < hsz; i++)
			wfs[i] = new RankRecord();
			
		q = new RankingQueue();

		six = 0;
		
		thr = (th < 0) ? TH : th;
	}
	
	// process more text for candidate keys
	
	private SortedList list = new SortedList(Token.MXW+4);
	
	public int rank (
	
		String segm, // text to be processed
		byte[]   pw  // reference profile weights
		
	) throws AWException {
		AnalyzedToken token; // word token stem

		if (segm.length() == 0)
			return nht;
			
		six++;
		q.flush();
 
 		an.setText(segm);
 		
		// extract keys and weight them

		while ((token = an.next()) != null) {
 
			if (token.length() <= 1)
				rankit("",0);
			else
				rankit(token.toString(),token.score(thr,pw));
			
		}
		
		// get anything remaining in queue
		
		while (rankit(null,0));
		
		return nht;
	}

	// put word root into hash table
	
	private int record (
	
		String root // word root

	) throws AWException {		
		int nh = htb.lookUp(root);
		
		if (nh == 0)
			throw new AWException("hash overflow");

		if (nh > 0)
			--nh;
		else {
			nh = -nh - 1;
			htb.array[nh] = root;
			wfs[nh].lsx = 0;
			wfs[nh].frq = 0;
			nht++;
		}
		
		return nh;
	}

	// keep track of word frequency and count of segments

	private void tally (
	
		int nh
		
	) {
		if (wfs[nh].lsx < six) {
			wfs[nh].frq++;
			wfs[nh].lsx = (byte) six;
		}
	}

	// save key for selection
	
	private boolean rankit (
	
		String word, // word to queue
		int     wgt  // its weight

	) throws AWException {
	
		short j,k,n;
		short wwt;
		String p;

		if (word != null)
			q.insert(word,wgt);

		// anything at other end of queue to select?

		p = q.next();
		if (p == null)
			return false;
		
		wwt = q.wwt;
		if (wwt == 0)
			return true;
	
		// already in selection list?

		LimitedString ps = new LimitedString(p,M);
		for (k = 0; k < count; k++)
			if (ps.equals(sls[k].ws))
				break;

		if (k == count)

			// record word not in list
			
			n = (short) record(p);
			
		else {

			// found at position k
			
			p = sls[k].ws;
			n = sls[k].hx;

			// if lower weight, no change in selection list
			
			if (wwt <= sls[k].wt) {
				tally(n);
				return true;
			}
			
		}

		// move to proper position on list

		for (j = k; --j >= 0; ) {
			if (sls[j].wt >= wwt)
				break;
			sls[j+1] = sls[j];
		}
		
		sls[j+1] = new RankWord();
		sls[j+1].wt = wwt;
		sls[j+1].ws = p;
		sls[j+1].hx = n;

		tally(n);

		// update count for new word

		if (k == count && count < smx)
			count++;

		return true;
	}

	// -------------- methods for ranking object --------------

	// return ranked keys

	public int ww; // to go with returned key
		
	public String out (
	
	) {
		int   nwr; // number of words kept
		int   frq; // segment frequency for word
		int   i,j;
		short k,n;
		String  s;
		
		RankWord r;

		if (next == 0) {

			// resort by segment frequency
			
			for (i = nwr = 0; i < count; i++) {
				r = sls[i];
				n = r.hx;

				// keep keys from multiple segments
				// if more than one was processed
				
				frq = wfs[n].frq;
				if (frq == 1 && six > 1)
					continue;

				// select by frequency
				
				for (j = nwr - 1; j >= 0; --j) {
					k = sls[j].hx;
					if (frq <= wfs[k].frq)
						break;
					sls[j+1] = sls[j];
				}
				sls[j+1] = r;
				nwr++;
			}
			count = nwr;
		}

		if (next < count) {
			ww = sls[next].wt;
			return sls[next++].ws;
		}
		else {
			reset();
			return null;
		}
	}
	
	// clean up
	
	public void reset (
	
	) {
		q.flush();
		htb.clear();
		next = count = 0;
		six = 0;
		nht = 0;
	}
	
}

