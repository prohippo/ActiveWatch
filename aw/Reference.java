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
// AW file Reference.java : 09Aug01 CPM
// convert references to Item instance

package aw;

import java.io.*;

public class Reference {

	private static final String x = "0::";
	private static String  prefix = x;
	private static int ns = 0;

	// handles expressions of the form m, b:k, and b::m
	
	public static Item to (
		String r
	) throws AWException {
		int b = 0;
		int m = 0;
		
		int n = r.indexOf(':');
		if (n >= 0) {
		
			// if complete form, save prefix
			
			int p = n + 1;
			if (r.charAt(p) == ':')
				p++;
			prefix = r.substring(0,p);
			
		}
		else {
		
			// if incomplete, attach last prefix
			
			r = prefix + r;
			n = r.indexOf(':');
			
		}

		// interpret item reference
		
		b = Integer.parseInt(r.substring(0,n));
		r = r.substring(n + 1);
		try {
			if (r.charAt(0) == ':') {
				m  = Integer.parseInt(r.substring(1));
				ns = 1;
			}
			else {
				int k = Integer.parseInt(r);
				Index ix = new Index(b,k);
				m  = ix.sx;
				ns = ix.ns;
			}
		} catch (IOException e) {
		    System.err.println("no index for " + prefix + r);
			throw new AWException(e);
		}

        // return item for it
        
		return new Item(b,m,1);
		
	}
	
	// return segment count
	
	public static int count ( ) { return ns; }

	// set prefix to specific string
		
	public static void set (
		String s
	) {
		prefix = s;
	}

	// set prefix back to default
		
	public static void reset (
	
	) {
		prefix = x;
	}
	
} 