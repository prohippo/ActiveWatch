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
// AW File TextItem.java : 23jul2021 CPM
// access to text files on a network

package object;

import aw.*;
import web.*;
import java.io.*;
import java.net.*;
import java.util.*;

// low-level access to text file via URL
		
class Text implements Runnable {

	public String hd; // for item header
	public String it; // for item text
	
	public byte[] b;  // input buffer of UTF-8 bytes

	private InputStream in; // for URL
	private int k;    // current number of bytes to read
	private int l;    // current buffer offset for reading
	private int n;    // current count of byte reads
	
	private static final int urlTimeLimit  = 10; // seconds
	private static final int readTimeLimit = 20; // seconds

	// create empty text on error
	
	Text (
	) {
		it = null;
	}
	
	// get source text file
	
	Text (
		
		String su, // URL
		Index  ix  // item index
	
	) throws IOException {
	
		// load entire item within specified time limits
		
		try {
			//// System.out.println("os= " + ix.os);

			TimedURL u = new TimedURL(su,urlTimeLimit);
			in = u.openStream();
			in.skip(ix.os);
			ThreadTimer reading = new ThreadTimer(this,readTimeLimit);
			l = 0;
			int m = ix.hs + ix.tl;

			//// System.out.println("header + text= " + m + " bytes");

			b = new byte[m];
			for (k = m; k > 0; ) {
				if (!reading.run())
					break;
				k -= n;
				l += n;
			}
			in.close();

			// bytes will be UTF-8 and must be converted 16-bit Unicode

			hd = (ix.hs > 0) ? new String(Arrays.copyOfRange(b,0,ix.hs)) : "";
			it = new String(Arrays.copyOfRange(b,ix.hs,m));

			//// System.out.println("it= [" + it + "]");

			in = null;
			b  = null;
		} catch (IOException e) {
			String es = (in == null) ? "bad url" : "cannot read";
			System.err.println("item: " + su + " (" + es + ")");
			if (in == null)
				throw e;
			else {
				System.err.println(e);
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}
		
	}
	
	// define thread for reading into buffer
	
	public void run (
	
	) {
		try {
			n = in.read(b,l,k);
		} catch (IOException e) {
			System.err.println(e);
			n = -1;
		}
	}
	
}

// user-level access to source text items by number

public class TextItem {

	private static Index  ix; // for access to source text
	private static Source sr;
	private static Text   tx;
	 
	private static int bns = -1; // saved batch number
	private static int ins = -1; // saved item number
	private static int ids = -1; // saved source index number
	
	private static final String msg = "** NO ITEM **";
	private static final byte NL = 10; // UTF-8 new line
	private static final byte CR = 13; // UTF-8 carriage return
 
	// get specified item
		
	public TextItem (
	
		int bn, // batch of target item
		int in  // item number in batch
		
	) {
		int id;
		
		try {
			ins = in;
			ix = new Index(bn,in);
			id = ix.si;
			if (bns != bn || ids != id) {
				sr = new Source(bn,id);
				bns = bn;
				ids = id;
			}
			tx = new Text(sr.getName(),ix);
		} catch (IOException e) {
			System.err.println("cannot access text " + bn + ":" + in);
			tx = new Text();
		}
	}

	// get header and body of text item
	
	public final String getFullText (
	
	) {
		return tx.hd + tx.it;
	}

	// get header of text item
		
	public final String getHeader (
	
	) {
		return tx.hd;
	}
	
	// get body of text item
		
	public final String getBody (
	
	) {
		return (tx.it != null) ? tx.it : "";
	}
	
	// extract line
	
	private static int LN = 144;

	public static String getLine (
		String s
	) {
		int ln = s.length();
		if (ln > LN)
			ln = LN;
		int n;
		for (n = 0; n < ln; n++)
			if (s.charAt(n) == '\r' || s.charAt(n) == '\n')
				break;
		return s.substring(0,n);
	}

	// get subject line of text item
			
	public final String getSubject (
	
	) {
		int ns = ix.sj - ix.os;
		int i = ns;
		for (; i < tx.b.length; i++)
			if (tx.b[i] == NL || tx.b[i] == CR)
				break;
		if (i == tx.b.length)
			return msg;
		else
			return new String(Arrays.copyOfRange(tx.b,ns,i));
	}
	
	// get source file
	
	public final String getSource (
	
	) {
		return sr.getName();
	}

	// clean up
	
	public void close (
		
	) {
		ix.close();
		sr.close();
		bns = ids = -1;
	}
	
}
