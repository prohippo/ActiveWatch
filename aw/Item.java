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
// AW file Item.java : 21Mar98 CPM
// basic reference to text subsegment

package aw;

import java.io.*;

public class Item {

	public  static final int  size =  8; // record size
	public  static final int scale = 16; // for scaling score
	
	private static final short MAX = 32767; // maximum scaled score

	public short bn; // batch number
	public short sg; // significance score
	public int   xn; // item index
	
	// constructor for subclassing
	
	protected Item (
	
	) {
	}

	// compose record from values
		
	public Item (
		
		int    b, // batch
		int    n, // index
		double s  // score
		
	) {
		int x = (int) (s*scale);
		if (x > MAX)
			x = MAX;
	
		bn = (short) b;
		sg = (short) x;
		xn = n;
		
	}

	// get record from file
		
	public Item (
	
		DataInput in
		
	) throws IOException {
	
		load(in);
		
	}

	// read record
		
	public void load (
	
		DataInput in
		
	) throws IOException {
	
		bn = in.readShort();
		sg = in.readShort();
		xn = in.readInt();
		
	}

	// write record
		
	public void save (
	
		DataOutput out
		
	) throws IOException {
	
		out.writeShort(bn);
		out.writeShort(sg);
		out.writeInt(xn);
		
	}

	// accessors
		
	public final int batch ( ) { return bn; }
	
	public final int index ( ) { return xn; }
	
	public final double score ( ) { return ((double) sg)/scale; }

}