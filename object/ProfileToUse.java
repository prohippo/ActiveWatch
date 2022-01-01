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
// ProfileToUse.java : 01Jun00 CPM
// support alternate I/O

package object;

import aw.AWException;
import aw.Profile;
import java.io.*;

public class ProfileToUse extends Profile {

	public static final String name = "profile";
	
	public ProfileToUse ( ) { }

	public ProfileToUse ( String file ) throws AWException {
		load(file);
	}
	
	public ProfileToUse ( int n ) throws AWException {
		super();
		if (n == 0)
			load(name);
		else
			load(n);
	}
	
	public ProfileToUse ( Profile pp ) {
		super();
		nhth = pp.nhth;
		shth = pp.shth;
		sgth = pp.sgth;
		uexp = pp.uexp;
		uvar = pp.uvar;
		gms  = pp.gms;
		wts  = pp.wts;
		trc  = pp.trc;
	}
	
	public final void save ( String file ) throws AWException {
		try {
			io = new RandomAccessFile(file,"rw");
			saveF();
			io.close();
		} catch (IOException ioe) {
			throw new AWException(ioe);
		}
	}
	
	public final void load ( String file ) throws AWException {
		try {
			io = new RandomAccessFile(file,"r");
			loadF();
			io.close();
		} catch (IOException ioe) {
			throw new AWException(ioe);
		}
	}
	
}