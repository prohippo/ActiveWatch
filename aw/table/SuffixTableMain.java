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
// AW file SuffixTableMain.java : 13jul2021 CPM
// build suffix table

package aw.table;

import aw.AWException;
import aw.TextAnalysisFile;
import java.io.*;

public class SuffixTableMain {

	public static final String suffix = "suffix";
	public static final String action = "action";

	public static void main ( String[] av ) {
	
		SuffixTable tb = new SuffixTable();
	
		BufferedReader   ins = null;
		BufferedReader   ina = null;
		FileOutputStream out = null;
		
		try {
		
			ins = new BufferedReader(new FileReader(suffix));
			ina = new BufferedReader(new FileReader(action));
			out = new FileOutputStream(TextAnalysisFile.suffixFile);
			
			tb.build(ina,ins);
			tb.save(new DataOutputStream(out));
		
		} catch (IOException e) {
			System.err.println("I/O error: " + e);
		} catch (AWException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (ins != null)
					ins.close();
				if (ina != null)
					ina.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
		
	}
	
}
