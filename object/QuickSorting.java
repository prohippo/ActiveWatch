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
// AW file QuickSorting.java : 10Nov97 CPM
// quicksorting of a string array

package object;

import aw.*;

// quicksort plush insertion sorting

public class QuickSorting {

	public static final String LoSentinel = " "; // low  sentinel record
	public static final String HiSentinel = "|"; // high sentinel record
	
	private static final int M    =   8; // switchover count for insertion sort
	private static final int NSTK = 100; // stack size for quicksort
	
	public static void sort (
 
		String[] rec,  // array of records with sentinels to sort
		int      max,  // actual record count excluding sentinels
		int 	 len   // size of key in chars
 
	) {
		int i,j;
		int upb,lwb;    // upper and lower bounds for partition
		int first,last; // converging limits within partition
		int free;       // index of free record slot to copy into
		
		int upsz,lwsz;  // partition split sizes
		
		int[] stkl = new int[NSTK]; // to stack lower bounds of partitions
		int[] stkh = new int[NSTK]; //          upper bounds
 
		int stkp = 0;
		int n    = max;
		
		String currnt;        // record to place within partition
		LimitedString curkey; // substring for comparison

		lwb = 1; // above low  sentinel
		upb = n; // below high sentinel
		
		for (;;) {
 
			// process the next partition of the array

			first = lwb;
			last = upb + 1;
 
			currnt = rec[lwb];
			curkey = new LimitedString(currnt,len);
			free = lwb;
 
			for (;;) {
 
				// put first record of partition
				// into its final position

				while (curkey.compareTo(rec[--last]) < 0)
					;
				if (last <= first)
					break;
 
				rec[free] = rec[last];
				free = last;
 
				first++;
				while (curkey.compareTo(rec[first++]) > 0)
					;
				if (last <= --first)
					break;
 
				rec[free] = rec[first];
				free = first;
			}
			rec[free] = currnt;
 
			// check if either half of the split partition
			// is large enough to split again

			lwsz = last - lwb;
			upsz = upb - last;
 
			if (upsz > M || lwsz > M) {

				// must continue to split array recursively,
				// stacking up only if both partitions are big
				
				if (lwsz <= upsz) {
					if (lwsz <= M)
						lwb = last + 1;
					else {
						stkl[stkp] = last + 1;
						stkh[stkp++] = upb;
						upb = last - 1;
					}
				}
				else if (upsz <= M)
					lwb = last - 1;
				else {
					stkl[stkp] = lwb;
					stkh[stkp++] = last - 1;
					lwb = last + 1;
				}
				
			}
			else if (--stkp < 0)

				// no further splitting with stack empty
				
				break;
				
			else {

				//pop next saved partition to split from stack
				
				lwb = stkl[stkp];
				upb = stkh[stkp];
			}
		}
 
		// when all partition sizes <= M, finish up
		// with straight insertion sort

		for (i = 1; i < n; i++) {
			j = i;
			currnt = rec[j+1];
			curkey = new LimitedString(currnt,len);
 
			while (curkey.compareTo(rec[j]) < 0) {
				rec[j+1] = rec[j];
				--j;
			}
			rec[j+1] = currnt;
		}
	}
}

