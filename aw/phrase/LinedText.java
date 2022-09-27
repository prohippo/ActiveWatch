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
// LinedText.java : 19jul2022 CPM
// to line out big chunks of Unicode text after converting to reduced AW ASCII
// for figuring out paragraph boundaries

package aw.phrase;

import aw.*;
import aw.ByteTool;
import aw.Lines;                // actual lining algorithms
import aw.Inputs;               // for handling different text line terminators
import aw.CharArray;
import java.util.stream.Stream; // for creating a char input stream
import java.io.*;

public class LinedText {

	public String ts; // text string
	public int[]  lx; // line index
	public int    nl; // index limit

	public LinedText (
	
		String tx,  // chunk of text 
		int    lw
		
	) throws AWException {
		ts = ByteTool.bytify(tx);                        // reduce to ASCII only
		ByteArrayInputStream bs = new ByteArrayInputStream(ts.getBytes()); 
		Inputs in = new Inputs(new DataInputStream(bs)); // need char conversion
		Lines  ls = new Lines();                         // 

		CharArray lb;
		while ((lb = in.input()) != null) {
//			System.out.println("record: " + lb);
			ls.record(lb,lw);
		}
			
		lx = ls.textIndex();
		ts = ls.textString();
		nl = ls.countAll();
//		ls.dump();
	}

	public String toString ( ) {
		StringBuffer sb = new StringBuffer();
		sb.append(ts);
		sb.append("\n: ");
		for (int i = 0; i <= nl; i++){
			sb.append(" ");
			sb.append(lx[i]);
		}
		return sb.toString();
	}

	//// for debugging
	////

	public static final int NL = 10;

	public void dump ( ) {
		System.out.println("nl= " + nl);
		int n = nl > NL	? NL : nl;
		for (int i = 0; i < n; i++) {
			System.out.println(String.format("%3d: %4d",i,lx[i]));
		}
	}

	public static void main ( char[] a ) {
		String ts = "this is a text!";
		try {
			LinedText lt = new LinedText(ts,100);
//			lt.dump();
		} catch ( AWException e ) {
			System.err.println(e);
		}
	}
}
