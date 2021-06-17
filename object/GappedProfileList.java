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
// AW file GappedProfileList.java : 11Aug98 CPM
// Tukey gapping applied to match scores in list

package object;

import aw.*;

public class GappedProfileList extends SortedProfileList {

	public int gap; // index of gap found
	
	// constructor
	
	public GappedProfileList (
		int n
	) throws AWException {
		super(n);
		if (count == 0)
			return;
		gapping();
	}
	
	// constructor
	
	public GappedProfileList (
		Item[]  l, // list array
		int    nm  // size of list
	) throws AWException {
		super(l,nm);
		gapping();
	}
	
	// find first significant gap in listed similarity measures

	private void gapping (
	
	) {
	
		if (count <= 1)
			gap = count;
		else {	
			Gapping gp = new Gapping((float) list[0].sg);

			for (int i = 1; i < count; i++)
				gp.add((float) list[i].sg);
				
			gap = gp.gap();
		}
		
	}
	
}
