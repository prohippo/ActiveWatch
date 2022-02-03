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
// LexicalStream.java : 30jan2022 CPM
// break text into lexical elements

package aw.phrase;

import aw.CharArray;
import aw.Letter;

public class LexicalStream {

	public static final char stp   = 0x81; // phrase   stop
	public static final char stpf  = 0x82; // sentence stop
	public static final char empty = 0x7F; // to be ignored
	public static final char blank = 0x20; // ASCII space
	
	public static final char CR = '\r';
	public static final char LF = '\n';
	public static final char BS = '\b';
	
	public static final char AMP = '&';
	public static final char BSL = '\'';
	
	private static final String punc = "!?:;";
	private static final String brac = "()[] ";

	protected CharArray text; // text buffer
	
	private static final int N = 3; // maximum space count for no break
	private static final int L = 4; // maximum abbreviation length
	
	// initialize with 0 terminated buffer
	
	public LexicalStream (
	
		CharArray text
		
	) {
		this.text = text;
	}
	
	// check for end of stream
	
	public final boolean end ( ) { return (text.length() == 0); }
	
	// moves to the next word or major punctuation in text
	// and returns the number of characters moved
	
	public int find (
	
	) {
		int it = 0;
		int lmt = text.length();
	
		for (; text.notEmpty(); it++) {
			char c = text.charAt(it);
			if (c == stp || c == stpf) break;
			if (c == empty) continue;
				
			// skip over initial spaces

			int itb = it;
			while (c == blank)
				c = text.charAt(++it);
				
			int k = it - itb;
			
			if (c == CR) {
				k = 0; continue;
			}
			if (c == LF) {
				k = 0; continue;
			}
			
			int n = 0;
			for (; c != 0 && Character.isISOControl(c); n++)
				c = text.charAt(++it);
				
			if (it >= lmt) {
				it = lmt;
				break;
			}

			if (k > N || n > 0) {
				c = ']';
				text.setCharAt(--it,c);
			}
				
			// special character checks and conversions

			if (c == 0 || Character.isLetterOrDigit(c) || c == AMP || c == BSL)
				break;
			else if (punc.indexOf(c) >= 0) {
				text.setCharAt(it,stpf);
				break;
			}
			else if (brac.indexOf(c) >= 0) {
				text.setCharAt(it,stp);
				break;
			}
			else if (c == ',')
				break;
			else if (c == '-') {
				c = text.charAt(it+1);
				if (k > 0 || c == '-' || c == ' ') {
					text.setCharAt(it,stp);
					break;
				}
			}
			else if (c == '.') {
				if (text.charAt(it+1) != '.' || text.charAt(it+2) != '.')
					text.setCharAt(it,stpf);
				else {
					it += 2;
					continue;
				}
				break;
			}
			else if (c == BS)
				text.setCharAt(it,' ');
		}
		
		text.skip(it);
		return it;
	}
	
	private static final String brks = "!?()[]";
	
	// scans the next alphanumeric sequence in text
	// and count of characters
	
	public int get (
	
	) {
		if (!text.notEmpty())
			return 0;

		int it = 0;
		char c = text.charAt(it);
		
		// special treatment for comma
		
		if (c == ',') {
			c = stp;
			text.setCharAt(it,c);
		}
		
		// look for phrase or sentence break
		
		if (c == stp || c == stpf) {
			text.skip(1);
			return 1;
		}
		
		// scan for next element
		
		for (int k = 0;; it++) {
			c = text.charAt(it);
			if (c == 0 || Character.isWhitespace(c) || c == stp || c == stpf)
				break;
			if (brks.indexOf(c) >= 0)
				break;
			if (Character.isLetterOrDigit(c) || c == AMP || c == BSL)
				k++;
			else if (c == Letter.APO) {
				if (!Character.isLetterOrDigit(text.charAt(it+1)))
					break;
			}
			else if (c == Letter.DOT) {
				if (k == 0)
					break;
				else if (k == 1 || Character.isLetterOrDigit(text.charAt(it+1)))
					k = 0;
				else
					break;
			}
			else if (c == '-' || c == '/') {
				if (it > 0) 
					if (!Character.isDigit(text.charAt(it+1)))
						break;
			}
			else if (c == ',') {
				if (it > 0) 
					if (!Character.isDigit(text.charAt(it+1)) ||
						!Character.isDigit(text.charAt(it-1)))
						break;
			}
			else if	(!Character.isLetterOrDigit(text.charAt(it+1)))
				break;
		}
		text.skip(it);
		return it;
	}
	
	// for access to token outside of package
	
	public final String collect ( int n ) { return text.getSubstring(-n,0); }

}
