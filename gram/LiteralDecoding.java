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
// AW file LiteralDecoding.java 21aug2021 CPM
// get literal information for decoding to string

package gram;

import aw.*;
import java.io.*;

class LiteralExtension extends LiteralBase {

	// load tables for decoding

	public LiteralExtension (
		DataInputStream in
	) throws AWException {
		load(in);
	}

	// get preloaded tables

	public LiteralExtension (
		Literal ls
	) {
		ldsx = ls.ldsx;
		trsx = ls.trsx;
		litx = ls.litx;
		lita = ls.lita;
	}

	// limit of alphanumeric char codes

	public static final char LIM = '\u0090';

	// represent literal n-grams

	public String decode (
		int ng  // literal index number
	) {

		int k,n;
		int   p;
		StringBuffer s = new StringBuffer();

		if (litx[ng] == 0)
			s.append('?');

		else if (ng >= trsx[Letter.NAN]) {

			// leading literal

			for (k = 0; k < Letter.NAN && ldsx[k] <= ng; k++)
				;
			s.append(Letter.from[--k]);
			for (p = litx[ng]; lita[p] < LIM; p++)
				s.append(lita[p]);
			if (lita[p] == LiteralBase.TM)
				s.append('-');
		}
		else {

			// trailing literal

//			System.out.println("trailing: " + ng);

			for (k = 0; k < Letter.NAN && trsx[k] <= ng; k++)
				;
//			System.out.println("k=" + k);
			for (p = litx[ng]; lita[p++] < LIM;)
				;
//			System.out.println("p=" + p + ", to " + litx[ng]);
//			System.out.println(new String(lita,litx[ng],p-litx[ng]));
			s.append('-');

			for (--p; p > litx[ng]; )
				s.append(lita[--p]);
			s.append(Letter.from[--k]);
		}
		return s.toString();
	}

}

public class LiteralDecoding {

	private LiteralExtension x;

	// initialize tables

	public LiteralDecoding (
		Literal lit
	) throws AWException {
		if (lit != null) {
			x = new LiteralExtension(lit);
			return;
		}

		try {
			FileInputStream is = new FileInputStream(FileAccess.to(LiteralBase.file));
			DataInputStream in = new DataInputStream(new BufferedInputStream(is));
			x = new LiteralExtension(in);
			in.close();
		} catch (IOException e) {
			throw new AWException("no literals: ",e);
		}
	}

	// actual decoding method

	public final String decode (
		int ng
	) {
		return x.decode(ng);
	}

}
