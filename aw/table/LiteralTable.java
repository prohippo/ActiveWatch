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
// LiteralTable.java : 20aug2021 CPM
// compile literal table from listing of patterns

package aw.table;

import aw.*;
import gram.*;
import object.QuickSorting;
import java.io.*;

public class LiteralTable extends LiteralBase implements TableBuilder {

	private String[] rec; // literal patterns from file
	
	// define literal indices from a pattern file

	public void build (
	
		BufferedReader in
		
	) throws AWException {
		
		int  i,j,k;
		int  n = 0; // number of literal patterns

		int     nb; // buffer allocation offset
		int    snb; // start of allocation for literal
		int     bl; // buffer limit
		
		String r;   // next literal
		String p;   // auxiliary

		// load all literals for sorting in special order

		rec = new String[Gram.NLIT+2];
		rec[n++] = QuickSorting.LoSentinel;

		try {
		
			while ((r = in.readLine()) != null) {
		
				// next literal entry
			
				r = r.trim().toLowerCase();
				k = r.length();
				if (k <= 2)
					continue;
				if (r.charAt(0) == ';')
					continue;

				// encode temporarily for proper sorting
			
				String xr = "", ry = "";
				if (r.charAt(0) == '-') {
					StringBuffer rb = new StringBuffer(r.substring(1));
					rb.reverse();
					r = rb.toString();
					if (r.charAt(0) == '-')
						throw new AWException("literal cannot have form -WXYZ-");
					xr = "-";
				}
				else
					ry = "-";  // unmarked literal assumed to be leading type
			
				if (n > Gram.NLIT)
					throw new AWException("too many records");

				// this restores leading and trailing hyphens in a record

				rec[n++] = xr + TableCode.forSorting(r) + ry;
				
			}
			
		} catch (IOException e) {
			throw new AWException("cannot read patterns: ",e);
		}
		
		rec[n] = QuickSorting.HiSentinel;
 
		// sort literals by internal coding

		System.out.print(--n + " literals altogether out of " + Gram.NLIT);
 		QuickSorting.sort(rec,n,MXL+1);
		System.out.println(" sorted");

		// store trailing literals

		for (i = nb = 1, j = k = 0; rec[i].charAt(0) == '-' && i <= n; i++) {
			r = rec[i];
			snb = nb;
			nb = TableCode.forStoring(r.substring(2),lita,nb);
			char x = TableCode.recode(r.charAt(1));
			int jx = Letter.toByte(x);
			while (j <= jx)
				trsx[j++] = (short) k;

			litx[k++] = (short) snb;
			lita[nb++] = LiteralBase.TM;
		}
		
		while (j <= Letter.NAN)
			trsx[j++] = (short) k;
		lita[nb] = LiteralBase.TM;

		// store leading literals

		for (j = 0; i <= n; i++) {
			r = rec[i];
			int ll = r.length() - 1;
			if (r.charAt(ll) == '-');
				r = r.substring(0,ll);
			snb = nb;
			nb = TableCode.forStoring(r.substring(1),lita,nb);
			char x = TableCode.recode(r.charAt(0));
			int jx = Letter.toByte(x);
			while (j <= jx)
				ldsx[j++] = (short) k;

			litx[k++] = (short) snb;
			lita[nb++] = LiteralBase.TM;
		}
		
		while (j <= Letter.NAN)
			ldsx[j++] = (short) k;

		System.out.println(nb + " out of " + lita.length + " characters in table");
		System.out.println("table saved");
		
	}
	
	public final String file ( ) { return TextAnalysisFile.literalFile; }
	
}
