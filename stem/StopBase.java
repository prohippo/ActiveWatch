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
// AW file StopBase.java : 13jul2021 CPM
// stopword support

package stem;

import aw.*;
import java.io.*;

public class StopBase {

	public static final int MXSW = 10; // maximum comparison length

	protected byte[]  table; // packed stops by length
	protected short[] index; // start of subtable for given length
	protected short[] count; // for stops accumulated by increasing length

	// initialization
	
	protected StopBase (
	
	) {
	
		index = new short[MXSW+2];
		count = new short[MXSW+2];
		table = new byte[0];

	}
	
	// load stop table from stream

	public void load (
	
		DataInputStream in
		
	) throws AWException {
	
		try {
			
			for (int i = 0; i < MXSW+2; i++)
				index[i] = in.readShort();
			for (int i = 0; i < MXSW+2; i++)
				count[i] = in.readShort();

			int ln = in.readInt();
			table = new byte[ln];
			in.readFully(table);
				
		} catch (IOException e) {
			throw new AWException("cannot load stop table");
		}
		
	}
	
	// write stopword table to stream
	
	public void save (
	
		DataOutputStream out,
		int              size
		
	) throws AWException {
	
		try {
			
			System.out.println("saving stops");
			for (int i = 0; i < MXSW+2; i++)
				 out.writeShort(index[i]);
			for (int i = 0; i < MXSW+2; i++)
				 out.writeShort(count[i]);

			out.writeInt(size);
			out.write(table,0,size);
				
		} catch (IOException e) {
			throw new AWException("cannot save stop table");
		}
	
	}

	// unit test

	public static void main ( String[] as ) {
		String dst = (as.length > 0) ? as[0] : "stps";
		StopBase sb = new StopBase();
		sb.table = new byte[100];
		System.out.println(sb.table.length);
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(dst));
			sb.save(out,10);
		} catch (IOException x) {
			System.err.println(x);
		} catch (AWException e) {
			System.err.println(e);
		}
	}
}

