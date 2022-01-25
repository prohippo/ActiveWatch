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
// Phraser.java : 10Feb99 CPM
// phrase summarization for clusters

package aw.phrase;

import aw.*;
import object.*;
import java.io.*;

public class Phraser {

	private static final int MIN = 3; // minimum items per cluster to use
	private static final int MAX = 6; // maximum
	
	private static final int N =  12; // maximum number of words per phrase
	
	private Map map = new Map();
	private KeyTextAnalysis  an;
	private PhraseExtraction pe;
	
	public Phraser (
	
		int nph
		
	) throws AWException {
		an = new KeyTextAnalysis();
		pe = new PhraseExtraction(nph,N);
		try {
			PhraseSyntax.initialize();
		} catch (IOException e) {
			throw new AWException(e);
		}
	}
	
	public void run (
	
	) throws AWException {
		int nc = map.limit();
		
		GappedProfileList ls;     // profile match list
		FullProfile profr = null; // profile expansion
		byte[] pv;  // expanded vector of profile weights
		int no = 0;
		
		for (int cn = 1; cn <= nc; cn++) {
			if (!map.defined(cn) || map.userType(cn))
				continue;
				
			System.out.print("------- cluster " + cn + ":");
			no++;

			// read in cluster list

			ls = new GappedProfileList(cn);
			int nm = ls.getCount();
			if (nm == 0) {
				System.out.println("no items listed");
				continue;
			}
			Item[] its = ls.getList();
			
			// read in cluster profile and expand to full vector

			profr = new FullProfile(cn);	

			if (profr.count() == 0) {
				System.out.println("null profile");
				continue;
			}

			pv = profr.vector();
			pe.reset(pv);
			
			// extract phrases from selected items
			
			int mlm = ls.gap;
			if (mlm < MIN)
				mlm = MIN;
			if (mlm > nm)
				mlm = nm;
			if (mlm > MAX)
				mlm = MAX;
			
			its = ls.getList();
			
			for (int i = 0; i < mlm; i++) {
				System.out.print(" " + its[i].bn + "::" + its[i].xn);
				pe.add(its[i]);
			}
			System.out.println();
				
			// show phrases obtained
			
			String[] phs = pe.get();
			for (int i = 0; i < phs.length; i++)
				System.out.println(phs[i]);
 		}
 		
		System.out.println("-------");
		System.out.println(no + " clusters summarized");
	}

}