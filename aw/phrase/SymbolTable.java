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
// SymbolTable.java : 08feb2022 CPM
// for encoding syntax symbols

package aw.phrase;

import aw.phrase.Syntax;
import aw.AWException;

public class SymbolTable {

	public static final int SymbolSize  =  3;  // maximum character count for symbol
	public static final int TableSize   =128;  // maximum number of all symbols

	// 1-byte coding of syntax categories:
	// upper 4 bits = general class
	// lower 4 bits = four possible specializations
	//
	// general classes will be compared by bitwise EXCLUSIVE OR
	// and specializations will be compared by bitwise AND with
	// analyses complemented; both must result in zero to match

	private static final int nALT = 4; // possible specializations
	
	private static final int Cv (
		int x
	) {
		return (byte)(x ^ Syntax.x0F);
	}
	
	// convert to symbol
	
	protected static int aCODING (
	
		String s
		
	) throws AWException {
		byte x = 0;
		while (s.length() > 0 && Character.isWhitespace(s.charAt(0)))
			s = s.substring(1);
		int k = s.indexOf(':');
		if (k > 0) {
			String t = s.substring(k+1);
			s = s.substring(0,k);
			for (int i = 0; i < nALT; i++) {
				char c = t.charAt(i);
				if (!Character.isDigit(c))
					break;
				x <<= 1;
				x |= (c == '1') ? 1 : 0;
			}
		}
		int n = Integer.parseInt(s);
		if (n == 0)
			System.err.println("syntax type UNKNOWN");
		if (n >= 8)
			throw new AWException("syntax type conflict with phrase markers");

		x |= (byte) (n << nALT);
		return Cv(x);
	}
	
	protected String[] symbolTable = new String[TableSize]; // syntax and feature table
	protected byte[]   symbolMap   = new byte[TableSize];   // symbol mappings
	protected int      symbolCount = 0; // how many in table
	
	private static final int nH = 8; // bits in a byte
	
	// copy symbol into the table up to SymbolSize and return its full length 

	protected int insert (
	
		String expression
		
	) {
		int k,n;
		int ll = expression.length();
		int lm = (ll < SymbolSize) ? ll : SymbolSize;
		StringBuffer b = new StringBuffer(SymbolSize);

		for (k = 0; k < lm && Character.isLetterOrDigit(expression.charAt(k)); k++)
			b.append(expression.charAt(k));
		symbolTable[symbolCount++] = b.toString().toUpperCase();
		for (; k < ll; k++)
			if (!Character.isLetterOrDigit(expression.charAt(k)))
				break;

		return k;
	}

	// returns index of table entry matching a given symbol or < 0 if not found
	//

	protected int scan (
	
		String symbol,
		int tableIndex,
		int tableLimit
		
	) {
		if (symbol.length() > SymbolSize)
			symbol = symbol.substring(0,SymbolSize);
		String sym = symbol.toUpperCase();
		for (; tableIndex < tableLimit; tableIndex++)
			if (sym.equals(symbolTable[tableIndex]))
				return tableIndex;
		return -1;
	}
	
	// to be overridden
	
	int scanForType (
	
		String typ
		
	) {
		return scan(typ,0,symbolCount);
	}

	// to be overridden
	
	int scanForFeature (
	
		String fet
		
	) {
		return scan(fet,0,symbolCount);
	}

	// look up syntactic type

	public int syntacticType (
	
		String symbolString
		
	) {
		int k = scanForType(symbolString);
		return (k < 0) ? 0 : symbolMap[k];
	}

	// look up syntactic feature

	public int modifierFeature (
	
		String symbolString
		
	) {
		int n = scanForFeature(symbolString);
		if (n < 0)
			return 0;
		else {
			int k = symbolMap[n];
			return (k >= 0 && k < nH) ? (1 << k) : 0;
		}
	}

	// look up semantic feature

	public int semanticFeature (
	
		String symbolString
		
	) {
		int n = scanForFeature(symbolString);
		if (n < 0)
			return 0;
		else {
			int k = symbolMap[n];
			return (k >= nH) ? (1 << (k - nH)) : 0;
		}
	}

	// encodes a symbol string as a syntax type plus features
	// in pattern form

	public void symbolToSyntax (
	
		String     symbolString,
		SyntaxPatt Patt
		
	) throws AWException {
		int m,n;
		int featureBase,featureStart;
		char sense; // feature sense indicator

		Patt.modifiermasks[0] = Patt.modifiermasks[1] = 0;
		Patt.semanticmasks[0] = Patt.semanticmasks[1] = 0;
		String symbol;
	
		// get bracketed syntactic features for symbol

		if ((featureBase = symbolString.indexOf('[')) < 0)
		
			symbol = symbolString;
		
		else {

			symbol = symbolString.substring(0,featureBase);
			
			featureBase++;
			while (symbolString.charAt(featureBase) != ']') {

				// feature sense (+,-) must be present

				sense = symbolString.charAt(featureBase++);
				if (sense != '+' && sense != '-')
					throw new AWException("bad syntactic feature: " + symbolString);

				// get next feature and look up

				featureStart = featureBase;
				while (Character.isLetterOrDigit(symbolString.charAt(featureBase)))
					featureBase++;
				String feature = symbolString.substring(featureStart,featureBase);

//				System.out.println("start= " + featureBase + " in " + symbolString);;
//				System.out.println(symbolCount + " symbols defined");

				n = scanForFeature(feature);
				if (n < 0)
					throw new AWException("unrecognized feature name: " + feature);

				byte[] b;
				m = symbolMap[n];
				if (m > nH) {
					b = Patt.semanticmasks;
					m -= nH;
				}
				else
					b = Patt.modifiermasks;

				b[(sense == '-') ? 1 : 0] |= (1 << m);
			}
		}

		// finally encode syntax type

		Patt.type = (byte) Cv(syntacticType(symbol));
	}

	// convert a pattern to syntax specification
	
	public void patternToSpecification (
	
		SyntaxPatt Patt,
		SyntaxSpec Spec
		
	) {
		Spec.type = (byte) Cv(Patt.type);
		Spec.modifiers = Patt.modifiermasks[SyntaxPatt.POSITIVE];
		Spec.semantics = Patt.semanticmasks[SyntaxPatt.POSITIVE];
	}

	// get a symbol for syntax code
	
	public String syntaxSymbol (
	
		int syntax
		
	) {
		int i;

		if (syntax == 0)
			return "UNK";

		for (i = 0; i < symbolCount; i++)
			if (symbolMap[i] == syntax)
				break;

		return (i == symbolCount) ? "" : symbolTable[i];
	}

}
