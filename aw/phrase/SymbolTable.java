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
// SymbolTable.java : 03sep2022 CPM
// for encoding syntax symbols for phrase extraction rules

package aw.phrase;

import aw.Format;
import aw.phrase.Syntax;
import aw.AWException;

public class SymbolTable {

	public static final int SymbolSize  =  3;  // maximum character count for symbol
	public static final int TableSize   =128;  // maximum number of all symbols

	// 1-byte coding of syntax categories:
	// upper 4 bits = general class (16 possible)
	// lower 4 bits = four possible specializations, more than 1 allowed
	//
	// for example, a verb is a 0100 specialization of general class 1
	//              a noun is a 1000 specialization of the same class
	// a word that can be a noun or a verb has a specialization of 1100
	//
	// general classes will be compared by bitwise EXCLUSIVE OR
	// and specializations will be compared by bitwise AND with
	// one set of bits complemented; both bitwise operations must
	// result in zero for two type codes to match

	protected static final byte Cv (
		int x
	) {
		return (byte)(x & Syntax.x0F);
	}

	// get coding for complex syntax type

	private static final int nSPC = 4; // possible specializations of general type

	protected static byte typeCODING (

		String s

	) throws AWException {
		byte x = 0;

		s = s.trim();
		int k = s.indexOf(':');
//		System.out.println("s= " + s + ", k= " + k);
		if (k > 0) {
			String t = s.substring(k+1);
			s = s.substring(0,k);
//			System.out.println("s= " + s + ", t= " + t);
			for (int i = 0; i < nSPC; i++) {
				char c = t.charAt(i);
				if (!Character.isDigit(c))
					break;
				x <<= 1;
				x |= (c == '1') ? 1 : 0;
			}
//			System.out.println("x= " + x);
		}
		int n = Integer.parseInt(s);
		if (n == 0)
			System.err.println("syntax type UNKNOWN");
		if (n >= 8)
			throw new AWException("syntax type conflict with phrase markers");
 
//		System.out.println("n= " + n);
		x |= (byte)(n << nSPC);
//		System.out.println("full x= " + x);
//		System.out.println("converted= " + (byte)(x));
		return (byte)(x);
	}

	protected String[] symbolTable = new String[TableSize];  // types and features table
	protected byte[]   symbolCode  = new byte[TableSize];    // associated code bytes
	protected int      symbolCount = 0; // how many in table

	public void dump ( ) {
		for (int i = 0; i < symbolCount; i++) {
			System.out.print(Format.it(i,2,' ') + ": ");
			System.out.println(symbolTable[i] + " = " + interp(symbolCode[i]));
		}
		System.out.println("----");
	}

	// copy symbol into the table up to SymbolSize and return its full length 

	protected int insert (

		String expression

	) {
		int k,n;
		int ll = expression.length();
		int lm = (ll < SymbolSize) ? ll : SymbolSize;
		StringBuffer sb = new StringBuffer(SymbolSize);

//		System.out.println("expression= " + expression);
		for (k = 0; k < lm && Character.isLetterOrDigit(expression.charAt(k)); k++)
			sb.append(expression.charAt(k));
//		System.out.println("sb= " + sb);
		symbolTable[symbolCount++] = sb.toString().toUpperCase();
		for (; k < ll; k++)
			if (!Character.isLetterOrDigit(expression.charAt(k)))
				break;

//		System.out.println("k= " + k);
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
//		System.out.println("scan for " + sym + " from " + tableIndex + " to " + tableLimit);
		for (int it = tableIndex; it < tableLimit; it++) {
			if (sym.equals(symbolTable[it])) {
//				System.out.println(">>> found at " + it);
				return it;
			}
		}
		return -1;
	}

	// encodes a symbol string as a syntax type plus features
	// in syntactic pattern form

	public void parseSyntax (

		String     syntaxString,
		SyntaxPatt Patt

	) throws AWException {
		int m;
		int featureBase,featureStart;
		char sense; // feature sense indicator

		Patt.modifiermasks[0] = Patt.modifiermasks[1] = 0;
		Patt.semanticmasks[0] = Patt.semanticmasks[1] = 0;
		String symbol;

		// get bracketed syntactic features for symbol

		if ((featureBase = syntaxString.indexOf('[')) < 0)

			symbol = syntaxString;

		else {

			symbol = syntaxString.substring(0,featureBase);

			featureBase++;
			while (syntaxString.charAt(featureBase) != ']') {

				// feature sense (+,-) must be present

				sense = syntaxString.charAt(featureBase++);
				if (sense != '+' && sense != '-')
					throw new AWException("bad syntactic feature: " + syntaxString);

				// get next feature and look up

				featureStart = featureBase;
				while (Character.isLetterOrDigit(syntaxString.charAt(featureBase)))
					featureBase++;
				String feature = syntaxString.substring(featureStart,featureBase);
//				System.out.println("feature= " + feature);

//				System.out.println("start= " + featureBase + " in " + syntaxString);;
//				System.out.println(symbolCount + " symbols defined");

				byte nb = modifierFeature(feature);
//				System.out.println("code= " + nb);
				if (nb < 0)
					throw new AWException("unrecognized feature name: " + feature);

				byte[] b = Patt.modifiermasks;

				b[(sense == '-') ? 1 : 0] |= nb;
			}
		}

		// finally encode syntax type

//		System.out.println("getting type for " + symbol);
		Patt.type = (byte)(syntacticType(symbol));
	}

	// qualifiers for syntax byte, encoded in 4 bits

	private static final String[] bits = {
		"0 0 0 0" , "0 0 0 1" , "0 0 1 0", "0 0 1 1",
		"0 1 0 0" , "0 1 0 1" , "0 1 1 0", "0 1 1 1",
		"1 0 0 0" , "1 0 0 1" , "1 0 1 0", "1 0 1 1",
		"1 1 0 0" , "1 1 0 1" , "1 1 1 0", "1 1 1 1"
	};

	// interpret symbol table byte entry as syntax or as feature bit

	public static final String interp ( byte b ) {
//		System.out.println("b= " + b);
		if (b == 0) return " 0 : 0 0 0 0";  // must be syntax
		if (b <  0) return "????";          // error
		int cls = b >> nSPC; // main syntactic category
		int spc = b  & 0x0F; // specializations
//		System.out.println("cls= " + cls + ", spc= " + spc);
		if (cls > 0)
			return Format.it(cls,2,' ') + " : " + bits[spc];
		else if (spc <= 8)
			return "(1 <<" + (spc-1) + ") syn";
		else
			return "(1 <<" + (spc-9) + ") sem";
	}

	//////// stub methods to get codes for a symbol string (all need to be overridden)
	//

	public byte syntacticType (
		String symbol
	) {
		return (byte)(-1);
	}

	public byte modifierFeature (
		String symbol
	) {
		return (byte)(-1);
	}

	public byte semanticFeature (
		String symbol
	) {
		return (byte)(-1);
	}

	////////

	// get a symbol string for code

	public String syntaxSymbol (

		int syntxn

	) {
		String s;

		if (syntxn < 0)
			return "???";

		int code = (syntxn >> 4); 

		if (syntxn == 0)
			s = "UNK";
		else if (code == 1)
			s = "XXX";
		else if (code < 8)
			s = String.format("%03d",code);
		else
			s = "!!!";

		return s;
	}

}
