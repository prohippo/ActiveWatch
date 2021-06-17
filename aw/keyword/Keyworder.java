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
// AW file Keyworder.java : 27Oct99 CPM
// derive descriptive keys for clusters

package aw.keyword;

import aw.*;
import object.*;
import java.io.*;

// obtain keys for all current clusters
// based on currently assigned text items

public class Keyworder {

	private static final int MAXN = 48; // maximum number of keys to select
	private static final int MLM  =  3; // minimum item count for keys

	private Map map = new Map();
	private KeyDerivation kdr;
	
	private int count; // number of distinct keys ranked
	
	// initialization
		
	public Keyworder (
		KeyDerivation kdr
	) {
		this.kdr = kdr;
	}

	// set up sequence file
		
	public void run (
	
		int nmpr  // how many items to process
		
	) throws AWException {

		FullProfile profr = null; // profile expansion
		Attribute   attrs = null; // profile attributes
		Item[] its; // item list
		Item   it;  // item
		
		GappedProfileList ls;
		
		System.out.println("deriving descriptive keys");

		int nm;     // item count for cluster
		int cn,cno; // cluster numbers
		String  fx; // for formatting
		
		byte[]  pv; // expanded vector of profile weights
			
		int    b,k; // item reference
		
		for (cn = cno = 1; cn <= Map.MXSP; cn++) {
 
			if (!map.defined(cn) || map.userType(cn))
				continue;
				
			fx = " " + cno++;
			System.out.print("------- cluster" + fx);
			for (k = fx.length(); k < 4; k++)
				System.out.print(" ");
			System.out.println("	  (@ " + cn + ")");

			// read in cluster list

			ls = new GappedProfileList(cn);
			nm = ls.getCount();
			if (nm == 0) {
				System.out.println("no items listed");
				continue;
			}
			
			// read in cluster profile and attributes

			profr = new FullProfile(cn);	
			attrs = new Attribute(cn);

			// expand profile to full vector

			if (profr.count() == 0) {
				System.out.println("null profile");
				continue;
			}

			pv = profr.vector();
			
			// select assigned items with highest scores

			count = 0;
 
 			int nmg = ls.gap;
			int mlm = nm < nmpr ? nm : nmpr;
			if (mlm > nmg)
				mlm = nmg;
			if (mlm < MLM)
				mlm = MLM;
			if (mlm > nm)
				mlm = nm;
			System.out.println("get keys from top " + mlm + " items out of " + nm);
			
			// extract keys from selected items
			
			its = ls.getList();
			
			kdr.set(pv,its,mlm,MAXN);
			
			for (int i = 0; i < mlm; i++)
				System.out.print(its[i].bn + "::" + its[i].xn + " ");
 
			// append keys to attributes

			count = kdr.count();
			System.out.println("\nkeys selected from " + count + " candidates");

			if (count > 0)
				store(attrs);
			
			attrs.save(cn);
			
		}
		
		// close out objects
		
		if (profr != null) {
			profr.close();
			attrs.close();
			kdr.close();
		}
		
		System.out.println(--cno + " clusters described");
		
	}

	// print key in fixed-width field
	
	private static final int M = 18; // output field width
	
	private final void print (
	
		String w
		
	) {
	
		System.out.print(Format.it(w,M));
		
	}
		
	// append ranked keys to attributes record

	private void store (
		
		Attribute at
		
	) throws AWException {

		int    n;
		String x;		
		String w;
		
		// check for valid attribute keys
		
		String p = new String(at.kys,0);
		
		int sp = p.indexOf('|');
		if (sp < 0)
			throw new AWException("bad keys");

		// create buffer for new keys with old user added keys
		
		String ps = p.substring(0,sp);
		
		StringBuffer pb = new StringBuffer(ps);
		pb.append("|");

		int pn = sp + 1;
		int pl = Attribute.PKWL - 1;
		
		for (n = 0; (w = kdr.next()) != null; ) {

			// get selected keys in order of ranking
						
			if (ps.indexOf(w) >= 0)
				continue;
			if ((n++)%4 == 0)
				System.out.println("");

			// send key to standard output with formatting
						
			print(w);

			// enough room in buffer for key?
			
			int m = w.length();				
			if (pn + m > pl)
				break;
				
			pb.append(w);
			pb.append(' ');
			pn += m + 1;
			
		}
		
		if (n > 0)
			System.out.println("");

		// pad rest of buffer with blanks
					
		for (int k = pb.length(); k < Attribute.PKWL; k++)
			pb.append(' ');

		// store in attribute record
					
		String s = pb.toString();
		s.getBytes(0,Attribute.PKWL,at.kys,0);

		// clear out rankings
					
		if (n > 0) {
			System.out.println("\n  -other keys-");
			for (int i = 0; (w = kdr.next()) != null; i++) {
				if (i%4 == 0)
					System.out.println("");
				print(w);
			}
			System.out.println("");
		}

	}

}
