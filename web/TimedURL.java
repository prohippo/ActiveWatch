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
// AW file TimedURL.java : 02May00 CPM
// network file access with timeout to avoid hanging

package web;

import java.io.*;
import java.net.*;

// URL access with timeout (cannot subclass URL!)

public class TimedURL implements Runnable {

	private InputStream in = null; // for communication with thread
	
	private URL url; // what to access on
	
	// constructor
	
	public TimedURL (
		String urlString, // URL to access
		int    delay      // how long to try in seconds
	) throws MalformedURLException {
		url = new URL(urlString);
		ThreadTimer tm = new ThreadTimer(this,delay);
		if (!tm.run()) {
			System.err.println("timed out: " + urlString);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
	}
		
	// actual thread operation required by Runnable
	
	public void run (
	
	) {
		try {
		    Thread.yield();
			in = url.openStream();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	// simulates final URL method
	
	public final InputStream openStream (
	
	) throws IOException {
		if (in == null)
			throw new IOException("URL access failed");
		return in;
	}
	
	// cleanup
	
	public final void cleanUp ( ) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
			}
			in = null;
		}
	}
	
} 