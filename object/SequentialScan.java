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
// AW file SequentialScan.java : 13jan2023 CPM
// compute similarity for each vector in batch

package object;

import aw.*;
import java.io.*;

public class SequentialScan {

	private int bno = 0; // batch
	private int ssn = 0; // subsegment

	private boolean fss = false;

	private ProfileForMatch  ps; // profile to search with
	private double           th; // squared search threshold

	// constructor

	public SequentialScan (
		int              bn, // batch to search
		ProfileForMatch  ps  // profile
	) {

		bno = bn;
		ssn = 0;
		this.ps = ps;
		th = ps.sgth*ps.sgth;

	}

	// compute next scaled similarity score

	private IndexVectorForMatch sv;

	public double next (

	) throws AWException {

		sv = new IndexVectorForMatch(new IndexVector(bno,ssn++));
		fss = (sv.subsegmentIndex() == 1);
        if (sv.match(ps,th))
            return sv.scaledSimilarity();
        else
            return -1;

	}

	// was last score for first subsegment in item?

	public final boolean firstSubsegment ( ) { return fss; }

	// get scan statistics

	public final int actualSum ( ) { return sv.sum(); }

	public final int fullSum ( ) { return sv.decodeSum(); }

}
