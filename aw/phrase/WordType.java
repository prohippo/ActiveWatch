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
// WordType.java : 09feb2022 CPM
// to determine syntactic type from mini-dictionary

package aw.phrase;

import aw.AWException;
import aw.ResourceInput;
import object.WordHashTable;
import java.io.*;

public class WordType {

	public static String wordFileName = "words";

	private static final int TableSize = 2053; // hash table size

	private static WordHashTable wordTable = new WordHashTable(TableSize); // for word lookup
	private static SyntaxSpec[]  wordType  = new SyntaxSpec[TableSize];    // syntax for words
	private static boolean wordsLoaded = false;
	private static int     wordCount = 0;

	// looks for word as char[] in table

	public static boolean match (

		char[] a,
		int    n,
		SyntaxSpec x

	) {
		if (!wordsLoaded)
			return false;

		int hash = wordTable.lookUp(a,n);

		if (hash <= 0)
			return false;
		else {
			x.copy(wordType[--hash]);
			return true;
		}
	}

	// loads words plus syntactic categories and returns a count

	static public int load (

		SymbolTable stb

	) throws IOException {
		String line;

		if (wordsLoaded)
			return wordCount;

		SyntaxPatt wordSyntax = new SyntaxPatt();

		for (int i = 0; i < TableSize; i++)
			wordType[i] = new SyntaxSpec();

		BufferedReader in = ResourceInput.openReader(wordFileName);

		while ((line = in.readLine()) != null) {

			line = line.trim();
			if (line.length() == 0 || line.charAt(0) == ';')
				continue;

			// back up from end of entry to first blank

			int k = line.lastIndexOf(' ');
			if (k < 0)
				continue;

			// process last part of entry as syntax specification

			String ss = line.substring(k+1);
			try {
				stb.symbolToSyntax(ss,wordSyntax);
			} catch (AWException e) {
				System.err.println("cannot interpret " + ss);
				continue;
			}

			// interpret start of entry as one or more words

			String w = line.substring(0,k).trim().toUpperCase();
			int hash = wordTable.lookUp(w);

			if (hash == 0)
				throw new IOException("hash table overflow");

			if (hash > 0)
				System.err.println("duplicate in word file: " + w);
			else {
				hash = -(++hash);
				wordTable.array[hash] = w;
				stb.patternToSpecification(wordSyntax,wordType[hash]);
				wordCount++;
			}

		}
		in.close();
		wordsLoaded = true;

		return wordCount;
	}

}
