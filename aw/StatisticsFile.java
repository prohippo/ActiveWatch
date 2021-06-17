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
// AW file StatisticsFile.java : 04Feb99 CPM
// access to statistics files

package aw;

import aw.AWException;
import java.io.*;

public abstract class StatisticsFile {

	protected RandomAccessFile io = null;
	protected int k = 0;
	
	public void load (
	
	) throws AWException {
		
		try {
			io = new RandomAccessFile(FileAccess.to(nameF()),"r");
			loadF();
		} catch (IOException e) {
			if (k == 0)
				return;
			throw new AWException("bad " + nameF());
		} finally {
			if (io != null)
				try {
					io.close();
				} catch (IOException e) {
				}
		}
	}

	public void save (
	
	) throws AWException {
		
		try {
			io = new RandomAccessFile(FileAccess.to(nameF()),"rw");
			saveF();
		} catch (IOException e) {
			throw new AWException("cannot save " + nameF());
		} finally {
			if (io != null)
				try {
					io.close();
				} catch (IOException e) {
				}
		}	
	}
	
	abstract void   loadF() throws IOException;
	abstract void   saveF() throws IOException;
	abstract String nameF();
	
}

