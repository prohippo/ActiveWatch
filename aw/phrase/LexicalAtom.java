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
// LexicalAtom.java : 27oct2022 CPM
// unit of parsing for phrase analysis with associated data type

package aw.phrase;

import aw.phrase.Syntax;
import java.util.Arrays;
import java.io.*;

public class LexicalAtom {

	public static final int L = 128; // make array larger than any actual atom

	public int        skip; // offset from current stream location
	public int        span; // actual width within stream
	public int      length; // atom length
	public char[]     atom; // atom as normalized chars
	public SyntaxSpec spec; // syntax for atom
	public boolean   stopp; // flag to stop phrase
	public boolean   stops; //         stop sentence

	// initialize

	public LexicalAtom (

	) {
		spec = new SyntaxSpec();
		atom = new char[L+1];
	}

	// for debugging

	public final byte modifiers ( ) { return spec.modifiers; }

	// for debugging

	public final byte type ( ) { return spec.type; }

	// for printing

	public String toString ( ) {
		String sa = new String(Arrays.copyOfRange(atom,0,length));
		return sa + " [[" + spec + " ]] skip= " + skip + ", span= " + span
			+ ", length= " + length + ", stop s=" + stops + ", p=" + stopp;
	}

	// get syntax information by lookup or inference

	public void getSyntax (

		SyntaxSpec prev

	) {

		// get exact char[] for lookup in various ways

		char[] ax = new char[length];
		System.arraycopy(atom,0,ax,0,length);
//		System.out.println("getSyntax: " + this);
//		System.out.println("length= " + length + ", atom= " + (new String(ax)));

		// check for a number

		if (NumberType.match(ax,spec)) {
			prev = spec;
			return;
		}
//		System.out.println("not number");

		// look up the word or its ending for its syntactic type

		if (WordType.match(ax,spec)   ||
			EndingType.match(ax,spec) ||
			NameType.match(ax,spec,prev)) {
//			System.out.println("Word/Ending/Name Type=" + spec);
			if ((spec.modifiers & Syntax.breakFeature) != 0)
				stopp = true;
			prev = spec;
			return;
		}
//		System.out.println("not in dictionary or implied by ending or acronym ");

		// if this fails, remove any inflectional ending and try again

		if (InflectionType.match(ax,spec,prev)) {
//			System.out.println("inflection: " + (new String(ax)) + " : " + spec);
			prev = spec;
			return;
		}
//		System.out.println("default is unknown type");

		spec.type = Syntax.unknownType;
		prev = spec;

	}

	////
	//// for debugging

	private static final String ax = "stayed";

	public static void main ( String[] a ) {
		try {
			PhraseSyntax.loadDefinitions();
		} catch ( IOException e ) {
			System.err.println(e);
			System.exit(1);
		}
		String x = (a.length > 0) ? a[0] : ax;
		char[] cx = x.toCharArray();
		LexicalAtom atm = new LexicalAtom();
		System.arraycopy(cx,0,atm.atom,0,cx.length);
		atm.length = atm.span = cx.length;
		System.out.println("   " + atm);
		SyntaxSpec ss = new SyntaxSpec();
		atm.getSyntax(ss);
		System.out.println(">> " + atm);
	}
}
