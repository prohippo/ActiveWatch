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
// GramStart.java : 23May00 CPM
// standard initializations

package gram;

import aw.ResourceInput;
import aw.AWException;
import java.io.*;

public class GramStart {

	public static Literal   set;
	public static Transform map;

	// get external definition tables for n-grams
	
	public static void load (
	
		String lits,
		String phonetic
		
	) throws AWException {
	
		if (set != null)
			return;
		
		try {
		
			reload(lits);
			
			BufferedReader rd = ResourceInput.openReader(phonetic);
			map = new Transform(rd);
			rd.close();

		} catch (IOException x) {
			throw new AWException(x);
		}
		
	}
	
	// update changed tables
	
	public static void reload (
	
		String lits
	
	) throws AWException {
	
		try {
		
			DataInputStream in = ResourceInput.openStream(lits);
			set = new Literal(in);
			in.close();

		} catch (IOException x) {
			throw new AWException(x);
		}
		
	}

	// clear tables
		
	public static void reset (
	
	) {
	
		set = null;
		map = null;
		
	}
	
}