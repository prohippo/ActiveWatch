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
// TG.java : 03oct2023 CPM
// test n-gram text analysis

package test;

import aw.*;
import stem.*;
import gram.*;
import object.*;
import java.io.*;

public class TG extends TGbase {

	public TG ( ) throws IOException { super(); }
	
	public static void main ( String[] av ) {
		String text;
		String line;
		
		System.out.println("test full text analysis");
		
		if (av.length > 0)
			text = av[0];
		else
			try {
				text = "";
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				while ((line = in.readLine()) != null)
					text += " " + line;
				System.out.println(">> " + text);
			} catch (IOException e) {
				System.err.println("input error: " + e);
				return;
			}

		AnalyzedToken tok;
				
		try {
			KeyTextAnalysis an = new KeyTextAnalysis();
			an.setText(text);
			TG tg = new TG();
			while ((tok = an.next()) != null) {
				System.out.println(tok);
				tg.show(tok);
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

}
