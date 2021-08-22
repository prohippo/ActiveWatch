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
// AW file LiteralBase.java : 18aug2021 CPM
// literal n-gram tables for lookup

package gram;

import aw.*;
import java.io.*;

public class LiteralBase {

	public static final String file = TextAnalysisFile.literalFile;

	public static final char TM     = '\u009c'; // special terminator

	public static final int  MXL    = 8;        // nominal literal pattern size

	protected short[] ldsx = new short[Letter.NAN+1];  // start of leading  literals
	protected short[] trsx = new short[Letter.NAN+1];  // start of training literals

	protected short[] litx = new short[Gram.NLIT+1];   // offsets to literals
	protected char [] lita = new char [Gram.NLIT*MXL]; // literals array

	public void load (

		DataInputStream in

	) throws AWException {
		int i;

		try {
			for (i = 0; i < Letter.NAN+1; i++)
				trsx[i] = in.readShort();

			for (i = 0; i < Letter.NAN+1; i++)
				ldsx[i] = in.readShort();

			for (i = 0; i < Gram.NLIT; i++)
				litx[i] = in.readShort();
			litx[i] = 0;

			for (i = 0; i < lita.length; i++)
				lita[i] = in.readChar();

		} catch (IOException e) {
			throw new AWException("cannot load literals");
		}
	}

	public void save (

		DataOutputStream out

	) throws AWException {
		int i;

		try {
			for (i = 0; i < Letter.NAN+1; i++)
				out.writeShort(trsx[i]);

			for (i = 0; i < Letter.NAN+1; i++)
			out.writeShort(ldsx[i]);

			for (i = 0; i < Gram.NLIT; i++)
				out.writeShort(litx[i]);
			litx[i] = 0;

			for (i = 0; i < lita.length; i++)
				out.writeChar(lita[i]);

		} catch (IOException e) {
			throw new AWException("cannot save literals");
		}
	}

}
