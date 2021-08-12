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
// AW file LinkMapping.java : 11aug2021 CPM
// convert link indices to subsegment references and vice versa
// with respect to a sequence file

package object;

import aw.*;
import java.io.*;

// mark batches within run

class RunIndex {

	short first; // first item of batch in run
	short limit; // one past last item of batch

}

// compressed run record from sequence referring to item numbers

class ItemRun {

	short base;  // where run starts in full sequence
	short ssn;   // starting item number of run
	short sse;   // one past last item number

}

public class LinkMapping {

	private static final int N = Link.MXTC + 10;   // must be at least MXTC

	private RunIndex[] rix = new RunIndex[Control.NTB];  // sequence record index by batch
	private ItemRun[]  rns = new ItemRun[N];       // compressed sequence records by item

	private byte[]  lkbn = new byte[Link.MXTC+2];  // batch numbers for items
	private short[] lkno = new short[Link.MXTC+2]; // full list of index numbers

	private int count; // how many items clustered

	public LinkMapping (

	) throws AWException {

		int   i,j;
		int  n,ns;
		int  b,bs;
		int  start,end;
		Subsegment ssr = null;

		// read sequence records and convert

		try {

			for (i = 0; i < Control.NTB; i++)
				rix[i] = new RunIndex();

			Sequence sqn = new Sequence("sequence");  // will load entire sequence file
			if (sqn.nmrun == 0)                       // if it exists
				throw new AWException("empty sequence");

			for (i = 0, bs = -1, n = 1; i < sqn.nmrun; i++) {
				b = sqn.r[i].bbn;
				start = sqn.r[i].ssn;
				end = start + sqn.r[i].nss;
//				System.out.println("batch " + b + ": " + start + " < " + end);
				for (j = start, ns = n; j < end; j++) {
					ssr = new Subsegment(b,j);
					if (ssr.sn == 1) {
						lkbn[n  ] = (byte)  b;
						lkno[n++] = (short) j;
					}
				}

				rns[i] = new ItemRun();
				rns[i].ssn  = (short)(ssr.it - (n - ns) + 1);
				rns[i].sse  = (short)(ssr.it + 1);
				rns[i].base = (short) ns;

				if (b != bs) {
					if (bs >= 0)
						rix[bs].limit = (short) i;
					rix[b].first = (short) i;
					bs = b;
				}
			}
			if (bs >= 0)
				rix[bs].limit = (short) i;

			count = n - 1;

		} catch (IOException e) {
			throw new AWException("cannot set link mapping");
		}
	}

	// convert item reference to link index number

	public int toLinkIndex (

		Item it

	) throws AWException {
		int b = it.bn;

		try {

			Subsegment ssr = new Subsegment(b,it.xn);

			// tag item link index

			for (int j = rix[b].first; j < rix[b].limit; j++) {
				if (ssr.it < rns[j].ssn)
					break;
				else if (ssr.it < rns[j].sse)
					return rns[j].base + ssr.it - rns[j].ssn;
			}

		} catch (IOException e) {
			throw new AWException("I/O error: ",e);
		}

		return -1;
	}

	// get item reference from link index

	public Item fromLinkIndex (

		int in

	) {

		Item it = new Item(Control.BADBatch,0,1);

		if (in > 0 && in <= Link.MXTC) {
			it.bn = lkbn[in];
			it.xn = lkno[in];
		}

		return it;
	}

	// get count of items linked

	public int countLinkIndex (

	) {
		return count;
	}

//	unit test

	public static void main ( String[] av ) {
		int m = 5;
		try {
			LinkMapping map = new LinkMapping();
			int n = map.countLinkIndex();
			if (m > n) m = n;
			int k = 1;
			for (int i = 1; i < m; i++, k += k) {
				Item it = map.fromLinkIndex(k);
				System.out.println(k + "= " + it.bn + "::" + it.xn);
			}
			System.out.println(av.length + " arguments");
			for (int i = 0; i < av.length; i++) {
				k = Integer.parseInt(av[i]);
				Item it = map.fromLinkIndex(k);
				System.out.println(k + "= " + it.bn + "::" + it.xn);
			}
		} catch (AWException e) {
			System.err.println(e);
		}
	}
}
