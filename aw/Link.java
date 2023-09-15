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
// AW file Link.java : 02sep2023 CPM
// class for cluster links with I/O

package aw;

import java.io.*;

public class Link {

	public static final String file      = "links";      // default file name
	public static final String duplicate = "duplicates"; // for any duplicates

	public static final int size = 8; // link record size

	private static DataInputStream    in;
	private static DataOutputStream  out;
    
	private static InputStream  inputs;
	private static OutputStream outputs;
    
	public static void setInput  ( InputStream  input  ) { inputs  = input; }
	public static void setOutput ( OutputStream output ) { outputs = output; }

	public static final short MXTC =  16384; // maximum number of items to cluster

	public static final int   MXML = 350000; // maximum link count

	public short  from; // start of link
	public short    to; // end   of link
	public float value; // link significance score

	// close all files (redundant for robustness)

	public static void close (

	) throws AWException {
		try {
			if (in  != null)
				in.close();
			if (out != null)
				out.close();
			in  = null;
			out = null;
		} catch (IOException e) {
			throw new AWException("bad link file",e);
		}
	}

	// create a new link with specified entries

	public Link (
		short from,
		short to,
		float value
	) {
		set(from,to,value);
	}

	// set fields of link

	public void set (
		short from,
		short to,
		float value
	) {
		this.from  = from;
		this.to    = to;
		this.value = value;
	}

	// create empty link

	public Link (

	) throws AWException {

	}

	// read link record

	public void load (

	) throws AWException {
		try {
			if (in == null)
                if (inputs == null)
                    in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                else
                    in = new DataInputStream(inputs);
			if (!load(in)) {
				in.close();
				in = null;
			}
		} catch (IOException e) {
			throw new AWException("no links",e);
		}
	}

	// read link record from stream with automatic closing

	public boolean load (
		DataInput input
	) throws AWException {
		try {
			from  = input.readShort();
			to    = input.readShort();
			value = input.readFloat();
			return (from <= MXTC);
		} catch (IOException e) {
			throw new AWException("link read",e);
		}
	}

	// write link record with automatic closing

	public void save (

	) throws AWException {
		try {
			if (out == null)
                if (outputs == null)
                    out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                else
                    out = new DataOutputStream(outputs);
			if (!save(out)) {
				out.close();
				out = null;
			}
		} catch (IOException e) {
			throw new AWException("cannot save links",e);
		}
	}

	// write link record to stream

	public boolean save (
		DataOutput output
	) throws AWException {
		try {
			output.writeShort(from);
			output.writeShort(to);
			output.writeFloat(value);
			return (from <= MXTC);
		} catch (IOException e) {
			throw new AWException("cannot write link",e);
		}
	}

}

