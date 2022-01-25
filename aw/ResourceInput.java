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
// AW file ResourceInput.java : 24jan2022 CPM
// for transparent support of jarred applications
//
// for optional packaging of AW tables into a .jar file as resources

package aw;

import java.io.*;
import java.net.*;

public class ResourceInput {

	static public DataInputStream openStream (
		String file
	) throws IOException {
	
		InputStream stream = open(file);
		if (stream == null)
			stream = new FileInputStream(FileAccess.to(file));
		return new DataInputStream(new BufferedInputStream(stream));
		
	}
	
	static public BufferedReader openReader (
		String file
	) throws IOException {
	
		Reader r;
		InputStream stream = open(file);
		if (stream != null)
			r = new InputStreamReader(stream);
		else
			r = new FileReader(FileAccess.to(file));
		return new BufferedReader(r);
		
	}
	
	static private InputStream open (
		String file
	) {
		return ResourceInput.class.getResourceAsStream("/" + file);
	}
	
}
