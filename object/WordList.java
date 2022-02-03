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
// AW file WordList.java : 24jan2022 CPM
// general utility class

package object;

import aw.*;
import java.util.Enumeration;

// list element

class WordListEntry implements SortableObject {

	public String word;
	public int     tag;
	
	// required by SortableObject interface
	
	public int compareTo ( SortableObject o ) {
		if (o instanceof WordListEntry)
			return word.compareTo(((WordListEntry) o).word);
		else {
			CharArray a = (CharArray) o;
			int k = word.length();
			for (int i = 0; i < k; i++) {
				char x = a.charAt(i);
				if (word.charAt(i) < x)
					return -1;
				if (word.charAt(i) > x)
					return  1;
			}
			return (k == a.length()) ? 0 : -1;
		}
	}
	
}

// for tagged lists of strings with lookup

public class WordList {

	private static final int N =   16; // default initial list length
	
	private BinarySearching bs = null; // for word lookup
	
	private WordListEntry[] list;
	
	private int count = 0;
	
	// access count field
	
	public final int getCount ( ) { return count; }

	// standard constructor
	
	public WordList (
		String[] ss,
		int       t
	) {
		int n = (ss.length > 0) ? 2*ss.length : N;
		list = new WordListEntry[n];
		for (int i = 0; i < ss.length; i++)
			add(ss[i],t);
	}
	
	// alternate constructor
		
	public WordList (
		Enumeration en,
		int          t
	) {
		this();
		while (en.hasMoreElements()) {
			String w = (String) en.nextElement();
			add(w,t);
		}
	}
	
	// empty constructor
	
	public WordList (
	
	) {
		list = new WordListEntry[N];
	}
	
	// put word into list
	
	public void add (
		String w,
		int    t
	) {
		int m = find(w);
		if (m < 0)
			insert(w,t,-m-1);
		else
			list[m].tag = t;
	}
	
	// lookup method
	
	private static WordListEntry we = new WordListEntry();
	
	public int find (
		String w
	) {
		if (bs == null)
			bs = new BinarySearching(list,count);
		we.word = w;
		return bs.find(we);
	}
	
	// lookup method
	
	public int find (
		CharArray wa
	) {
		if (bs == null)
			bs = new BinarySearching(list,count);
		return bs.find(wa);
	}
	
	// how to add to list
	
	protected void insert (
		String w,
		int    t,
		int    n
	) {
		bs = null;
		
		if (count == list.length) {
			WordListEntry[] tl = new WordListEntry[2*count];
			System.arraycopy(list,0,tl,0,n);
			System.arraycopy(list,n,tl,n+1,count-n);
			list = tl;
		}
		else if (n < count)
			System.arraycopy(list,n,list,n+1,count-n);
			
		WordListEntry x = new WordListEntry();
		list[n] = x;
		x.word = w;
		x.tag = t;
		count++;
	}
	
	// drop entries with specified tag
	
	public final int drop (
		int t
	) {
		int k = 0;
		for (int i = 0; i < count; i++)
			if (list[i].tag != t)
				list[k++] = list[i];
		int n = count - k;
		count = k;
		return n;
	}
	
	// get tag for list entry
	
	public final int tag (
		int n
	) {
		if (n < 0 || n >= count)
			return -1;
		else
			return list[n].tag;
	}
	
	// return words
	
	public final String[] getAll (
	
	) {
		String[] ws = new String[count];
		for (int i = 0; i < count; i++)
			ws[i] = list[i].word;
		return ws;
	}

}
