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
// TestPh.java : 30aug2022 CPM

package test;

import aw.*;
import aw.phrase.Parse;
import aw.phrase.PhraseElement;
import aw.phrase.CompoundPhraseAnalysis;
import aw.phrase.CombinedSymbolTable;
import aw.phrase.PhraseDump;
import aw.phrase.Syntax;
import object.TextItem;
import java.io.*;

// view PhraserMain (PHRASR) components on output of AnalyzerMain (ANALZR)

public class TestPh {

	private static final int N = 96; // maximum number of phrases to report for segment

	public static void main ( String[] a ) {

		int bn = (a.length > 0) ? Integer.parseInt(a[0]) : 0;
		int xn = (a.length > 1) ? Integer.parseInt(a[1]) : 0;

		PhraseElement[] phe = new PhraseElement[N];
		for (int i = 0; i < N; i++)
			phe[i] = new PhraseElement();

		try {
			CombinedSymbolTable tb = new CombinedSymbolTable();
			Syntax.initialize(tb);
			System.out.print("subsegment " + bn + "::" + xn);
			Subsegment ss = new Subsegment(bn,xn);
			int in = ss.it;
			System.out.println(" in item " + bn + ":" + in);
			TextItem ti = new TextItem(bn,in);
			String tx = ti.getBody();
			Parse pa = new Parse(bn,in);
			System.out.println(tx.length() + " chars of text altogether");
			System.out.println("subsegment at " + ss.so + ", " + ss.ln + " chars");
			System.out.println("raw pa=" + pa);
			System.out.println("--------------------------------");
			PhraseDump.showBytes(pa.analysis,System.out);
			System.out.println("--------------------------------");
			PhraseDump.show(tx,pa.analysis,tb,System.out);
			System.out.println("--------------------------------");
			CompoundPhraseAnalysis an = new CompoundPhraseAnalysis(tx,pa.analysis,ss.so,ss.ln);
			int n;
			while ((n = an.getNextPhrase(phe)) > 0) {
				int o = an.getPhraseOffset();
				int k = an.getPhraseIndex();
				System.out.println(k + " @" + o + ": " + an.toPhraseString(phe,n));
				for (int i = 0; i < n; i++) {
					int nt = phe[i].type();
					String ms = String.format("%1x",(nt & 0xF));
					System.out.print(tb.syntaxSymbol(nt) + "|" + ms);
					System.out.print(":[" + Format.hex(phe[i].modifiers()) + "] ");
					System.out.println(phe[i].word);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
