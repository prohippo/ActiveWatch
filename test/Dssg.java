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
// Dssg.java : 18Apr00 CPM

package test;

import aw.*;
import object.*;
import java.io.*;

public class Dssg {

	// dump segm-- files

	static final int M  = 12;
	static final int NN = 72;

	public static void main ( String[] av ) {
	
		int n = (av.length > 0) ? Integer.parseInt(av[0]) : 0;
		int m = (av.length > 1) ? Integer.parseInt(av[1]) : M;
		System.out.println("dumping up to " + m + " records");
		
		try {
		
			Subsegment ss = new Subsegment(0,-1);
			System.out.println(ss.count(0) + " subsegments");
			
			TextSubsegment ts;
			String         tx;
			
			for (int i = 0; i < m; i++, n++) {
				ts = new TextSubsegment(0,n);
				tx = ts.getText();
				System.out.print("0::" + n + " = ");
				int ln = tx.length();
				System.out.print(ln + " chars in item " + ts.getItemNumber());
				System.out.println(", subsegment index= " + ts.getIndex());
				int nn = (av.length > 2) ? ln : (ln < NN) ? ln : NN;
				System.out.println("[" + tx.substring(0,nn) + "]");
			}
						
		} catch (EOFException e) {
			System.out.println("EOF at record " + n);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
