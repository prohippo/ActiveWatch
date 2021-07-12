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
// AW file TableCode.java : 29Mar99 CPM
// for building external AW language tables

package aw.table;

import aw.Letter;
import aw.AWException;

public class TableCode {

	// to be consistent with token sorting, digits are mapped to lower case letters
	// and letters are mapped to upper case
	
	// (invisible) character encoding for sorting of table entries
	
	public static String forSorting (
	
		String r
		
	) {
		char x;
		
		StringBuffer s = new StringBuffer(r.length());

		for (int i = 0; i < r.length(); i++) {
			char c = r.charAt(i);
			
			if (Character.isDigit(c))
				x = (char)(c - '0' + 'a');
			else if (Character.isLetter(c))
				x = Character.toUpperCase(c);
			else if (c == Letter.APO)
				x = '{';
			else if (c == Letter.DOT)
				x = '}';
			else
				continue;
				
			s.append(x);
		}
			
		return s.toString();
	}

	// actual encoding to store in table
		
	public static int forStoring (
	
		String r,
		byte[] b,
		int   nb
		
	) throws AWException {
		for (int i = 0; i < r.length(); i++)
			b[nb++] = recode(r.charAt(i));
		
		return nb;
	}

	// get one encoded character
		
	public static byte recode (
	
		char c
		
	) throws AWException {
		if (Character.isLowerCase(c))
			c -= 'a' - '0';
		else if (Character.isUpperCase(c))
			c = Character.toLowerCase(c);
		else if (c == '{')
			c = Letter.APO;
		else if (c == '}')
			c = Letter.DOT;
			
		byte z = Letter.toByte(c);
		if (z < 0)
				throw new AWException("bad character in entry <" + c + ">");
		return z;
	}

}