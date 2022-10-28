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
// CombinedSymbolTable.java : 27oct2022 CPM
// for defining AW syntactic types and features

// Types and features are in a single lookup table since they are so few.
// This requires, however, that type and feature names all be different.
// Their definitions are encoded into a single byte with distinguishable
// 2-nibble formats. In hexadecimal representation, these are
//  0X00 = unknown syntactic type
//  0X0k = syntactic or semantic feature, where hexadecimal digit k > 0
//  0Xmn = syntactic main type, where 0 < m < 8 and 0 <= n <= F in hex.
//  0XFF = error (= -1 byte)
// Feature definitions have to be decoded into 1 of 15 actual bit masks
// for a byte representation of a feature.
//  1 = 00000001
//  2 = 00000010
//  ...
//  D = 00100000
//  E = 01000000
// Note: No F! Up to 8 syntactic features can be defined; only 7 semantic.

package aw.phrase;

import aw.AWException;
import aw.ResourceInput;
import java.io.*;

public class CombinedSymbolTable extends SymbolTable {

	private static final int BufferSize  = 80;  // input line buffer size

	private static final String SymbolFileName = "symbols";

	private int syntypCount;  // number of syntactic types
	private int modfetCount;  // syntactic and semantic features
	private int fullCount=0;  // total symbol count

	public CombinedSymbolTable (

	) throws IOException {
		super();
		loadSymbols(ResourceInput.openReader(SymbolFileName));
	}

	public CombinedSymbolTable (

		BufferedReader reader

	) throws IOException {
		super();
		loadSymbols(reader);
		Syntax.initialize(this);
	}

	private static final char DEL = 255;
	private static final char COM = ';';
	private static final int NF = 8;

	// read a line

	private String gets (

		BufferedReader in

	) throws IOException {
		String s = in.readLine();
		if (s != null) {
			s = s.trim();
			if (s.length() > 0) {
				char c = s.charAt(0);
				if (c == COM || c == DEL)
					s = "";
			}
		}
		return s;
	}

	// loads syntax type and feature modifier symbols into the global
	// symbolTable from the "symbols" file and sets symbolsLoaded flag

	private void loadSymbols (

		BufferedReader reader

	) throws IOException {
		String inBuff;
		int n;

		// read in all symbol definitions

		try {

			// read in syntactic types up to break line

			while ((inBuff = gets(reader)) != null) {
				if (inBuff.length() == 0)
					continue;

				if (inBuff.charAt(0) == '.')
					break;

				if (fullCount >= TableSize)
					throw new IOException("symbol overflow at " + inBuff);

				n = insert(inBuff);
//				System.out.println("inBuff= " + inBuff);
//				System.out.println("n= " + n);
				symbolCode[fullCount++] = typeCODING(inBuff.substring(n));
				syntypCount++;
//				dump();
			}


			// read and store feature definitions to end of input

			while ((inBuff = gets(reader)) != null) {
				if (inBuff.length() == 0)
					continue;

				if (inBuff.charAt(0) == '.')
					continue;

				if (fullCount >= TableSize)
					throw new IOException("feature overflow at " + inBuff);

				n = insert(inBuff);
//				System.out.println("> " + inBuff);

				// fill in feature value

				inBuff = inBuff.substring(n);
				int m = inBuff.indexOf('=');
				int x = (m < 0) ? 0 : Integer.parseInt(inBuff.substring(m + 1));
				if (x < 15)
					modfetCount++;
				byte b = (byte)(x + 1);
				symbolCode[fullCount++] = b;
//				System.out.println("count= " + modfetCount);
//				System.out.println("code = " + String.format("%02x",b));
			}

		} catch (AWException e) {
			throw new IOException("cannot interpret symbol definitions");
		}

		reader.close();
	}

	// for print diagnostics

	public String toString ( ) {
		return
			"  typs= " + syntypCount +
			", modf= " + modfetCount +
			", full= " + fullCount + ": ";
	}

	////// to override SymbolTable stubs

	public byte syntacticType (
		String symbol
	) {
		byte n = super.scan(symbol,0,syntypCount);
//		System.out.println("n= " + n);
		return n; 
	}

