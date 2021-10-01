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
// AW file SuffixTable.java : 30sep2021 CPM
// compile suffix table from listing of patterns

package aw.table;

import aw.*;
import stem.*;
import java.io.*;
import object.QuickSorting;

public class SuffixTable extends StemExtension {

	private static final int SFXLEN = 16;      // maximum suffix length
	private static final int NSFXND = 6500;    // maximum number of nodes
	private static final int NSFX   = 2500;    // maximum suffix count

	private int free; // next free node

	private String[] rec = new String[NSFX+2]; // input suffix records

	// build a loadable suffix table from a listing of
	// word endings and action definitions

	public SuffixTable (

	) {
		super(NSFXND);
	}

	public void build (

		BufferedReader ina, // suffix rules from file
		BufferedReader ins  // actions

	) throws AWException {

		char  x=0;
		short i,k;
		String  r;

		System.out.println("getting actions");

		// define actions for match from file

		for (k = i = 0; i < NASQ; ) {

			try {
				if ((r = ina.readLine()) == null)
					break;
			} catch (IOException e) {
				throw new AWException("cannot read action: ",e);
			}

			// retention count

			r = r.trim();
			if (r.length() < 2 || r.charAt(0) == ';')
				continue;
			if (!Character.isDigit(r.charAt(0)))
				throw new AWException("no retention count: " + r);

			actp[i] = k;
			int nr = r.charAt(0) - '0';
			acts[k++] = (byte) nr;
			r = r.substring(1);

			// restored characters

			int n;

			for (n = 0;; n++) {
				x = r.charAt(n);
				if (!Character.isLetter(x))
					break;
				acts[k++] = Letter.toByte(x);
			}
			actn[i++] = (short)(nr + n);
			acts[k++] = (x == '!') ? StemBase.ENDR : StemBase.ENDS;
		}

		// pad out rest of actions

		acts[k]   = 0;
		acts[k+1] = StemBase.ENDS;
		while (i < NASQ)
			actp[i++] = (short) k;
 
		// load suffixes

		StringBuffer suf;
		String rs;
		int m,n;

		System.out.println("getting suffixes");

		// read suffixes plus actions on match

		i = 0;
		rec[i++] = QuickSorting.LoSentinel;

		for (;;) {

			try {
				if ((rs = ins.readLine()) == null)
					break;
			} catch (IOException e) {
				break;
			}

			r = rs = rs.trim();
			if (r.length() < 2 || r.charAt(0) == ';')
				continue;

			// suffix pattern

			for (m = 0; !Character.isWhitespace(r.charAt(m)); m++);
			suf = new StringBuffer(r.substring(0,m));

			// contextual condition on match

			r = r.substring(m).trim();
			for (m = 0; Character.isDigit(r.charAt(m)); m++);
			if (m == 0)
				throw new AWException("no contextual condition: " + rs);
			int mj = Integer.parseInt(r.substring(0,m));

			// final action on match

			r = r.substring(m).trim();
			for (m = 0; m < r.length(); m++)
				if (!Character.isDigit(r.charAt(m)))
					break;
			if (m == 0)
				throw new AWException("no final action: " + rs);
			int mn = Integer.parseInt(r.substring(0,m));

			// check for valid suffix pattern

			x = suf.charAt(0);
			if (x == '|')
				suf.setCharAt(0,'!'); // done for sorting
			else if (!Character.isLetter(x) && x != '*')
				throw new AWException("missing suffix=<" + suf + ">");
 
			if (mj > 3 || mn >= NASQ)
				throw new AWException("bad action code= " + rs);

			if (i > NSFX)
				throw new AWException("too many suffixes");

			rec[i++] = suf.reverse().toString().toUpperCase() + " " + mj + mn;

		}

		// sort reversed suffixes alphabetically

		n = i - 1;
		rec[i] = QuickSorting.HiSentinel;

		System.out.print("sorting ");

		QuickSorting.sort(rec,n,SFXLEN);

		System.out.println("done");

		// build a suffix tree, with separate subtrees
		// indexed by array sufx for each possible final
		// character in a suffix

		free = 0;
		for (i = 1; i <= n; i++) {
			r = rec[i];
			x = r.charAt(0);

			// start from last letter in suffix

			if (!Character.isLetter(x))
				throw new AWException("illegal suffix: " + r);

			int ip = x - 'A';

			// add suffix table index if needed

			if (sufx[ip] < 0) {
				if (free > NSFXND)
					throw new AWException("suffix overflow on " + r);
				sufx[ip] = (short) free;
				putAlpha(free,ip);
				stopLinkSequence(free++);
			}

			// find where to insert suffix in tree

			short np = sufx[ip];
			for (;;) {
				if (r.length() == 0)
					throw new AWException("bad suffix: " + rec[i]);
				x = r.charAt(0);
				if (Character.isWhitespace(x))
					break;
				int d = x - 'A';
				if (d == getAlpha(np)) {
					r = r.substring(1);
					if (!isLinkSequenceEnd(np))
						np++;
					else {
						extendLinkSequence(np);
						x = r.charAt(0);
						break;
					}
				}
				else if (!CodedLink.isNull(k = getLink(np)))
					np = k;
				else {
					branchLink(np,free);
					break;
				}
			}

			// add new tree nodes as needed

			while (Character.isLetter(x)) {
				putAlpha(free++,x - 'A');
				r = r.substring(1);
				x = r.charAt(0);
			}
			if (x == '!') {
				putAlpha(free++,StemBase.VBR);
				r = r.substring(1);
			}
			else if (x == '*') {
				putAlpha(free++,StemBase.AST);
				r = r.substring(1);
			}

			r = r.substring(1);
			int major = r.charAt(0) - '0';
			int minor = Integer.parseInt(r.substring(1));
			putAction(free-1,major,minor);
			stopLinkSequence(free-1);
		}

		System.out.println("table built with " + free + " nodes");
	}

	public void save (

		DataOutputStream out

	) throws AWException {
		save(out,free);
		System.out.println("table saved");
	}

}
