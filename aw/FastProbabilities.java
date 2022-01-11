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
// AW file FastProbabilities.java : 19Sep02 CPM
// faster buffered access to n-gram probabilities

package aw;

import java.io.*;

public class FastProbabilities {

	private DataInputStream da; // access to buffer
	private static byte[]   pb; // buffer for probabilities
	
	// special buffering for probabilities
	
	public FastProbabilities (
    
	) throws IOException {
        this("probs");
    }
    
	public FastProbabilities (
       String file
	) throws IOException {
		try {
			if (pb == null) {
				File f = new File(file);
				FileInputStream in = new FileInputStream(f);
                int n = (int) f.length();
				pb = new byte[n];
                for (int k = 0; k < n; )
                    k += in.read(pb,k,n-k);
				in.close();
			}
			da = new DataInputStream(new ByteArrayInputStream(pb));
		} catch (IOException e) {
            da = null;
			pb = null;
			throw e;
		}
	}

	// access probability value
		
	public float at (
		int n
	) throws IOException {
		if (pb == null || n < 1 || n >= Parameter.MXI)
			return -1;
        da.reset();
        da.skip(4*n);
        return da.readFloat();
	}
	
	// get full array
	
	public float[] array (
	
	) throws IOException {
		float[] pbs = new float[Parameter.MXI+2];
        da.reset();
        for (int i = 0; i < Parameter.MXI; i++)
            pbs[i] = da.readFloat();
		return pbs;
	}
    
    // get partial packed array
    
    public float[] packedArray (
        short[] g,
        int    lg
    ) throws IOException {
        float[] pbs = new float[lg];
        for (int i = 0, og = 0; i < lg; i++) {
            da.skip(4*(g[i] - og));
            pbs[i] = da.readFloat();
            og = g[i];
        }
		return pbs;
    }

}

