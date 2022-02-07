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
// LiteralPattern.java : 06feb2022 CPM
// identification of syntactic element by pattern matching

package aw.phrase;

import aw.Letter;
import aw.phrase.Syntax;
import java.io.*;

public class LiteralPattern {

	public    static final String file = "litss";
	
	public    static final int LIMIT = 512;
	protected static final int NINDX = Letter.NAN + 3;
	
	protected static final String WILDs = "@#!?%"; // wildcards matching 1 char
	protected static final String NULLs = "[]\\";  // these match null strings
	protected static final String INITs = WILDs + "*["; // may legally start pattern

	protected static final char UPPR = '!';
	protected static final char CNSN = '%';
	protected static final char LBKT = '[';
	protected static final char RBKT = ']';
	protected static final char INPT = '^';
	protected static final char LIMT = '|';
	protected static final char RSTR = '*';
	protected static final char ALPH = '@';
	protected static final char NUMR = '#';
	protected static final char SPCS = '_';
	protected static final char WILD = '?';
	protected static final char ESCP = '\\';
	
	protected static final char DEL = (char) Parsing.Empty;

	protected short[] index  = new short[NINDX];   // first entry starting with a given character
	protected short[] offset = new short[LIMIT+1]; // offset of each entry in pattern array
	protected SyntaxSpec[] syntax = new SyntaxSpec[LIMIT]; // syntax for entry
	protected char[] pattern; // stored entries

	// get patterns from file (JDK 1.1)
	
	public void load (
	
		DataInputStream in
	
	) throws IOException {
	
		for (int i = 0; i < NINDX; i++)
			index[i] = in.readShort();
		int n = index[NINDX-1];
		for (int i = 0; i <= n; i++)
			offset[i] = in.readShort();
		for (int i = 0; i <  n; i++) {
			syntax[i] = new SyntaxSpec();
			syntax[i].read(in);	
		}
		int m = offset[n];
		pattern = new char[m];
		InputStreamReader rd = new InputStreamReader(in);
		rd.read(pattern,0,m);
		
	}
	
	// put patterns into file (JDK 1.1)
	
	public void save (
	
		OutputStream os
	
	) throws IOException {
	
		if (pattern == null)
			return;
		BufferedOutputStream bos = new BufferedOutputStream(os);
		DataOutputStream out = new DataOutputStream(bos);
		for (int i = 0; i < NINDX; i++)
			out.writeShort(index[i]);
		int n = index[NINDX-1];
		for (int i = 0; i <= n; i++)
			out.writeShort(offset[i]);
		for (int i = 0; i <  n; i++)
			syntax[i].write(out);
		int m = offset[n];
		OutputStreamWriter wr = new OutputStreamWriter(bos);
		wr.write(pattern,0,m);
		wr.flush();
		
	}

}
