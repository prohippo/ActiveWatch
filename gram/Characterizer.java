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
// AW file Characterizer.java : 30aug2021 CPM
// encapsulated n-gram extraction

package gram;

import aw.Letter;
import aw.AWException;
import stem.Token;
import gram.LexicalGram;
import java.io.*;
import java.util.*;

public class Characterizer {

	private static final int N2gB = Gram.IB2 + Letter.NA*Letter.NAN;

	private TokenBuffer tb = new TokenBuffer(LexicalGram.MXn); // work area

	private Literal table;        // user-defined indices
	private GramMap   map;        // pre-defined long lexical n-grams

	private int index = 0;        // to get successive indices for token

	private ArrayList<Short> las; // list of indices obtained  for token

	// create an extractor with externally defined literals table

	public Characterizer (
		Literal lit,  // literal index table
		GramMap gm    // longer n-grams
	) {
		table = lit;
		map = gm;
	}

	// give extractor some text to analyze into list of indices

	public void set (
		Token token  // a bit of text to index
	) {
		index = 0;
		las = new ArrayList<Short>(0);
//		System.out.println(tb);
		tb.set(token);
//		System.out.println(tb);

		// get a trailing and a leading literal index, if any

		short g = table.forward(tb); // leading
//		System.out.println(tb);
		short h = table.reverse(tb); // trailing
//		System.out.println(tb);

//		System.out.println("g=" + g + ", h=" + h);

		if (g > 0)
			las.add(g);
		if (tb.exhausted())
			h = 0;  // forward literal extraction supersedes

		// extract all other lexical indices

//		System.out.println(tb);
		while ((g = LexicalGram.get(tb,map)) > 0)
			las.add(g);

		// delay insertion of trailing index until after last leading index

		if (h > 0)
			las.add(h);

	}

	// get successive n-gram occurrences for token

	public final Short get ( ) {
		if (index < las.size())
			return las.get(index++);
		else
			return -1;
	}

	// get n-gram count for token

	public final int count ( ) { return las.size(); }

	// get all n-gram indices for token

	public final Short[] list ( ) {
		return las.toArray(new Short[las.size()]);
	}

	// unit test

	public static final void main ( String[] as ) {

		String s = (as.length > 0) ? as[0] : "clapdoodle";

		try {
			GramStart gs = new GramStart();
			LiteralDecoding ld = new LiteralDecoding(gs.table);
			GramDecode gd = new GramDecode(gs.map,ld);

			Characterizer chzr = new Characterizer(gs.table,gs.map);
			chzr.set(new Token(s));

			short g;
			while ((g = chzr.get()) > 0)
				System.out.println(String.format("%5d : %s",g,gd.toString(g)));

		} catch (AWException e) {
			System.err.println(e);
			System.exit(1);
		}

	}

}
