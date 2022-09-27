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
// Syntax.java : 04sep2022 CPM
// define named syntactic categories for phrase analysis

package aw.phrase;

import java.io.*;

// for constituents

class SyntaxSpec {

	public static final int size = 3;

	byte type;      // part of speech
	byte modifiers; // more lexical info
	byte semantics; // semantic features

	void copy (

		SyntaxSpec x

	) {
		type = x.type;
		modifiers = x.modifiers;
		semantics = x.semantics;
	}

	void read (

		DataInput in

	) throws IOException {
		type = in.readByte();
		modifiers = in.readByte();
		semantics = in.readByte();
	}

	void write (

		DataOutput out

	) throws IOException {
		out.writeByte(type);
		out.writeByte(modifiers);
		out.writeByte(semantics);
	}

	public void clear ( ) {
		type = 0;
		modifiers = 0;
		semantics = 0;
	}

	public String toString ( ) {
		String features = String.format("[%02x][%02x]",modifiers,semantics);
		return Syntax.tb.interp(type) + ": " + features;
	}

	public int kind ( ) {
		return (type >> 4); // general syntactic class
	}

}

// for rules

class SyntaxPatt {

	public static final int POSITIVE = 0; // for selecting bit masks
	public static final int NEGATIVE = 1;

	byte   type; // syntactic class
	byte[] modifiermasks = new byte[2]; // bit masks
	byte[] semanticmasks = new byte[2]; // bit masks

	// compare type with pattern

	public boolean matchSyntaxType (

		SyntaxSpec spec

	) {
		return (
			((type & spec.type) & Syntax.x0F) == 0 &&
			((type ^ spec.type) & Syntax.xF0) == 0
		);
	}

	// compare features with pattern

	public boolean matchSyntaxFeatures (

		SyntaxSpec spec

	) {
		return !(
			(modifiermasks[0] & ~spec.modifiers) != 0 ||
			(modifiermasks[1] &  spec.modifiers) != 0 ||
			(semanticmasks[0] & ~spec.semantics) != 0 ||
			(semanticmasks[1] &  spec.semantics) != 0
		);
	}

	public String toString (

	) {
		byte[] bs;
		bs = modifiermasks;
		String fmm = String.format("[%02x%02x]",bs[1],bs[0]);
		bs = semanticmasks;
		String fsm = String.format("[%02x%02x]",bs[1],bs[0]);
		return Syntax.tb.interp(type) + ": " + fmm + fsm;
	}
}

public class Syntax {

	public static final byte x0F = (byte) 0x0F; // for masking nibbles
	public static final byte xF0 = (byte) 0xF0; //

	private static String[] typn;

	public  static byte unknownType = 0;        // default predefined type

	public  static byte adjectiveType;          // defining syntactic types
	public  static byte adverbType;
	public  static byte auxiliaryType;
	public  static byte determinerType;
	public  static byte initialType;
	public  static byte nameType;
	public  static byte nounType;
	public  static byte numberType;
	public  static byte prepositionType;
	public  static byte pronounType;
	public  static byte timeType;
	public  static byte verbType;
	public  static byte breakFeature;
	public  static byte capitalFeature;         // defining syntactic features
	public  static byte functionalFeature;
	public  static byte inflectedFeature;
	public  static byte moreFeature;
	public  static byte possessiveFeature;

	public  static CombinedSymbolTable tb;

	// check for content constituent

	public static final boolean functional (
		SyntaxSpec ss
	) {
//		System.out.println("modf= " + ss.modifiers + ", func= " + functionalFeature);
		return (ss.modifiers & functionalFeature) != 0;
	}

	public static byte feature (
		String name
	) {
		return tb.modifierFeature(name);
	}

	public static void initialize (
		CombinedSymbolTable tbl
	) {
		tb = tbl;
		adverbType      = tb.syntacticType("ADV");
		auxiliaryType   = tb.syntacticType("AUX");
		determinerType  = tb.syntacticType("DET");
		initialType     = tb.syntacticType("INI");
		nameType        = tb.syntacticType("NAM");
		nounType        = tb.syntacticType("NOU");
		numberType      = tb.syntacticType("NUM");
		prepositionType = tb.syntacticType("PRE");
		pronounType     = tb.syntacticType("PRO");
		timeType        = tb.syntacticType("TIM");
		verbType        = tb.syntacticType("VER");
		breakFeature      = feature("BREA");
		capitalFeature    = feature("CAPI");
		inflectedFeature  = feature("INFL");
		functionalFeature = feature("FUNC");
		moreFeature       = feature("MORE");
	}

	// map pattern string into syntax specification

	public static void patternToSpecification (

		SyntaxPatt patt,
		SyntaxSpec spec

	) {
//		System.out.println("**from " + patt);
		spec.type = (byte)(patt.type);
		spec.modifiers = patt.modifiermasks[SyntaxPatt.POSITIVE];
		spec.semantics = patt.semanticmasks[SyntaxPatt.POSITIVE];
//		System.out.println("**to   " + spec);
	}

}
