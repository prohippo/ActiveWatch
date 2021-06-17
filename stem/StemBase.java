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
// AW file StemBase.java : 12Aug99 CPM
// definitions for morphological stemming

package stem;

import aw.AWException;
import java.io.*;

class Node extends CodedLink { // for suffix table

	static final int size = 3 + CodedLink.size;

	byte  major; // action on match
	byte  minor; // action on match
	byte  alpha; // character to match
	
}

public class StemBase {

	public static final short AST = 42; // ASCII asterisk for morphological stemmer
	public static final short VBR =124; // ASCII vertical bar
	
	public static final short MNSW =     3; // minimum stem length
	public static final byte  ENDR =    33; // ASCII exclamation point
	public static final byte  ENDS =    46; // ASCII period

	protected static final int NASQ =  127; // number of minor action codes
	protected static final int MXSQ =    6; // average number of bytes per code
	protected static final int NMAX =   46; // maximum alphabet size
	
	protected byte[]  acts = new byte[NASQ*MXSQ]; // coded action sequences
	protected short[] actp = new short[NASQ];     // index to sequences
	protected short[] actn = new short[NASQ];     // length changes for actions
 
	protected Node[]  suftbl;                 // suffix table
	protected short[] sufx = new short[NMAX]; // table index by letter

	// read suffix table from stream
		
	public void load (
		DataInputStream in
	) throws AWException {
	
		try {
				
			// read actions in
			
			in.readFully(acts);
			
			for (int i = 0; i < NASQ; i++)
				actp[i] = in.readShort();
			for (int i = 0; i < NASQ; i++)
				actn[i] = in.readShort();
			for (int i = 0; i < NMAX; i++)
				sufx[i] = in.readShort();

			// read rest of file as suffix table entries
			
			int nn = in.readInt();

			suftbl = new Node[nn];

			for (int i = 0; i < nn; i++) {
			
				suftbl[i] = new Node();
				
				// fill out table node from file
				
				suftbl[i].major = in.readByte();
				suftbl[i].minor = in.readByte();
				suftbl[i].alpha = in.readByte();
				suftbl[i].link  = in.readShort();
			}
			in.close();
			
		} catch (IOException e) {
			throw new AWException("cannot load suffix table");
		}
		
	}
	
	// write suffix table to stream
		
	public void save (
		DataOutputStream out,
		int            count
	) throws AWException {
	
		try {
				
			// write actions out
			
			out.write(acts);
			
			for (int i = 0; i < NASQ; i++)
				out.writeShort(actp[i]);
			for (int i = 0; i < NASQ; i++)
				out.writeShort(actn[i]);
			for (int i = 0; i < NMAX; i++)
				out.writeShort(sufx[i]);
				
			out.writeInt(count);

			// write out suffix table entries
			
			for (int i = 0; i < count; i++) {
				out.writeByte(suftbl[i].major);
				out.writeByte(suftbl[i].minor);
				out.writeByte(suftbl[i].alpha);
				out.writeShort(suftbl[i].link);
			}
			out.close();
			
		} catch (IOException e) {
			throw new AWException("cannot save suffix table");
		}
		
	}
	
}