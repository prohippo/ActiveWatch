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
// AW file VectorsForInnerProducts.java : 10Apr99 CPM
// access to model and squeezed vectors for inner products

package object;

import aw.*;
import java.io.*;

// precomputed model plus squeezed vectors

public class VectorsForInnerProducts {

	private static final String file = "svectors";

	public PairwiseModel model;
	public int           count;
	public IndexVectors    ivs;

	// create from existing model and vectors
	
	public VectorsForInnerProducts (
		PairwiseModel md,
		int           ct,
		IndexVectors  iv
	) {
		model = md;
		count = ct;
		ivs = iv;
	}

	// get model and vectors from input stream
		
	public VectorsForInnerProducts (
	
	) throws AWException {
		try {
			load();
		} catch (IOException e) {
			throw new AWException("cannot load squeezed vectors: ",e);
		}
	}

	// method to read model and vectors
		
	public void load (
	
	) throws IOException, AWException {
		File f = new File(FileAccess.to(file));
		FileInputStream fi = new FileInputStream(f);
		DataInputStream in = new DataInputStream(new BufferedInputStream(fi));
		model = new PairwiseModel(in);
		count = in.readInt();
		int ln = (int)f.length() - PairwiseModel.size - 4;
		ivs = new IndexVectors(in,ln);
		ivs.NX = Parameter.NLX;
		in.close();
	}

	// method to write model and vectors
		
	public void save (
	
	) throws IOException, AWException {
		FileOutputStream fo  = new FileOutputStream(file);
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(fo));
		model.save(out);
		out.writeInt(count);
		ivs.save(out);
		out.close();
	}

}
