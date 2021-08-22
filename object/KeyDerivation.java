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
// AW file KeyDerivation.java : 21aug2021 CPM
// get descriptive keys from items with respect to a profile

package object;

import aw.AWException;
import aw.Item;
import java.io.*;
import java.util.*;

public class KeyDerivation {

	private static final int HSZ = 4111; // prime hash size
	private static final int THR =   -1; // selection threshold

	protected static Ranking rkg;
	
	protected int nmky = 0; // number of keys selected
	
	private   int mxns = 0; // maximum number of keys kept

	private TextSubsegment ts = null;
	
	// standard constructor for non-phrase analyses
	
	public KeyDerivation ( ) { }
	
	// process a set of subsegments: possibly overridden
	
	public void set (
		byte[] pv, // expanded profile
		Item[] it, // item list
		int   nit, // item count
		int   mxn  // maximum selection
	) throws AWException {
		
		// start with empty list of keys
		
		if (rkg == null)
			rkg = new Ranking(mxn,HSZ,THR);
		else
			rkg.reset();
		
		nmky = 0;
		mxns = mxn;

		// scan all listed items
		
		for (int i = 0; i < nit; i++) {
			try {
				rankFromItem(it[i],pv);
			} catch (IOException e) {
				throw new AWException("cannot get text: ",e);
			}
		}
		
	}
	
	// process a single item
	
	protected void rankFromItem (
		Item  itm,
		byte[] pv
	) throws AWException,IOException {
	
		ts = new TextSubsegment(itm.bn,itm.xn);
		nmky = rkg.rank(ts.getText(),pv);
		
	}

	// get rest of derived keys up to specified maximum
	
	private ArrayList<String> ws = new ArrayList<String>();
		
	public String[] rest (
	
	) {
	
		ws.clear();
		for (int i = 0; i < mxns; i++) {
			String w = rkg.out();
			if (w == null)
				break;
			ws.add(w);
		}
		String[] a = new String[ws.size()];
		ws.toArray(a);
		return a;
		
	}

	// get successive derived keys
		
	public final String next ( ) { return rkg.out(); }

	// how many key occurrences selected
		
	public final int count ( ) { return nmky; }
	
	// close out text segments: can be overridden
	
	public       void close ( ) { if (ts != null) ts.close(); }
	
}
