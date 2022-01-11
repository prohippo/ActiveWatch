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
// AW file ItemTally.java : 01Jun00 CPM
// represent a subset of items in a boolean vector

package object;

import aw.AWException;
import aw.Control;
import aw.Item;
import object.IndexedItem;
import object.AccessionNumber;

public class ItemTally implements ItemMembership {

	private boolean[] tally; // marking of subset
	private int count; // nonzero tallies
	private int limit; // highest tally
	private int base;  // base number for tally index
	
	private AccessionNumber an = new AccessionNumber(0);

	// initialize
	
	public ItemTally (
	
	) {
		Control c = new Control();
		int n = c.totalCount();
		tally = new boolean[n];
		count =  0;
		limit = -1;
		base = c.ndel;
	}
	
	
	// tally a single item
	
	public final void mark (
		Item it
	) throws AWException {
		mark(index(it));
	}
	
	public final void mark (
		int m
	) {
		int k = m - base;
		if (!tally[k]) {
			if (limit < k)
				limit = k;
			tally[k] = true;
			count++;
		}
	}	

	// test a tally
	
	public boolean find (
		int m
	) {
		return tally[m - base];
	}
	
	// convert item reference to tally index
	
	private int index (
		Item it
	) throws AWException {
		IndexedItem item = new IndexedItem(it);
		an.set(item.bn,item.index);
		return an.value();
	}
	
	private IndexedItem[] subset;
	
	// get subset of tallied items
	
	public final IndexedItem[] subset (
	
	) throws AWException {
		fill(count,true);
		return subset;
	}
	
	// get subset of untallied items
	
	public final IndexedItem[] complement (
	
	) throws AWException {
		fill(tally.length - count,false);
		int k = limit + 1 - count;
		for (int i = limit + 1; i < tally.length; i++) {
			an.set(i + base);
			subset[k++] = new IndexedItem(an,0);
		}
		return subset;
	}
	
	// recover a subset up to the tally limit
	
	private void fill (
		int n,
		boolean sense
	) throws AWException {
		AccessionNumber an = new AccessionNumber(0);
		subset = new IndexedItem[n];
		int k = 0;
		for (int i = 0; i <= limit; i++)
			if (tally[i] == sense) {
				an.set(i + base);
				subset[k++] = new IndexedItem(an,0);
			}
	}
	
	// total number of items tallied
	
	public final int uniqueCount (
	
	) {
		return count;
	}
	
	// zero out tallies
	
	public final void clear (
	
	) {
		if (count > 0)
			tally = new boolean[tally.length];
		count = limit = 0;
	}
	
}