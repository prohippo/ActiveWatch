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
// Syntax.java : 19Jun00 CPM
// define named syntactic categories

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

}

public class Syntax {

	public static final byte x0F = (byte) 0x0F; // for masking
	public static final byte xF0 = (byte) 0xF0; //

	// for named types
	
	public static byte 
		adjectiveType,
		adverbType,
		auxiliaryType,
		determinerType,
		initialType,
		nameType,
		nounType,
		numberType,
		prepositionType,
		timeType,
		unknownType,
		verbType;

	// for named features
	
	public static byte
		capitalFeature,
		functionalFeature,
		breakFeature,
		inflectedFeature,
		possessiveFeature,
		moreFeature;
		
	private static boolean done = false;
	
	// define categories from symbol table
	
	public static void initialize (

		SymbolTable tb
		
	) {
		if (done) return;
		
		// syntax types to be recognized explicit for phrase extraction

		adjectiveType   = (byte) tb.syntacticType("ADJective");
		adverbType      = (byte) tb.syntacticType("ADVerb");
		auxiliaryType   = (byte) tb.syntacticType("AUXiliary");
		determinerType  = (byte) tb.syntacticType("DETerminer");
		initialType     = (byte) tb.syntacticType("INItial");
		nameType        = (byte) tb.syntacticType("NAMe");
		nounType        = (byte) tb.syntacticType("NOUn");
		numberType      = (byte) tb.syntacticType("NUMber");
		prepositionType = (byte) tb.syntacticType("PREposition");
		timeType        = (byte) tb.syntacticType("TIMe");
		unknownType     = (byte) tb.syntacticType("UNKnown");
		verbType        = (byte) tb.syntacticType("VERb");
		
		// syntax features

		capitalFeature    = (byte) tb.modifierFeature("CAPital");
		functionalFeature = (byte) tb.modifierFeature("FUNctional");
		breakFeature      = (byte) tb.modifierFeature("BREak");
		inflectedFeature  = (byte) tb.modifierFeature("INFlected");
		possessiveFeature = (byte) tb.modifierFeature("POSsessive");
		moreFeature       = (byte) tb.modifierFeature("MORe");

		done = true;
	}

	// check for content constituent
	
	public static final boolean positionalType (
		SyntaxSpec ss
	) {
		return (ss.modifiers & functionalFeature) == 0;
	}
	
}
