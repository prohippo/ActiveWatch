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
// EndingType.java : 22Apr99 CPM
// to determine syntactic type

package aw.phrase;

import aw.AWException;
import aw.ResourceInput;
import aw.Letter;
import java.io.*;

public class EndingType {

	private static EndingBase etb;

	// get syntactic type from match of word endings
	
	public static boolean match (
	
		char[] a,
		int   ln,
		SyntaxSpec x
		
	) {
		if (etb == null)
			load();

		// ending cannot include first vowel in token

		int ab = 0;
		for (; ab < ln; ab++)
			if (EndingBase.VOWEL(a[ab]))
				break;
		if (ab == ln)
			return false;
			
		byte ms = (byte)(~x.modifiers);
		
		SyntaxSpec save = null;

		int ap = ln - 1;
		int k = Letter.toByte(a[ap]);
		if (k >= Letter.NA || k < 0)
			return false;
		int n = etb.endx[k];
		if (n == EndingBase.Empty)
			return false;

		// get initial entry into suffix table and start scan

		while (ap > ab) {

			byte b = Letter.toByte(a[ap]);
			if (b == etb.endtbl[n].alpha) {

				// continue down tree to match longest possible suffix

				if (etb.endtbl[n].syntax.type != Syntax.unknownType) {
					byte m = etb.endtbl[n].constraint;
					if ((m & ms) == 0)
						save = etb.endtbl[n].syntax;
				}

				// maximum match reached?

				if (etb.endtbl[n].isLeftEnd())
					break;
				n++;
				--ap;
				
			}
			else {

				// if no match, try the next alternative subtree

				n = etb.endtbl[n].getLink();
				if (n == 0)
					break;
			}
		}
		
		// show best match
		
		if (save == null)
			return false;
		else {
			x.type = save.type;
			x.modifiers |= save.modifiers;
			x.semantics |= save.semantics;
			return true;
		}
	}
	
	// read in ending table
	
	private static void load (
	
	) {
		try {
			DataInputStream in = ResourceInput.openStream(EndingBase.file);
			etb = new EndingBase();
			etb.load(in);
			in.close();
		} catch (IOException e) {
			etb.clear();
		}
	}
	
}
