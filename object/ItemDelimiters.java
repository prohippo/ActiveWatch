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
// AW file ItemDelimiters.java : 14Oct99 CPM
// consolidated delimiter processing

package object;

import aw.*;
import java.io.*;

public class ItemDelimiters {

	public static final String shortFile = "delimiters";
	public static final String longFile  = "delimit";
	
	// look for full finite state automaton definition
	
	public static BufferedReader stream (
	) {
		try {
			FileReader in = new FileReader(longFile);
			return new BufferedReader(in);
		} catch (IOException e) {
			return null;
		}
	}
	
	public static final int N  = 7; // how many patterns accepted
	
	public              String[] pat = { "", "", "", "*", "", "", "" };
	
	public static final String[] lab = {
		"Start of Header",
		"Subject",
		"End of Header", 
		"Start of Text Body",
		"Start of Trailer",
		"Other Start of Trailer",
		"End of Item"
	};
	
	// constructor
	
	public ItemDelimiters (
	
	) throws AWException {
		try {
			FileReader r = new FileReader(shortFile);
			BufferedReader in = new BufferedReader(r);
			for (int i = 0; i < N; i++)
				pat[i] = in.readLine();
			in.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			throw new AWException("cannot get delimiters",e);
		}
	}
	
	// set patterns explicitly
	
	public void set (
		String soh,  // start of header
		String sbj,  // subject line
		String eoh,  // end of header
		String sob,  // start of text body
		String sot,  // end of text body
		String soto, // other end of text body
		String eom   // end of item
	) {
		pat[0] = soh;
		pat[1] = sbj;
		pat[2] = eoh;
		pat[3] = sob;
		pat[4] = sot;
		pat[5] = soto;
		pat[6] = eom;
	}
	
	// save patterns
	
	public void save (
	
	) throws AWException {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(shortFile));
			for (int i = 0; i < N; i++)
				out.println(pat[i]);
			out.close();
		} catch (IOException e) {
			throw new AWException(e);
		}
	}
	
	// for building finite state automaton
	
	private static final String sohP  = "0 0 SOH (SBJ) ";
	private static final String sbjP  = "0 0 SBJ (SOT) ";
	private static final String eohPs = "0 1 SOT (SOT) + ";
	private static final String sobPe =  " 0 SOT (EOT) ";
	private static final String sotP  =    " EOT (EOM) ";
	private static final String eomP  =  " 0 EOM (SOH) ";
	private static final String sotPe =  " 0 EOT (EOM) -";
	private static final String eomPe =  " 0 EOM (SOH) -";
	
	private static final String plus  = "+ ";
	private static final String minus = "- ";
	
	// constructor
	
	public BufferedReader byteStream (
	
	) throws AWException {
		String soh  = pat[0]; // start of header
		String sbj  = pat[1]; // subject line
		String eoh  = pat[2]; // end of header
		String sob  = pat[3]; // start of text body
		String sot  = pat[4]; // end of text body
		String soto = pat[5]; // other end of text
		String eom  = pat[6]; // end of item
	
		// use to create byte array containing delimiters
		
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintWriter bop = new PrintWriter(bo);

		String a,p;
		String sohSave;
		
		boolean sbjTaken = false;
		boolean sobTaken = false;
		boolean eomTaken = false;
		boolean sohTaken = false;
		
		// special constraints for simplification
		
		if (sob.length() > 0 && !sob.equals("*"))
			eoh = "";
		else if (eoh.length() > 0)
			sob = "";
		
		// for start of text
		
		if (soh.length() > 0) {
			p = soh;
			a = (sbj.length() > 0 || sob.length() > 0) ? plus : minus;
		}
		else if (sbj.length() > 0) {
			p = sbj;
			a = minus;
			sbjTaken = true;
		}
		else if (eoh.length() > 0 && sob.length() == 0) {
			if (sot.length() == 0 && eom.length() == 0)
				throw new AWException("need end of text");
			p = "*";
			a = minus;
		}
		else if (sob.length() > 0) {
			p = sob;
			a = minus;
			sobTaken = true;
		}
		else if (sot.length() > 0 || eom.length() > 0) {
			p = "*";
			a = minus;
		}
		else
			throw new AWException("need start of item");
		put(bop,sohP + a + p);
		sohSave = p;
		
		// for start of subject
		
		if (sbjTaken) {
			p = "*";
			a = (sob.length() > 0) ? plus : minus;
		}
		else if (sbj.length() > 0) {
			p = sbj;
			a = (sob.length() > 0) ? plus : minus;
		}
		else if (eoh.length() > 0) {
			p = "*";
			a = minus;
		}
		else if (!sobTaken) {
			p = sob;
			a = minus;
			sobTaken = true;
		}
		else {
			p = "*";
			a = minus;
		}
		put(bop,sbjP + a + p);
		
		// for start of text
		
		int bs = 0;
		
		if (sobTaken) {
			p = "*";
		}
		else if (sob.length() > 0) {
			p = sob;
		}
		else if (eoh.length() > 0) {
			put(bop,eohPs + eoh);
			bs = 1;
			p = "*";
		}
		else {
			p = "*";
		}
		put(bop,bs + sobPe + plus + p);
		
		int b = 0; // beginning
		int e = 0; // ending
		
		// for end of text
		
		if (sot.length() > 0) {
			p = sot;
			a = (eom.length() > 0) ? plus : minus;
		}
		else if (eom.length() > 0) {
			p = eom;
			a = minus;
			eomTaken = true;
		}
		else if (!sohSave.equals("*")) {
			p = sohSave;
			a = minus;
			sohTaken = true;
			e = 1;
		}
		else
			p = "";
		
		if (p.length() > 0) {
			String rule = b + " " + e + sotP + a + p;
			if (soto.length() > 0)
				put(bop,terminate(rule) + " | " + soto);
			else	
				put(bop,rule);
		}
		bop.println(b + sotPe);
		b = e;

		// for end of item
		
		if (eomTaken) {
			p = "*";
			a = plus;
		}
		else if (eom.length() > 0) {
			p = eom;
			a = plus;
		}
		else if (sohTaken) {
			p = "*";
			a = minus;
		}
		else if (!sohSave.equals("*")) {
			p = sohSave;
			a = (sot.length() > 0) ? plus : minus;
			sohTaken = true;
		}
		else
			p = "";
			
		if (p.length() > 0)
			put(bop,b + eomP + a + p);
		bop.println(0 + eomPe);
				
		// make BufferedReader from byte array created above
		
		byte[] buf = bo.toByteArray();
		ByteArrayInputStream in = new ByteArrayInputStream(buf);
		try {
			bo.writeTo(System.out);
		} catch (IOException de) {
		}
		
		return new BufferedReader(new InputStreamReader(in));
	}
	
	// print pattern for segmentation
	
	private void put (
		PrintWriter bop,
		String s
	) {
		bop.println(terminate(s));
	}
	
	// append trailing *, if needed
	
	private String terminate (
		String s
	) {
		char c = s.charAt(s.length() - 1);
		if (c != '*' && c != '/')
			return s + "*";
		else
			return s;
	}
		
}