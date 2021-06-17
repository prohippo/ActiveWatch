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
// AW File Inflex.java : 10Mar00 CPM
// standard inflectional stemmer for English

package stem;

import aw.Letter;

public class Inflex {
 		
	// define operation code ranges for recognition

 	private static final short
		nfai= 0, nsuc=-1,
		nbeg= 8, 
		nisa=16, 
		nlen=24, 
		ndbl=32;

	// return codes for deflex and reflex

	private static final int
		isFAIL= -1,
		isNOTP=  0,
		isSUCC=  1;

	// apply logic of inflectional stemming table against word
	
	private static int deflexen (
	
		Token    word, // input word
		int       end, // new word length
		short[] table  // table to drive suffix removal

	) {
		int i,k,m,n;
		int opcode;    // next operation in stemming
		int base;      // subset count
		int  it;       // table index
 		boolean match; // length comparison flag
 		
		it = 0;
		m = end;
		n = table[it++];
    	
		if (n >= end)
			return isNOTP;

		// check that table is appropriate for word ending
		
		for (i = 0; i < n; i++)
			if (word.array[--end] != (byte) table[it++])
				return isNOTP;
 
		// scan table logic
 
		while ((opcode = table[it++]) != 0) {
 
			if (opcode <= nsuc) {
 
				// word satisfies conditions for removing ending

				k = 0;
				end = m - n;
				if (opcode < nsuc) {
					end -= (opcode + 10);
					if (table[it] < 0)
						return reflexen(word,end,0);

					k = table[it++];
				}
				word.length(end);
				for (i = 0; i < k; i++)
					word.append((byte)table[it++]);
				return isSUCC;
			}
 
			else if (opcode < nbeg) {
 
				// conditionally enter logic block on matching a character sequence

				if (end - opcode < 0)
					it += table[it];
				else {
					for (i = 1; i <= opcode && word.array[end - i] == table[it + i]; i++) ;
					if (i > opcode) {
						it  += opcode + 1;
						end -= opcode;
					}
					else
						it += table[it];
				}
			}
 
			else if (opcode < nisa) {
 
				// check whether next character is in a specified set

				if (end > 0) {
					base = it + 1;
					it = base + 1;
					for (i = 0; i < table[base] && word.array[end - 1] != table[it++]; i++) ;
 
					if (i < table[base])
						it = base + table[base] + 1;
					else
						it = (base - 1) + table[base - 1];
				}
				else
					it += table[it];
			}
 
			else if (opcode < nlen) {
 
				// check length of word

				k = table[it++];
				switch (opcode) {
	case Table.lt:	match = (m <  k); break;
	case Table.gt:	match = (m >  k); break;					
	case Table.eq:	match = (m == k); break;	
	case Table.ne:	match = (m != k); break;
	default:		return isNOTP;
				}

				if (!match) it += table[it];
				else it++;
			}
 
			else
				return reflexen(word,m - n,opcode);
		}
		return isFAIL;
	}

	// restore a word to stem form
	
	private static int reflexen (
 
		Token word, // word token
		int    end, // word length
		int opcode  // operation
 
	) {

		if (opcode == 0)
			return deflexen(word,end,Table.restore);
 
		else if (opcode < ndbl) {
			if (word.array[end - 1] == word.array[end - 2])
				return deflexen(word,end - 1,Table.undouble);
			else
				return deflexen(word,end,Table.special);
		}
		else if (opcode >= Table.v0) {
 
			// vowel sequence checks

			word.length(end);
		
			end -= 2;
			switch (opcode) {
case Table.v0:
case Table.v1:
				if (end < 0 || Letter.cnx[word.array[end]])
					break;
case Table.v2:
				--end;
				if (end < 0 || Letter.vwx[word.array[end]])
					break;
				if (end > 0)
					if (word.array[end] == Table.xu && word.array[end - 1] != Table.xq )
						break;
				word.append((byte)Table.xe);
			}
			return isSUCC;
		}
		else
			return isFAIL;
	}
 
 	// remove -s, -ed, -ing inflectional endings
 	
	public static int inflex (
 
		Token word  // word token
 
	) {
		int k,n;
 
		n = deflexen(word,word.length(),Table.dropS);
		
		k = (n == isNOTP) ? deflexen(word,word.length(),Table.dropED) : isNOTP;
 
		if (n != isFAIL && k == isNOTP)
			k = deflexen(word,word.length(),Table.dropING);
 
		return word.length();
	}

	public static int reflex (
 
		Token word, // word token
		int opcode  // operation
 	
	) {
		reflexen(word,word.length(),opcode);
		return word.length();
	}
	
}

