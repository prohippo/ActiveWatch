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
// AW File Stem.java : 15sep2021 CPM
// morphological stemmer

package stem;

import aw.*;
import java.io.*;

// for stemming stack

class StemFrame {

	byte    mjr; // action code
	byte    mnr; // action code
	byte    len; // length remaining
	boolean flg; // recursion flag
	
}

// classs for stemming

public class Stem extends StemBase {

	private StemFrame[] stk;
	
	// for subclassing by table building class
	
	protected Stem (
	
	) {
	}
	
	// constructor to load
	
	public Stem (
		DataInputStream in
	) throws AWException {
		this();
		load(in);
		stk = new StemFrame[Token.MXW+1];
		for (int i = 0; i < stk.length; i++)
			stk[i] = new StemFrame();
	}
	
	// actual stemming method
	
	public int stem (
		Token t
	) {
		int  stkn; // for internal stack
		int  p;
		int  k,n;

		int ln = t.length();
		if (ln == 0)
			return 0;
			
		stk[0].mjr = stk[0].mnr = 0;
		stk[0].flg = false;

		// recursion loop for multiple suffixes
		
		do {

			// initialize stack for any failure
			
			stk[0].len = (byte) ln;
			stkn = 1;

			// get initial entry into suffix table
			
			n = t.array[--ln];
			if (n < Letter.NA && sufx[n] >= 0) {
				p = sufx[n];

				// search suffix tree
				
				while (ln >= 0) {
					if (t.array[ln] == suftbl[p].alpha) {
						if (suftbl[p].major != 0 || suftbl[p].minor != 0) {
							stk[stkn].mjr = suftbl[p].major;
							stk[stkn].mnr = suftbl[p].minor;
							stk[stkn].flg = false;
							stk[stkn++].len = (byte) ln;
						}
						if (suftbl[p].isLeftEnd())
							break;
						--ln;
						p++;
					}
					else {
						if (suftbl[p].alpha == AST && ln >= 0) {
							stk[stkn].mjr = suftbl[p].major;
							stk[stkn].mnr = suftbl[p].minor;
							stk[stkn].flg = true;
							stk[stkn++].len = (byte)(ln + 1);
						}

						// continue suffix matching
						
						int m = suftbl[p].getLink();
						if (CodedLink.isNull(m))
							break;
						p = m;
					}
				}

				// check for condition of matching entire word
				
				if (suftbl[p].alpha == VBR && ln < 0) {
					stk[stkn].flg = false;
					stk[stkn].mjr = suftbl[p].major;
					stk[stkn].mnr = suftbl[p].minor;
					stk[stkn++].len = 0;
				}
			}

			// look for longest applicable suffix
			
			do {
				ln = stk[--stkn].len;
				k = evalcd(stk[stkn].mjr,stk[stkn].mnr,ln,t,stk[0].len);
			} while (k < 0);
			ln = k;

		} while (stk[stkn].flg);

		return ln;
	}

	// check applicability of action codes
	// and return a positive value if so

	private int evalcd (
		int	mjr, // major action code
		int	mnr, // minor action code
		int	lng, // current token length
		Token t, // token string
		int fail // length to return
	) {
		byte x=0;
		byte k;
		int ap;  // action sequence pointer

		// apply major action to word

		if (lng > 0)
			x = t.array[lng - 1];
		else if (mjr > 1)
			return -1;

		if (mjr == 0)
			return fail;
		else if (mjr == 1
			  || mjr == 2 && Letter.cns[x]
			  || mjr == 3 && Letter.spc[x])
			  ;
		else
			return -1;

		if (actn[mnr] + lng < MNSW) return -1;

		// apply minor action to word

		ap = actp[mnr];
		lng += acts[ap++];
		t.setLength(lng);

		while ((k = acts[ap++]) < Letter.NA)
			t.append(k);
			
		if (k == ENDR)
			Inflex.reflex(t,0);

		// return modified length
		
		return t.length();
	}

}
