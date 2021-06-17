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
// AW file Range.java : 18Apr99 CPM
// for recording parameters of n-gram probabilities

package aw;

import aw.AWException;
import java.io.*;

public class Range extends StatisticsFile {

	public static final String name = "range";

	public float low;
	public float high;
	public float entropy;

	public Range (

		float lo,
		float hi,
		float rg
	
	) throws AWException {
		low = lo;
		high = hi;
		entropy = rg;
	}
	
	public Range (
	
	) throws AWException {
		load();
	}
	
	final void loadF (
	
	) throws IOException {
		low = io.readFloat();
		high = io.readFloat();
		entropy = io.readFloat();
	}
	
	final void saveF (
	
	) throws IOException {
		io.writeFloat(low);
		io.writeFloat(high);
		io.writeFloat(entropy);
	}
	
	final String nameF (
	
	) {
		return name;
	}
	
}