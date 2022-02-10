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
// CombinedSymbolTable.java : 09feb2022 CPM
// for defining syntactic types and features

package aw.phrase;

import aw.AWException;
import aw.ResourceInput;
import java.io.*;

public class CombinedSymbolTable extends SymbolTable {

	private static final int BufferSize  = 80;  // input line buffer size

	private static final String SymbolFileName = "symbols";

	private int syntaxCount;  // number of syntax  symbols
	private int featureCount; // number of feature symbols

	public CombinedSymbolTable (

	) throws IOException {
		super();
		try {
			loadSymbols(ResourceInput.openReader(SymbolFileName));
		} catch (IOException e) {
			throw new IOException("no symbol file");
		}
	}

	public CombinedSymbolTable (

		BufferedReader reader

	) throws IOException {
		super();
		loadSymbols(reader);
	}

	private static final char DEL = 255;
	private static final char COM = ';';

	// read a line

	private String gets (

		BufferedReader in

	) throws IOException {
		String s = in.readLine();
		if (s != null) {
			s = s.trim();
			if (s.length() > 0) {
				char c = s.charAt(0);
				if (c == COM || c == DEL)
					s = "";
			}
		}
		return s;
	}

	// loads syntax type and feature modifier symbols into the global
	// symbolTable from the "symbols" file and sets symbolsLoaded flag

	private void loadSymbols (

		BufferedReader reader

	) throws IOException {
		String inBuff;
		int n;

		// read in symbol definitions until line starting with '.'

		int mapping = 0;

		try {

			while ((inBuff = gets(reader)) != null) {
				if (inBuff.length() == 0)
					continue;

				if (inBuff.charAt(0) == '.')
					break;

				if (syntaxCount++ >= TableSize)
					throw new IOException("symbol overflow at " + inBuff);

				n = insert(inBuff);
				symbolMap[mapping++] = (byte) aCODING(inBuff.substring(n));
			}

			// read and store feature definitions to end of input

			while ((inBuff = gets(reader)) != null) {
				if (inBuff.length() == 0)
					continue;

				if (syntaxCount + featureCount >= TableSize)
					throw new IOException("feature overflow at " + inBuff);

				n = insert(inBuff);

				// fill in feature value

				inBuff = inBuff.substring(n);
				int m = inBuff.indexOf('=');
				int x = (m < 0) ? 0 : Integer.parseInt(inBuff.substring(m + 1));
				symbolMap[mapping++] = (byte) x;
				featureCount++;
			}

		} catch (AWException e) {
			throw new IOException("cannot interpret symbols");
		}

		reader.close();
	}

	// to override

	int scanForType (

		String typ

	) {
		return scan(typ,0,syntaxCount);
	}

	// to override

	int scanForFeature (

		String fet

	) {
		return scan(fet,syntaxCount,syntaxCount+featureCount);
	}

}
