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
// WordType.java : 08sep2022 CPM
// to determine syntactic type from mini-dictionary

package aw.phrase;

import aw.Format;
import aw.AWException;
import aw.ResourceInput;
import aw.phrase.Syntax;
import object.HashTable;
import java.io.*;

public class WordType {

	public static String wordFileName = "words";

	private static final int TableSize = 4001; // hash table size

	private static HashTable    wordTable = new HashTable(TableSize);  // for word lookup
	private static SyntaxSpec[] wordType  = new SyntaxSpec[TableSize]; // syntax for words
	private static boolean wordsLoaded = false;
	private static int     wordCount   = 0;

	// looks for word as char[] in table

	public static boolean match (

		char[] a,
		SyntaxSpec x

	) {
		if (!wordsLoaded)
			return false;

		int hash = wordTable.lookUp(a);
//		System.out.println(new String(a) + " -> " + hash);
		if (hash <= 0)
			return false;
		else {
//			System.out.println(wordType[hash-1]);
			x.copy(wordType[hash-1]);
			return true;
		}
	}

	// loads words plus syntactic categories and returns a count

	static public int load (

		SymbolTable stb

	) throws IOException {
		String     line;       // for word definition
		SyntaxPatt wordSyntax; // encode its syntax

		if (wordsLoaded)
			return wordCount;

//		System.out.println("symbols: " + stb);
		System.out.println("loading words");

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

			String ss = line.substring(k+1);;
//			System.out.println("ss= " + ss);
			try {
				wordSyntax = new SyntaxPatt();
				stb.parseSyntax(ss,wordSyntax);
			} catch (AWException e) {
				System.err.println(e);
				System.err.println("cannot interpret " + ss + "i: " + ss);
				continue;
			}
//			System.out.println("to " + wordSyntax);

			// interpret start of entry as one or more words

			String w = line.substring(0,k).trim().toUpperCase();
			int hash = wordTable.lookUp(w);

			if (hash == 0)
				throw new IOException("hash table overflow");

			if (hash > 0) {
				System.err.println("duplicate in word file: " + w);
			}
			else {
				hash = -(++hash);
//				System.out.println("hash= " + hash);
				wordTable.array[hash] = w;
				wordType[hash] = new SyntaxSpec();
				Syntax.patternToSpecification(wordSyntax,wordType[hash]);
//				System.out.println("= " + wordType[hash]);
				wordCount++;
			}

		}
		in.close();
		wordsLoaded = true;

		return wordCount;
	}

	// show hash keys and word definitions

	public static void dumpKeys ( ) {
		for (int i = 0; i < TableSize; i++) 
			if (wordTable.array[i] != null) {
				System.out.print(i + ": " + wordTable.array[i] + " = ");
				System.out.println(wordType[i]);
			}
		System.out.println("--------");
	}

//
////	for debugging

	private static void test ( String x ) {
		int nh = wordTable.lookUp(x);
		System.out.print("[[" + x + "]] -> ");
		if (nh > 0) {
			SyntaxSpec ss = wordType[--nh];
			System.out.print("found @" + nh + " = ");
			if (ss == null) {
				System.out.println("no definition");	
			}
			else {
				System.out.println(ss);
			}
		}
		else {
			System.out.println("NOT found");
		}
	}

	public static void main ( String[] a ) {
		int n;
		try {
			System.out.println("testing Word definitions");
			CombinedSymbolTable stb = new CombinedSymbolTable();
			stb.dump();
			WordType.load(stb);
			System.out.println("word count= " + wordCount);

			String[] ax = {
				"the" , "AND" , "to" , "THAT" , "after" , "Then" , "xxxx",
				"russia" , "captain" , "secretary" , "had" , "man's"
			};

			if (a.length == 0) a = ax;
			System.out.println();
			for (int i = 0; i < a.length; i++)
				test(a[i]);
			System.out.println();
//			dumpKeys();
		} catch ( Exception e ) {
			System.err.println(e);
		}
	}
}
