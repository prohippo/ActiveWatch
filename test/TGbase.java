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
// TGbase.java : 29jan2023 CPM
// diagnostic tool showing n-gram indexing

package test;

import aw.*;
import gram.*;
import stem.Token;
import object.AnalyzedToken;
import java.io.*;

public class TGbase extends TextAnalysisFile {

	private FastProbabilities pb=null;
	private GramDecode gd=null;

	public TGbase ( ) throws IOException {
		pb = new FastProbabilities();
		try {
			GramStart gs = new GramStart();
			LiteralDecoding ld = new LiteralDecoding(gs.table);
			gd = new GramDecode(gs.map,ld);

		} catch (AWException e) {
			throw new IOException("no decoding");
        	}
	}

	void show ( AnalyzedToken tok ) throws IOException {
		System.out.println("token <" + tok.toString() + ">");
		Short[] gx = tok.indices();
		for (int i = 0; i < gx.length; i++) {
			int gm = gx[i];
			if (gm == 0)
				break;
			System.out.print(" =" + Format.it(gm,6));
			System.out.print(" " + Format.it(gd.toString(gm),12));
			if (pb != null)
				System.out.print("(p=" + Format.it(pb.at(gm),8,6) + ")");
			System.out.println();
		}
    	}

	public static void main ( String[] a ) {
		System.out.println("show n-gram indexing");
		if (a.length == 0)
			return;
		try {
			TGbase tgb = new TGbase();
			SimpleList ls = new SimpleList(Token.MXW+2);

			GramStart gs = new GramStart();
			Characterizer ch = new Characterizer(gs.table,gs.map);

			for (int i = 0; i < a.length; i++) {
				Token t = new Token(a[i]);
				tgb.show(new AnalyzedToken(t,ch));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
