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
// AW file IndexedItem.java : 14Aug98 CPM
// for keeping retrieval lists

package object;

import aw.*;
import java.io.*;

// subsegment with full item reference

public class IndexedItem extends Item {

	public int index; // item index in batch
	
	static Subsegment sr; // save last record
	static Index      ix; // save last record
	
	// construct from subsegment reference
	
	public IndexedItem (
		Item it
	) throws AWException {
	
		super();
		bn = it.bn;
		xn = it.xn;
		sg = it.sg;
		try {
			sr = new Subsegment(bn,xn);
			index = sr.it;
		} catch (IOException e) {
			throw new AWException("subsegment",e);
		}
		
	}
	
	// construct from item reference
	
	public IndexedItem (
		AccessionNumber an,
		double          wt
	) throws AWException {
	
		super();
		index  = an.index();
		bn = (short) an.batch();
		try {
			ix = new Index(bn,index);
			xn = ix.sx;
			sg = (short)(wt*Item.scale);
		} catch (IOException e) {
			throw new AWException("index",e);
		}
		
	}
	
	// empty constructor
	
	public IndexedItem (
	) {
		index = -1;
	}
	
	// clean up
	
	public void close (
	) {
		if (sr != null)
			sr.close();
		if (ix != null)
			ix.close();
	}
		
}

