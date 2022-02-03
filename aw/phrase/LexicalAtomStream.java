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
// LexicalAtomStream.java : 30jan2022 CPM
// produce a sequence of syntactically tagged atoms

package aw.phrase;

import aw.Letter;
import aw.phrase.Syntax;

public class LexicalAtomStream extends LexicalStream {

	// inherits
	// CharArray text;

	private LiteralType   lt; // for multi-word atoms

	private LexicalAtom atom; // for return

	private boolean putBack = false;

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
		this.lt = lt;
		atom = new LexicalAtom();
	}

	// put back an atom

	public void backUp (
		LexicalAtom a
	) {
		putBack = true;
	}

	// special check for putback

	public int find (

	) {
		return (putBack) ? 0 : super.find();
	}

	// gets the next token from a text string plus syntactic information

	public LexicalAtom next (

	) {
		if (putBack) {
			putBack = false;
			return atom;
		}

		CharArrayWithTypes text = (CharArrayWithTypes) this.text;

		LexicalAtom a = atom;
		a.skip           = super.find();
		a.spec.type      = Syntax.unknownType;
		a.spec.modifiers = a.spec.semantics = 0;
		a.atom[0] = 0;

		// check for special case of null atom

//		System.out.println("length= " + text.length());
		if (text.length() == 0)
			return null;

		char x = text.charAt(0);
		if (stop(x,a))
			return a;

		// set feature bits for any capitalization

		if (Character.isUpperCase(x))
			a.spec.modifiers |= Syntax.capitalFeature;

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

		}
		else {

			// otherwise, use standard extraction method

			kl = get();
//			System.out.println("kl= " + kl);
//			System.out.println("text=[" + text + "]");
			if (kl == 1 && stop(text.charAt(-1),a)) {
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
				}
				else if (Character.toLowerCase(text.charAt(-1)) == 's' &&
						 text.charAt(-2) == Letter.APO) {
					a.spec.modifiers |= Syntax.possessiveFeature;
					text.putCharBack(blank);
					text.putCharBack(empty);
				}
			}
			kl = text.position() - its;
			a.span = kl;

		}

//		System.out.println("1 " + a);
		text.copyChars(a.atom,kl,a.span);  // fill current atom for return
//		System.out.println("2 " + a);
		return a;

	}

	// set a phrase or sentence stop atom

	private boolean stop (
		char x,
		LexicalAtom a
	) {
		if ((x & 0x80) == 0)
			return false;
		else {
			if (x == LexicalStream.stp)
				a.stopp = true;
			else
				a.stops = true;
			a.span = 1;
			a.length = 0;
			text.skip(1);
			return true;
		}
	}

	////////
	//////// for debugging

	public static void main ( String[] a ) {

		if (a.length == 0) {
			System.out.println("need a String argument");
			System.exit(0);
		}
		try {
			System.out.println("=" + a[0]);
			CharArrayWithTypes chwt = new CharArrayWithTypes(a[0]);
			System.out.println("[" + chwt + "]");
			LexicalAtomStream las = new LexicalAtomStream(chwt);     // no multi-word atoms
			for (int i = 0; i < 6; i++) {
				LexicalAtom atom = las.next();
				System.out.println(atom);
				System.out.println("[" + chwt + "]");
			}			
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}

	}

}