	public byte modifierFeature (
		String symbol
	) {
//		System.out.println("symbol= " + symbol + ", range= " + syntypCount + ", " + fullCount);
		int n = scan(symbol,syntypCount,fullCount);
//		System.out.println("n= " + n);
		if (n < 0)
			return (byte)(-1);
		byte m = (byte)(1 << (n - 1));
//		System.out.println("code= " + n + ", bit mask=" + String.format("%02x",m));
		return (byte) m; 
	}

	public byte semanticFeature (
		String symbol
	) {
		int n = scan(symbol,syntypCount,fullCount);
		return (n < 0) ? (byte)(-1) : (byte)(1 << (symbolCode[n] - 8)); 
	}

	public static final byte WILD = (byte) 0xF0;

	// encodes a symbol string as a syntax type plus features
	// as syntactic pattern

	public void parseSyntax (

		String     syntaxString,
		SyntaxPatt Patt

	) throws AWException {
		int featureBase,featureStart;
		char sense; // feature sense indicator

		Patt.modifiermasks[0] = Patt.modifiermasks[1] = 0;
		Patt.semanticmasks[0] = Patt.semanticmasks[1] = 0;
		String symbol;

		// get bracketed syntactic features for symbol

		if ((featureBase = syntaxString.indexOf('[')) < 0)

			symbol = syntaxString;

		else {   // encode syntactic features

			symbol = syntaxString.substring(0,featureBase);

			int ssl = syntaxString.length();
			if (featureBase < ssl)
				featureBase = parseFeatures(syntaxString,featureBase,true ,Patt);
			featureBase++;
			if (featureBase < ssl) 
				featureBase = parseFeatures(syntaxString,featureBase,false,Patt);
		}

		// finally encode syntax type

//		System.out.println("getting type for " + symbol);
		Patt.type = (symbol.equals("?")) ? WILD : (byte)(syntacticType(symbol));
	}

	// interpret bracketed modifier or semantic feature specifications

	private int parseFeatures (

		String  syntaxString, // syntax+semantic definition string
		int     featureBase,  // starting bracket in string
		boolean    syn,       // modifier or semantic
		SyntaxPatt patt       // where to put results

	) throws AWException {
		char sense;  // for feature checking

		if (syntaxString.charAt(featureBase) != '[')
			return featureBase; // no bracketing
		featureBase++;              // skip left bracket for first feature

		while (syntaxString.charAt(featureBase) != ']') {

			// feature sense (+,-) must be explicit

			sense = syntaxString.charAt(featureBase++);
			if (sense != '+' && sense != '-')
				throw new AWException("bad syntactic feature: " + syntaxString);

			// get next feature and look it up

			int featureStart = featureBase;
			while (Character.isLetterOrDigit(syntaxString.charAt(featureBase)))
				featureBase++;
			String feature = syntaxString.substring(featureStart,featureBase);
//			System.out.println("feature= " + feature);

//			System.out.println("start= " + featureBase + " in " + syntaxString);
//			System.out.println(symbolCount + " symbols defined");

			byte nb  = (syn) ? modifierFeature(feature) : semanticFeature(feature);

//			System.out.println("code= " + nb);
			if (nb == -1)  // cannot just test < 0!
				throw new AWException("unrecognized feature name: " + feature);

			byte[] b = (syn) ? patt.modifiermasks : patt.semanticmasks;

			b[(sense == '-') ? 1 : 0] |= nb; // add feature test to syntax pattern
		}

		return featureBase;
	}

	// type or feature symbol lookup

	public byte scan ( String symbol ) {
		byte bn = super.scan(symbol,0,symbolCount);
//		System.out.print(String.format("bn= %02x",bn));
		return bn;
	}

	/////// for debugging
	///////

	public void dump ( ) {
		System.out.println(toString());
		super.dump();
	}

	public static void main ( String[] a ) {
		try {
			String[] ax = { "noun" };
			if (a.length > 0)
				ax = a;
			CombinedSymbolTable cst = new CombinedSymbolTable();
			cst.dump();
			System.out.println("====");
			if (a.length == 0) a = ax;
			for (int j = 0; j < a.length; j++) {
				String aj = a[j];
				byte b = cst.scan(aj);
//				System.out.print(aj + " | code=" + b + " = ");
				System.out.println(SymbolTable.interp(b));
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
