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
// LexicalAtomStream.java : 27oct2022 CPM
// produce a sequence of syntactically tagged atoms from text stream

package aw.phrase;

import aw.Letter;
import aw.phrase.Syntax;

public class LexicalAtomStream extends LexicalStream {

	// preallocate lexical atoms for reuse

	private static final int nR = 4; // how many in rotation

	private class AtomRotation {

		private LexicalAtom[] ring = new LexicalAtom[nR];
		private int ringK = 0;

		// constructor

		public AtomRotation ( ) {

			for (int i = 0; i < nR; i++) {
				ring[i] = new LexicalAtom();
//				System.out.println(i + ") " + ring[i]);
			}
		}

		// get next atom in rotation

		public LexicalAtom next ( ) {
			LexicalAtom a = ring[ringK++];
			a.skip = a.span = a.length = 0;
			a.stops = a.stopp = false;
//			System.out.println(a.spec);
			a.spec.clear();
			if (ringK == nR) ringK = 0;
			return a;
		}

		// show rotation for debugging

		public void dump ( ) {
			int n = ringK;
			for (int i = 0; i < nR; i++) {
				System.out.println(n + ") " + ring[n]);
				if (--n < 0) n += nR;
			}
		}

	}

	// source of reusable atoms

	private AtomRotation rotation = new AtomRotation();

	// CharArray text is inherited from super class

	private LiteralType   lt; // for parsing multi-word atoms

	private LexicalAtom atom; // for temporarily saving atom for next output

	private SyntaxSpec  prev; // left context for setting syntax of bext atom

	// initialize

	public LexicalAtomStream (

		CharArrayWithTypes tx

	) {
		this(tx,null);  // skip literals
	}

	public LexicalAtomStream (

		CharArrayWithTypes tx,
		LiteralType lt

	) {
		super(tx);
//		System.out.println(this);
		this.lt = lt; // to recognize literals
		atom = null;  // forget any saved previous atom
		resetPreviousSyntax();
	}

	// reset context for getting syntax

	public void resetPreviousSyntax (

	) {
		prev = new SyntaxSpec();
	}

	// put an atom back at the front of stream

	public void putBack (
		LexicalAtom a
	) {
		atom = a;
	}

	// special check for putback

	public int find (

	) {
//		System.out.println("find start of atom");
		return (atom != null) ? 0 : super.find();
	}

	// gets the next token from a text string plus syntactic information

	public LexicalAtom next (

	) {
		if (atom != null) {
//			System.out.println("putBack= " + atom); 
			LexicalAtom as = atom;
			atom = null;
			return as;
		}

//		System.out.println("next: " + text);
		CharArrayWithTypes text = (CharArrayWithTypes) this.text;

		LexicalAtom a = rotation.next();

		// check for special case of no text input

//		System.out.println("length= " + text.length());
		if (text.isEmpty())
			return null;

		a.skip = find();   // advance in text to next atom

//		System.out.println("skip= " + a.skip + " in " + this);

		char x = text.charAt(0);
//		System.out.println("x= " + x + " : " + a);
		if (stopping(x,a)) {   // look for single stop character
			return a;
		}

//		System.out.println("set atom feature bits");
		// set feature bits for any capitalization

		for (int i = 1; Character.isLetterOrDigit(text.charAt(i)); i++)
			if (Character.isUpperCase(text.charAt(i))) {
				a.spec.modifiers |= Syntax.moreFeature;
				break;
			}

		// get next atom

		int its = text.position();
		int kl  = 0;

		// check for known literals

		if (lt != null && lt.match(text,a.spec)) {

			// treat any matched literal as single atom, but normalize internal spaces

			int it = text.position();
			kl = it - its;
			int j = -kl;
			for (int i = j; i < 0; i++, j++) {
				if (!Character.isWhitespace(text.charAt(j)))
					text.moveChar(j,i);
				else {
					text.setCharAt(j,' ');
					while (++i < 0 && Character.isWhitespace(text.charAt(i)));
					--i;
				}
			}
			a.span = kl;
//			System.out.println("by pattern: " + a);

		}
		else {

			// otherwise, use standard extraction method

			kl = get();
//			System.out.println("got kl= " + kl + " chars");
//			System.out.println("new input=[" + text + "]");
			if (kl == 1 && stopping(text.charAt(-1),a)) {
				text.skip(-1);
				return a;
			}

//			System.out.println("full length= " + text.length());
			int n = -kl;
			int dk = 0;
			if (kl == 0)
				a.spec.modifiers |= Syntax.breakFeature;
			else if (kl == 2 && Character.isLetter(text.charAt(n)) && text.charAt(n+1) == '.')
				a.spec.type = Syntax.initialType;
			else if (kl > 3) {
				if (text.charAt(-1) == Letter.APO &&
					Character.toLowerCase(text.charAt(-2)) == 's') {
					a.spec.modifiers |= Syntax.possessiveFeature;
					text.putCharBack(empty);
					a.stopp = true;
				}
				else if (Character.toLowerCase(text.charAt(-1)) == 's' &&
						 text.charAt(-2) == Letter.APO) {
					a.spec.modifiers |= Syntax.possessiveFeature;
					text.putCharBack(blank);
					text.putCharBack(empty);
					a.stopp = true;
				}
			}
			kl = text.position() - its;
			a.span = kl;
//			System.out.println("by other: " + a);

		}

//		System.out.println("1st char= " + x + String.format(", cap= %02x",Syntax.capitalFeature));
		if (Character.isUpperCase(x))
			a.spec.modifiers |= Syntax.capitalFeature;

//		System.out.println("prev= " + prev);
//		System.out.println("1: " + a);
		text.copyChars(a.atom,kl,a.span);  // fill current atom for return
		a.length = a.span;
//		System.out.println("2: " + a);
//		System.out.println("text= [" + text + "]");
		a.getSyntax(prev);
//		System.out.println("3: " + a);
		prev = a.spec;
		return a;

	}

	// set a phrase or sentence stop atom

	private boolean stopping (
		char x,
		LexicalAtom a
	) {
//		System.out.println("stop x= " + x);
		if ((x & 0x80) == 0)
			return false;
		else {
			if (x == LexicalStream.stp)
				a.stopp = true;
			else
				a.stops = true;
			a.atom[0] = x;
			a.span = 1;
			a.length = 0;
//			System.out.println("a= " + a);
			text.skip(1);
//			System.out.println("text= " + text);
			resetPreviousSyntax();
			return true;
		}
	}

	// show stream

	public final String toString ( ) {
		return "stream @" + text.position() + " = " + text.toString();
	}

	////////
	//////// for debugging

	public static void main ( String[] a ) {

		if (a.length == 0) {
			System.out.println("need a String argument");
			System.exit(0);
		}
		try {
			PhraseSyntax.loadDefinitions();
//			System.out.println(String.format("cap= %02x",Syntax.capitalFeature));
			CharArrayWithTypes chwt = new CharArrayWithTypes(a[0]);
//			System.out.println("input buffer= " + chwt);
			LexicalAtomStream las = new LexicalAtomStream(chwt);
			for (int i = 0; las.notEmpty(); i++) {
//				System.out.println(" -------- " + i);
//				System.out.println(las);
				LexicalAtom atom = las.next();
//				System.out.println(">> " + atom);
//				System.out.println("(" + i + ") " + chwt);
			}			
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}

	}

}
