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
// AW file Characterizer.java : 14jul2021 CPM
// encapsulated n-gram extraction

package gram;

import aw.Letter;
import aw.AWException;
import stem.Token;
import java.io.*;
import java.util.*;

public class Characterizer {

	private static final int N2gB = Gram.IB2 + Letter.NA*Letter.NAN;

	private TokenBuffer tb = new TokenBuffer(); // work area
	
	private Respelling  phonetic; // how letter combinations sound
	private Literal     table;    // user-defined indices
	
	private int index = 0;        // to get successive indices for token
	
	private ArrayList<Short> las; // list of indices obtained  for token
	
	// prime with phonetic transforms and literals table
	// (the output list is passed as argument to avoid reallocation)
	
	public Characterizer (
		Respelling trs, // for phonetic coding
		Literal    lit  // literal index table
	) {
		phonetic = trs;
		table    = lit;
	}

	// analyze a given token into list of indices
		
	public void set (
		Token token  // what to analyze
	) {
		index = 0;
		las = new ArrayList<Short>(0);
		tb.set(token);	
		
		// get trailing and first leading literal index, if any
		
		short g = table.forward(tb); // leading
		short h = table.reverse(tb); // trailing

		int  lb = -1;

		if (h > 0 && tb.rvrs == tb.start)
			g = 0;
			
		if (g > 0) {
			las.add(g);
			lb = tb.fwrd + tb.nor - 1;
		}
			
		// extract all other indices

		for (;;) {

			// get next leading literal index

			if (lb == tb.fwrd)
				if ((g = table.forward(tb)) > 0) {
					las.add(g);

					// where to look for next leading literal

					lb = tb.fwrd + tb.nor - 1; 
					continue;
				}

			// get next lexical index

			if ((g = LexicalGram.get(tb)) <= 0)
				break;
			las.add(g);
			
			// special case for next possible leading literal

			if (g > N2gB && lb < tb.fwrd)
				lb = tb.fwrd;

		}

		// delay insertion of trailing index until after last leading index

		if (h > 0)
			if (lb < tb.end)
				las.add(h);
			
		// get phonetic index
		
		phonetic.transform(tb);
		g = PhoneticGram.get(tb);
		las.add(g);
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
			Respelling rs = new Respelling(new BufferedReader(new FileReader("phonetic")));
			Literal    ls = new Literal(new DataInputStream(new FileInputStream("lits")));

			Characterizer chzr = new Characterizer(rs,ls);
			chzr.set(new Token(s));

			short g;
			while ((g = chzr.get()) > 0) {
				System.out.println(g);
			}

		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		} catch (AWException e) {
			System.err.println(e);
			System.exit(2);
		}

	}
	
}
