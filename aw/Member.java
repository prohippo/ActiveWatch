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
// AW file Member.java : 03Mar98 CPM
// access to cluster seed file

package aw;

import java.io.*;

public class Member {

	static final String file = "clusters"; // file name

	public short index;    // link index of cluster seed member
	public short strength; // how densely linked to other members in seed
	
	private static DataInputStream  in;
	private static DataOutputStream out;

	// define a new member record
	
	public Member (
		int inx,
		int str
	) throws IOException {
		index    = (short) inx;
		strength = (short) str;
		save();
	}
	
	// for special case of no member records
	
	public Member (
		Object x
	) throws IOException {
		if (out == null)
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
	}

	// get a member record from file of cluster seeds
		
	public Member (
	) throws IOException {
		load();
	}

	// to read a member record
		
	public void load (
	) throws IOException {
		if (in == null)
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		index    = in.readShort();
		strength = in.readShort();
	}
		
	// to write a member record
		
	public void save (
	) throws IOException {
		if (out == null)
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		out.writeShort(index);
		out.writeShort(strength);
	}

	// close cluster seed file
		
	public void close (
	) {
		try {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			in  = null;
			out = null;
		} catch (IOException e) {
		}
	}
	
}
