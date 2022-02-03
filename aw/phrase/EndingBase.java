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
// AW file EndingBase.java : 11Sep00 CPM
// for keeping table of endings associated with parts of speech

package aw.phrase;

import aw.AWException;
import aw.Letter;
import stem.CodedLink;
import java.io.*;

class Node extends CodedLink {

	public static final int size = 2 + SyntaxSpec.size + CodedLink.size;

	// instance variables
	
	public byte alpha;        // character to match
	public byte constraint;   // added conditions on match
	public SyntaxSpec syntax; // syntax associated with match
	
	public Node ( ) {
		syntax = new SyntaxSpec();
	}
	
	public final void read ( DataInputStream in ) throws IOException {
		alpha = in.readByte();
		constraint = in.readByte();
		syntax.read(in);
		link = in.readShort();
	}
	
	public final void write ( DataOutputStream out ) throws IOException {
		out.writeByte(alpha);
		out.writeByte(constraint);
		syntax.write(out);
		out.writeShort(link);
	}
	
}

public class EndingBase {
	
	public static final String file = "endss"; // word endings file
	
	public static final byte Empty = -1; // value for empty end tree index

	protected Node[]  endtbl; // ending table
	protected short[] endx = new short[Letter.NA]; // table index by letter

	// definition of vowel (includes Y)
	
	public static boolean VOWEL ( char x ) {
		int cb = Letter.toByte(x);
		return (cb >= 0) ? !Letter.cnx[cb] : false;
	}

	// create empty table

	protected EndingBase (
		int n
	) {
		clear();
		endtbl = new Node[n];
		for (int i = 0; i < n; i++) {
			Node node = new Node();
			endtbl[i] = node;
		}
	}
	
	// set table for loading
	
	public EndingBase (
	
	) {
	}
	
	// clear out
	
	public void clear (
	
	) {
		for (int i = 0; i < Letter.NA; i++)
			endx[i] = Empty;
	}
	
	// read suffix table from stream
		
	public void load (
		DataInputStream in
	) throws IOException {
		
		for (int i = 0; i < Letter.NA; i++)
			endx[i] = in.readShort();

		int nn = in.readInt();

		endtbl = new Node[nn];

		for (int i = 0; i < nn; i++) {
		
			// fill out table node from file
			
			endtbl[i] = new Node();
			endtbl[i].read(in);
			
		}
		in.close();
			
	}
	
	// write ending table to stream
		
	public void save (
		DataOutputStream out,
		int              count
	) throws IOException {
	
		for (int i = 0; i < Letter.NA; i++)
			out.writeShort(endx[i]);

		// write out endings
		
		out.writeInt(count);
		
		for (int i = 0; i < count; i++)
			endtbl[i].write(out);

		out.close();
		
	}
	
}