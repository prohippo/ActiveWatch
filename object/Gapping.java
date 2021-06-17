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
// AW file Gapping.java : 13Jun00 CPM
// Tukey gap selection algorithm

package object;

import java.util.Vector;

public class Gapping {

	private static final double X = 2.3;  // minimum significant gap

	private Vector v = new Vector(); // save gaps
	private double sm  = 0; // sum of gaps
	private double sms = 0; // sum of gaps squared
	
	private double last;
	
	// initialize for computing gaps
		
	public Gapping (
		float w  // first weight
	) {
		last = w;
	}
	
	// compile gaps
	
	public void add (
		float w  // second and subsequent weights
	) {
		double x = last - w;
		last = w;
		v.addElement(new Float(x));
		sm  += x;
		sms += x*x;
	}
	
	// select first significant gap
	
	public int gap (
	
	) {
		int ndeg = v.size();
		if (ndeg == 0)
			return 0;
			
		double es = sm /ndeg;
		double vr = sms/ndeg - es*es;
		double th = es + X*Math.sqrt(vr);

		int n = 0;		
		for (; n < ndeg; n++) {
			double gp = ((Float) v.elementAt(n)).doubleValue();
			if (gp > th)
				break;
		}
		return n + 1;
	}

}