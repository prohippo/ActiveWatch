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
// Analyzer.java : 24feb2022 CPM
// phrase analysis update module

package aw.phrase;

import aw.*;
import object.TextItem;
import java.io.*;

public class Analyzer extends AnalyzerBase {

	private int batch; // which to analyze
	private int count; // how many items

	private static final int W = 80; // line width

	// data base initializations

	public Analyzer (

		int nb, // byte limit on output for single item
		int wl  // word limit for single phrase

	) throws AWException {
		super(nb,wl);
		Control c = new Control();
		batch = c.cubn;
		count = Index.count(batch);
		if (count == 0) {
			batch = c.previous(batch);
			count = Index.count(batch);
		}
	}

	// process every item in last batch

	public void run (

	) throws AWException {
		Parse pp = null;
		System.out.println("batch count= " + count);
		for (int n = 0; n < count; n++) {
			System.out.println(batch + ":" + n);
			TextItem it = new TextItem(batch,n);
			String ts = it.getBody();
			System.out.println(ts.length() + " chars");
			LinedText lt = new LinedText(ts,W);
			Parsing ps = analyze(lt);
			System.out.println("  parsing= " + ps);
			reparse(ts,ps);
			System.out.println("reparsing= " + ps);
			try {
				pp = new Parse(ps);
				pp.save(batch);
			} catch (IOException e) {
				throw new AWException(e);
			}
		}
		if (pp != null)
			pp.close();
	}

}
