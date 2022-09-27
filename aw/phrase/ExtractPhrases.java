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
// ExtractPhrases.java : 22sep2022 CPM
// dump all phrases for batch

package aw.phrase;

import aw.AWException;
import aw.Format;
import aw.Item;
import aw.Reference;
import aw.phrase.Parse;
import object.FullProfile;
import object.IndexedItem;
import object.TextItem;
import java.io.*;

public class ExtractPhrases {

	private static final int LN = 32;
	private static final int NW = 12;
	
	public static void main ( String[] a ) {
		int pn = (a.length > 1) ? Integer.parseInt(a[1]) : 1;
		System.out.println("top phrases for saved profile " + pn);

		IndexedItem itm;
		byte[] pv;
		PhraseExtractor pe;
		
		try {
			FullProfile fp = new FullProfile(pn);
			pv = fp.vector();
			Item it;
			if (a.length == 0)
				it = new Item(0,0,0);
			else
				it = Reference.to(a[0]);
			itm = new IndexedItem(it);
			pe = new PhraseExtractor(NW);
		} catch (AWException e) {
			e.printStackTrace();
			return;
		}
		int n = itm.index;
		int b = itm.bn;
		System.out.println("extract phrases for " + b + ":" + n);
		
		try {
			TextItem ti = new TextItem(b,n);
			String tx = ti.getBody();
			int ln = tx.length();
			int ll = (ln < LN) ? ln : LN;
			System.out.println("=" + tx.substring(0,ll) + " : " + ln + " chars");
			Parse ps = new Parse(b,n);
			Parsing pp = ps.analysis;
			pe.reset(tx,pp);
			pe.reset(pv);
			TaggedPhrase ph;
			while ((ph = pe.next()) != null)
				System.out.println("(" + Format.it(ph.score,4) + ") " + ph.phrase);
			System.out.println("DONE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
