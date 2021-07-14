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
// AW file IndexVector.java : 13jul2021 CPM
// n-gram index vector for text item

package object;

import aw.*;
import java.io.*;

// basic AW n-gram index vector with special I/O

public class IndexVector extends SimpleIndexVector {

	// buffer for actual compressed vector
	
	protected Vector vc; // vector object for I/O
	protected Offset vo; // offset object for I/O

	// create a compressed vector from a byte array
	
	public IndexVector (
	
		byte[] v,     // array
		int    mvsum  // minimum vector sum
		
	) throws AWException {
		super(v,mvsum);

		// create the vector object
		
		vc = new Vector(bb,compressedLength());
	}

	// load an index vector from a file
		
	public IndexVector (
	
		int bn,
		int in
		
	) throws AWException {
		super();
		Offset vn;
		try {
			vo = new Offset(bn,in);
			vn = new Offset(bn,in+1);
			vc = new Vector(bn,vo.offset,vn.offset - vo.offset);
			bb = vc.vb;
		} catch (IOException e) {
			throw new AWException("cannot read vector: ",e);
		}
	}
        
        // needed for subclassing
        
        public IndexVector ( ) { }
	
	// direct access to vector
	
	public final Vector getVector ( ) { return vc; }
	
	// direct access to offset
	
	public final Offset getOffset ( ) { return vo; }

	// append an index vector to a numbered batch file
	
	public void save (
	
		int bn
		
	) throws AWException {
		try {
			vc.save(bn);
			vo = new Offset(vc.position());
			vo.save(bn);			
		} catch (IOException e) {
			throw new AWException("cannot write vector: ",e);
		}
	}
	
	// add to an arbitrary file
	
	public int save (
	
		DataOutput out
	
	) throws IOException {
		return vc.save(out);
	}

	// close vector and offset files
	
	public void close (
	
	) {
		if (vo != null) {
			vc.close();
			vo.close();
			vo = null;
		}
	}

}
