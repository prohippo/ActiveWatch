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
// CombinedSymbolTable.java : 04sep2022 CPM
// for defining syntactic types and features

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
				modfetCount++;
				byte b = (byte)(1 << x);
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
		int n = scan(symbol,0,syntypCount);
//		System.out.println("n= " + n);
		return (n < 0) ? (byte)(-1) : symbolCode[n]; 
	}

	public byte modifierFeature (
		String symbol
	) {
//		System.out.println("symbol= " + symbol + ", range= " + syntypCount + ", " + fullCount);
		int n = scan(symbol,syntypCount,fullCount);
//		System.out.println("n= " + n + ", code= " + String.format("%02x",symbolCode[n]));
		return (n < 0) ? (byte)(-1) : symbolCode[n]; 
	}

	public byte semanticFeature (
		String symbol
	) {
		int n = scan(symbol,syntypCount,fullCount);
		return (n < 0) ? (byte)(-1) : (byte)(1 << (symbolCode[n] - 8)); 
	}


	// encodes a symbol string as a syntax type plus features
	// in pattern form

	public void symbolToSyntax (
	
		String     symbolString,
		SyntaxPatt Patt
		
	) throws AWException {
		int m,n;
		int featureString,featureStart;
		char sense; // feature sense indicator

		Patt.modifiermasks[0] = Patt.modifiermasks[1] = 0;
		Patt.semanticmasks[0] = Patt.semanticmasks[1] = 0;
		String symbol;
	
		// get bracketed syntactic features for symbol

		if ((featureString = symbolString.indexOf('[')) < 0)
		
			symbol = symbolString;
		
		else {

			symbol = symbolString.substring(0,featureString);
			
			featureString++;
			while (symbolString.charAt(featureString) != ']') {

				// feature sense (+,-) must be present

				sense = symbolString.charAt(featureString++);
				if (sense != '+' && sense != '-')
					throw new AWException("bad syntactic feature: " + symbolString);

				// get next feature and look up

				featureStart = featureString;
				while (Character.isLetterOrDigit(symbolString.charAt(featureString)))
					featureString++;

				String feature = symbolString.substring(featureStart,featureString);
				n = modifierFeature(feature);
				if (n < 0)
					throw new AWException("unrecognized feature name: " + feature);

				byte[] b;
				m = symbolCode[n];
				if (m > NF) {
					b = Patt.semanticmasks;
					m -= NF;
				}
				else
					b = Patt.modifiermasks;

				b[(sense == '-') ? 1 : 0] |= (1 << m);
			}
		}

		// finally encode syntax type

		Patt.type = (byte) Cv(syntacticType(symbol));
	}

	/////// for debugging
	///////

	public void dump ( ) {
		System.out.println(toString());
		super.dump();
	}

	public byte scan ( String symbol ) {
		int n = scan(symbol,0,symbolCount);
		if (n < 0)
			return (byte)(-1);
		else
			return symbolCode[n];
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
