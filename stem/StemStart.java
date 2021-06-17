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
// StemStart.java : 23May00 CPM
// standard initializations

package stem;

import aw.ResourceInput;
import aw.AWException;
import java.io.*;

public class StemStart {

	static public Stem   tree;
	static public Stop  table;
	static public Stopat list;

	public static void load (
		
		String sufs,   // suffix file name
		String stps,   // stop   file name
		String stpats  // stop patterns file name
		
	) throws AWException {
	
		if (tree != null)
			return;
		
		try {
		
			DataInputStream iu = ResourceInput.openStream(sufs);
			tree = new Stem(iu);
			iu.close();

			reload(stps,stpats);

		} catch (IOException x) {
			throw new AWException("stem initialization fails");
		}
		
	}
	
	public static void reload (
	
		String stps,   // stop   file name
		String stpats  // stop patterns file name
		
	) throws AWException {
	
		try {
		
			DataInputStream it = ResourceInput.openStream(stps);
			table = new Stop(it);
			it.close();

			BufferedReader rd = ResourceInput.openReader(stpats);
			list = new Stopat(rd);
			rd.close();
		
		} catch (IOException e) {
			throw new AWException(e);
		}

	}
	
	public static void reset (
	
	) {
	
		tree  = null;
		table = null;
		list  = null;
		
	}
	
}