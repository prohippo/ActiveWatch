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
// StopTable.java : 21aug2021 CPM
// compile stop table from listing of patterns

package aw.table;

import aw.*;
import stem.*;
import java.io.*;
import object.QuickSorting;

public class StopTable extends StopBase implements TableBuilder {

	public static final int NSTP = 9600; // maximum stopword count

	String[] rec = new String[NSTP+2];   // stopword definition records

	int[] number = new int[MXSW+2];      // numbers of stops for length
		
	private int nt = 0; // bytes allocated in table
	
	public StopTable (
	
	) {
		super();
	}

	// compile table from input stream
			
	public void build (
	
		BufferedReader in
	
	) throws AWException {
	
		String r;
		int i = 0;
		rec[i++] = QuickSorting.LoSentinel;

		try {
		
			// read in each stop entry into array with length

			while ((r = in.readLine()) != null) {
			
				r = r.trim();
				if (r.length() == 0)
					continue;
			
				if (r.charAt(0) == ';')
					continue;
				
				if (i == NSTP)
					throw new AWException("too many stops");

				// enforce maximum stop length
				
				int ln = r.length();
				if (ln > MXSW) {
					ln = MXSW;
					r = r.substring(0,MXSW);
				}
				
				// ignore any non-alphanumeric at end of stop
				
				if (!Character.isLetterOrDigit(r.charAt(ln-1))) {
					--ln;
					r = r.substring(0,ln);
				}

				// encode for sorting
								
				char x = (char)('a' + ln);
				rec[i++] = x + TableCode.forSorting(r);
				
			}

			rec[i] = QuickSorting.HiSentinel;

			int n = i - 1;
			System.out.print(n + " stops altogether ");
			
			// sort first by length,then alphabetically
			
			QuickSorting.sort(rec,n,MXSW+1);
			System.out.println("sorted");

			// compute space required for stop table

			int lm = 0;
			for (int j = 1; j <= n; j++) {
				r = rec[j];
				int k = r.charAt(0) - 'a';
				number[k]++;

				// fill in index for stopwords shorter than
				// current length, if not already done

				while (lm < k) {
					nt += number[lm]*lm;
					index[++lm] = (short) nt;
				}
			}

			nt += number[lm]*lm;
			table = new byte[nt];

			for (lm++; lm <= MXSW + 1; lm++)
				index[lm] = (short) nt;

			// build stopword table proper

			int bs = 1;
			int ln;
			int nn;
			
			lm = 0;
			for (i = 1, nn = 0; i <= n; i++) {

				r = rec[i];
				ln = r.charAt(0) - 'a';
				if (ln > lm) {
					lm = ln;
					nn = index[ln];
					count[ln] = (short) bs;
					bs += number[ln];
				}

				// for stopwords of same length,
				// store letters at same word
				// position together

				r = r.substring(1);
				ln = r.length();
				for (int j = 0, k = 0; j < ln; j++) {
					table[nn+k] = (byte) TableCode.recode(r.charAt(j));
					k += number[ln];
				}
				nn++;
			}
			
		} catch (IOException e) {
			throw new AWException("cannot read stops: ",e);
		}
		
		System.out.println("table built with " + nt + " bytes");
		
	}
	
	// save table to output stream
	
	public void save (

		DataOutputStream out
	
	) throws AWException {
	
		save(out,nt);
		System.out.println("table saved");
			
	}
	
	// get name of table file
	
	public final String file ( ) { return TextAnalysisFile.stopFile; }
	
}
