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
// AW file Hubber.java : 06sep2023 CPM
// report on link hubs

package aw.hub;

import aw.AWException;
import aw.Item;
import aw.Link;
import aw.Format;
import object.*;
import java.io.*;

// scan cluster links for hub items

public class Hubber {
	
	private ItemCounter  x;  // for selecting
	
	private LinkMatrix  ma;
	private LinkMapping lm;
	private int th;          // threshold as scaled
	
	public Hubber (
		double thr       // link threshold
	) throws AWException {
		ma = new LinkMatrix(Link.MXML);
		lm = new LinkMapping();
		x  = new ItemCounter(0);
		th = (int) thr*LinkMatrix.SCALF;
	}
	
	public void run (
		int minL         // minimum link count for hub
	) throws AWException {
				
		for (int r = 1; r <= ma.nrow; r++) {
			// scan row in upper link matrix triangle
			for (int is = ma.lrU[r], ie = ma.lrU[r+1]; is < ie; is++) {
				Item itr = lm.fromLinkIndex(r);
				int sg = ma.lvU[is];
				if (sg >= th) {  // cpunt only links above threshold
					Item it = lm.fromLinkIndex(ma.lkU[is]);
					double w = ((double) sg)/LinkMatrix.SCALF;
					x.tally(itr.bn,itr.xn,sg);
					x.tally(it.bn,it.xn,sg);
				}
			}
		}
		x.report(minL);
	}
	
}
