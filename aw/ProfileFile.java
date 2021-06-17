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
// AW file ProfileFile.java : 04Feb99 CPM
// access to random access files keyed by profile

package aw;

import java.io.*;

public abstract class ProfileFile {

	protected RandomAccessFile io; // for reading and writing

	// get access to file
		
	public ProfileFile (
	) {
	}
	
	// go to record for specified profile
		
	private void seek (
		int pn
	) throws IOException {
		if (io == null)
			io = new RandomAccessFile(FileAccess.to(nameF()),"rw");
		io.seek((pn - 1)*sizeF());
	}

	// save record for specified profile number
		
	public void save (
		int pn
	) throws AWException {
	
		try {
			seek(pn);
			saveF();
		} catch (IOException e) {
			throw new AWException("profile " + pn +": " + e);
		}
		
	}

	// load record for specified profile number
		
	public void load (
		int pn
	) throws AWException {
	
		try {
			seek(pn);
			loadF();
		} catch (IOException e) {
			throw new AWException("profile " + pn +": " + e);
		}
		
	}
	
	// close I/O file
	
	public void close (
	) {
		try {
			if (io != null) {
				io.close();
				io = null;
			}
		} catch (IOException e) {
		}
	}
	
	// required from subclasses

	          abstract String nameF ( );
	          abstract int    sizeF ( );
	protected abstract void   loadF ( ) throws IOException;
	protected abstract void   saveF ( ) throws IOException;
	
}

