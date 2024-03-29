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
// PhraseExtraction.java : 25sep2022 CPM
// get selected scored phrases from specified items for profile

package aw.phrase;

import aw.*;
import object.*;
import java.io.*;

public class PhraseExtraction {

	PhraseExtractor ex; // get all analyzed phrases from item text
	PhraseSelector  ls; // select highest scoring unique phrases

	byte[] pv; // n-gram weights for reference profile

	// set up for number and size of phrases

	public PhraseExtraction (
		int nph, // how manyl phrases to select
		int nwd  // maximum phrase element count
	) throws AWException {
		ex = new PhraseExtractor(nwd);
		ls = new PhraseSelector(nph,nwd);
	}

	// set selection weights

	public void reset (
		byte[] pv
	) {
		ls.reset();
		this.pv = pv;
		ex.reset(pv);
	}

	// return all selected phrases

	public String[] get (
	) {
		int n = ls.count;
		String[] a = new String[n];
		for (int i = 0; i < n; i++)
			a[i] = ls.array[i].phrase;
		return a;
	}

	// process another item for phrases to select

	public void add (
		Item it  // where to get phrases
	) throws AWException {
		IndexedItem itx = new IndexedItem(it);
		TextItem ti = new TextItem(itx.bn,itx.index);
		String tx = ti.getBody();
//		System.out.println(tx.substring(0,60));

		Parse ps;
		try {
			ps = new Parse(itx.bn,itx.index);
//			System.out.println(ps);
			ex.reset(tx,ps.analysis);
		} catch (IOException e) {
			throw new AWException(e);
		}

		TaggedPhrase ph;

		while ((ph = ex.next()) != null) {
			if (ph.score > 0) {
//				System.out.println(">> ph= [" + ph + "]");
				ls.add(ph);
			}
		}
//		ex.hashdump();
	}

}
