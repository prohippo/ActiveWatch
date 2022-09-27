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
// PhraseDump.java : 15sep2022 CPM
// dump phrase analyses for all current items

package aw.phrase;

import aw.ByteTool;
import aw.Format;
import aw.phrase.Parsing;
import aw.phrase.CombinedSymbolTable;
import aw.phrase.Syntax;

import java.io.*;

public class PhraseDump {

	public static boolean show (

		String  t,       // text for analysis
		Parsing p,       // phrase analysis of text
		SymbolTable stb, // symbol table for reporting analyses
		PrintStream out

	) {
		boolean status = true;
		int phno=0;  // statistics of analysis
		int tp  =0;  //

//		System.out.println(" ** func= " + Format.hex(Syntax.functionalFeature));

		if (p.count == 0)
			return status;

		int bl = p.length;
		byte[] b = p.buffer;

		int bp = 0; // byte pointer into parsing buffer
		int vb;     // byte  value from parse
		int vs;     // short value

		int phraseCount = 0;
		int sentenceCount = 0;
		int paragraphCount = 0;

		while (bp < bl) {

			if (phno > p.count) break; // internal phrase count takes priority

			int n = b[bp++];
			switch (n) {

case Parsing.Paragraph:
				vs = ByteTool.bytesToShort(b,bp);
				bp += 2;
				out.println("<paragraph   > +" + Format.it(vs,4,'0'));
				tp += vs;
				paragraphCount++;
				break;

case Parsing.Sentence:
				vs = ByteTool.bytesToShort(b,bp);
				bp += 2;
				out.println("<sentence    > +" + Format.it(vs,4,'0'));
				tp += vs;
				sentenceCount++;
				break;

case Parsing.Phrase:
				vs = ByteTool.bytesToShort(b,bp);
				bp += 2;
				out.println("<phrase   " + Format.it(phno++,3) + "> +" + Format.it(vs,4,'0'));
				tp += vs;
				phraseCount++;
				break;

case Parsing.Pad:
				out.println("<pad         >");
				break;

default:
//				out.print(String.format("(%2d)",n));
				out.print("<" + Format.it(stb.interp((byte)(n)),12) + "> ");
				byte x = b[bp++];         // syntactic features
				out.print("[" + Format.hex(x));

				byte y = b[bp++]; // semantic  features
				out.print(" " + Format.hex(y) + "]");

				vs = ByteTool.bytesToShort(b,bp);
				bp += 2;
				out.print(" +" + Format.it(vs,4,'0'));
				int wl = b[bp++];
				out.print(" " + Format.it(wl,2) + " chars");
				tp += vs;
				if (wl < 0) {
					out.print(" ????????");
					status = false;
				}
				else if (wl > 0) {
					if (tp + wl <= t.length())
						out.print("  [" + t.substring(tp,tp+wl) + "]");
					else {
						out.print(" >>>> OUT OF BOUNDS !!!! ");
						out.print("(tp= " + tp + ", wl= " + wl);
						out.print(", length= " + t.length() + ")");
						status = false;
					}
				}
				out.println(" @" + tp);
				tp += wl;
			}
		}
		out.println("------------------------");
		out.println("Paragraphs: " + paragraphCount);
		out.println("Sentences:  " + sentenceCount);
		out.println("Phrases:    " + phraseCount);
		return status;
	}

	// alternate byte dump

	public static void showBytes (
		Parsing     ps,
		PrintStream out
	) {
		int Pc = ps.getCount();
		int Pp = ps.getLength();
		byte[] Pb = ps.getBuffer();
		out.print("phrase count= " + Pc + ", length= " + Pp);
		for (int i = 0; i < Pp; i++) {
			if (i%20 == 0)
				out.println();
			out.print(String.format(" %02x",Pb[i]));
		}
		out.println();
	}


//// testing on simple analysis
////

	private static final byte[] b0 = { // simple partial parse
		Parsing.Paragraph , 0, 0,
		Parsing.Sentence  , 0, 0,
		Parsing.Phrase    , 0, 0,
 		40,  0,  0,  0,  0,  9,
		20,  1,  0,  0,  1,  6,
		20,  1,  0,  0,  1,  5,
		Parsing.Phrase    , 0, 1,
		40,  4,  0,  0,  0,  6,
		Parsing.Pad, Parsing.Pad, Parsing.Pad, Parsing.Pad,
		Parsing.Phrase    , 0, 1,
		96, 38,  0,  0,  0,  2,
		 0,  0,  0,  0,  1,  6,
		(byte) 0x80   // padding to make the length of parsing even
	};

	private static final String text =
		"President Donald Trump stayed on script for more than a week.";

	public static void main ( String[] av ) {
		CombinedSymbolTable symb;
		try {
			symb = new CombinedSymbolTable();
			Syntax.initialize(symb);
			symb.dump();
			Parsing psg = new Parsing(3,b0.length,b0);
			System.out.println("parsing= " + psg);
			show(text,psg,symb,System.out);
			showBytes(psg,System.out);
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
	}

}
