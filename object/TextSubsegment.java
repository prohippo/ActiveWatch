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
// AW File TextSubsegment.java : 11jul2021 CPM
// access subsegments in text files

package object;

import aw.*;
import java.io.*;

// get source text subsegments by number

public class TextSubsegment {

	public  static Subsegment ss; // for access to subsegment text
	private static TextItem   it; // item containing subsegment
	private static String     tx; // text of subsegment
	 
	private static int bns = -1;  // saved batch   number
	private static int ins = -1;  // saved item    number
	private static int sns = -1;  // saved segment number

	// get specified subsegment record
		
	public TextSubsegment (
	
		int bn, // batch of target item
		int sn  // segment number in batch
		
	) throws AWException, EOFException {
		
		try {

			if (bns != bn || sns != sn) {
				ss = new Subsegment(bn,sn);
				if (bns != bn || ins != ss.it) {     // in case this segment is NOT
					it = new TextItem(bn,ss.it); // in same item as last segment
					tx = it.getBody();
				}
				bns = bn;
				sns = sn;
				ins = ss.it;
			}

		} catch (EOFException e) {
			throw e;
		} catch (IOException e) {
			throw new AWException("cannot get subsegment: ",e);
		}
	}

	// get subsegment text from item
	
	public String getText (
	
	) throws AWException {
		int tln = tx.length() - ss.so; // available text in chars
		
		if (ss.ln > tln) {             // check for bad subsegment record
			System.err.println("tx= [" + tx + "]");
			System.err.print("@" + bns + "::" + sns + " ");
			System.err.println("text underflow: " + ss.ln + ">" + tln);
			System.err.println("offset= " + ss.so + ", index= " + ss.sn);
			if (tln < 0)
				return "";
			ss.ln = (short) tln;
		}

		return tx.substring(ss.so,ss.so + ss.ln);
	}

	// clean up
		
	public void close (
	
	) {
		Subsegment.close();
		if (it != null)
			it.close();
	}

	// get stored subsegment index in record
		
	public final int getIndex ( ) { return ss.sn; }

	// get item number for subsegment
		
	public final int getItemNumber ( ) { return ss.it; }
	
	// get subsegment offset in item
	
	public final int getOffset ( ) { return ss.so; }
	
}
