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
// AW file Transform.java : 01Aug02 CPM
// to convert token into normalized phonetic spelling

package gram;

import aw.AWException;
import aw.Letter;
import java.io.*;

/**
 * token respelling for more accurate phonetic indexing
 */

public class Transform implements Serializable {

	private static final byte X =  -1; // special marker
	private static final byte DL=  99; // delimiter
	private static final byte NX= 127; // sentinel

	private static final int  NT=  96; // maximum number of transforms
	private static final int  LT=   8; // nominal transform length
        
        //
        // members are hidden
        //

	private short[] tx = new short[Letter.NA+1]; // transform index by first letter
	private short[] to = new short[NT];          // offsets to each transform
	private byte[]  tb = new byte[NT*LT];        // buffer for transforms
	private short ninitl; // count of anchored initial transforms
	private short ntotal; // count of all transforms
        
       /**
        * initialize transforms from text definitions
        * @param in where to get text from
        * @exception AWException on read failure
        */

	public Transform (
	
		BufferedReader in
		
	) throws AWException {
	
                int   k,n;   // miscellaneous index variables
		int  code;   // tag to sort on
		String line; // definition record

		ntotal = 0;
		
		try {

			for (short po = 0; (line = in.readLine()) != null;) {
				if (ntotal == NT)
					break;

				// check type of transform
							
				if (line.length() == 0)
					continue;
				char ch = line.charAt(0);
				if (Character.isLetter(ch))
					code = Character.toLowerCase(ch) - 'a';
				else if (ch == '.')
					code = -1;
				else
					continue;
				
				// sort by first character
                                
				for (n = ntotal; n > ninitl; --n) {
					if ((int)(tb[to[n-1]]) <= code)
						break;
					to[n] = to[n-1];
				}
				to[n] = po;

				if (code < 0)
					ninitl++;
				if (ntotal++ == NT)
					throw new AWException("too many transforms");

				// convert to internal coding

				tb[po++] = (byte) code;
                                
                                int ln = line.length();				
                                for (k = 1; k < ln && line.charAt(k) != '|'; k++)
                                        tb[po++] = Letter.toByte(line.charAt(k));

                                tb[po] = X;
                                if (k < ln) {
                                        po++;
                                        k++;
                                }

                                int bo = po;
                                for (; k < ln; k++)
                                        tb[po++] = Letter.toByte(line.charAt(k));

                                tb[po] = X;
                                int eo = po++;

				// reverse encoded substitution
				while (--eo > bo) {
					byte xb = tb[eo]; tb[eo] = tb[bo]; tb[bo++] = xb;
				}
			}

			// set up index of transforms

                        int last;
			for (last = -1, n = ninitl; n < ntotal; n++) {
				k = tb[to[n]];
				while (k > last)
					 tx[++last] = (short) n;
			}
			while (++last <= Letter.NA)
				tx[last] = ntotal;	

		} catch (IOException e) {
			;
		}
	}
	
       /**
        * apply lexical transformations to token
        * @param t buffered token
        */

	public void transform (
	
		TokenBuffer t
		
	) {
		int  ns,ne; // pattern index limits
		int  bp,bl; // buffer  indices
		int  s;     // pattern index
		int  p,po;  // token   indices
		int  n;     // pattern number
		byte k=-1;  // saved encoded character

		// set indices in token buffer

		t.buffer[t.end] = NX; // must not match anything!
		
		// start matching initial patterns as special case

		bp = t.start;
		bl = t.end;
		po = 0;
		for (ns = 0, ne = ninitl; bp <= bl; ) {
		
			for (n = ns; n < ne; n++) {

				// pattern comparison
				for (p = bp, s = to[n] + 1; t.buffer[p] == tb[s]; p++, s++);

				// substitute on full match
				if (tb[s++] == X) {
					while (tb[s] != X)
						t.buffer[--p] = tb[s++];
					bp = p;
					break;
				}
				
			}

			// if no match, move a character to output
			if (n == ne && k >= 0)
				t.buffer[po++] = k;

			// restart pattern matching from next character
			k = t.buffer[bp++];
			if (k >= Letter.NA || k < 0)
				ns = ne = 0;
			else {
				ns = tx[k];
				ne = tx[k+1];
			}
		}

		// terminate output with special character
		
		t.buffer[po] = NX;
		t.fwrd = t.start = 0;
		t.rvrs = t.end = po;
	}
        
}


