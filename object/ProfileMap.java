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
// AW file ProfileMap.java : 22Oct97 CPM
// profile allocation map

package object;

import aw.*;
import java.io.*;

public class ProfileMap extends Map {

	private int[] lsiz = new int[MXSP+1]; // cluster sizes
	private long[] pdat = new long[MXSP+1]; // review dates
	private boolean[] news = new boolean[MXSP+1]; // note new profiles

	// load  previous cluster attributes to help allocation
	
	public ProfileMap (
	
	) throws AWException {
		Attribute at; // profile attributes

		for (int k = 1; k <= MXSP; k++)
			if (!userType(k) && activeType(k)) {
				at = new Attribute(k);
				lsiz[k] = at.stm.nrold + at.stm.nrnew;
				pdat[k] = at.stm.rdate;
			}

	}

	// allocates storage for a new profile, possibly
	// deallocating older ones to make room

	private static final long MXd = 0x7FFFFFFFFFFFFFFFL; // maximum date
	
	public short allocate (
	
		byte type  // type of profile to allocate
		
	) {
		short pn; // profile number
		long  pd; // profile date
		int   ls; // list size
 
		if (countAll() < MXSP)

			// if there is a free profile, allocate it

			pn = findFree();

		else {

			// otherwise, deallocate least recently
			// reviewed profile with fewest hits

			pn = -1;
			pd = MXd;
			ls = Link.MXTC + 1;
			for (short k = 1; k <= MXSP; k++) {
				if (news[k] || !userType(k) || pdat[k] > pd)
					continue;
				if (pdat[k] == pd && lsiz[k] >= ls) continue;
				pn = k;
				pd = pdat[k];
				ls = lsiz[k];
			}
			if (pn < 0) {
				System.err.println("profile overflow");
				return (short)(-1);
			}

			System.out.println("freeing up profile " + pn);
		}
 
		// mark profile as allocated

		setType(pn,type);
		news[pn] = true;
		return pn;
	}
	
}
