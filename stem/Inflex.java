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
// AW File Inflex.java : 09jan2023 CPM
// standard inflectional stemmer for English

package stem;

import java.io.*;
import aw.Letter;

public class Inflex {
 		
	// define operation code ranges for recognition

 	private static final short
		nfai= 0, nsuc=-10,
		nisa=10, 
		nbeg=16,
		nlen=20, 
		ndbl=32;

	// return codes for deflex and reflex

	private static final int
		isFAIL= -1,
		isNOTP=  0,
		isSUCC=  1,
		doMORE=  2;

	// for communicating between methods

	private static byte last = (byte) 0xFF;

	// apply logic of inflectional stemming table against word
	
	private static int deflexen (
	
		Token    word, // input word
		int       end, // new word length
		short[] table  // table to drive suffix removal

	) {
		int i,k,m,n,ne;
		int opcode;    // next operation in stemming
		int base;      // subset count
		int  it;       // table logic index
 		boolean match; // length comparison flag
 		
		it = 0;
		m = end;          // length of word to stem
		n = table[it++];  // length of candidate ending
    	
		if (n >= end)
			return isNOTP;

		// check that table is appropriate for word ending
		
		for (i = 0; i < n; i++)
			if (word.array[--end] != (byte) table[it++])
				return isNOTP;
//		System.out.println("\nm= " + m + ", n= " + n);
//		System.out.println("word= " + word + ", last= " + last);

		// scan table logic
 
		while ((opcode = table[it++]) != 0) {
 
//			System.out.println("opcode= " + opcode + ", end= " + end);
//			System.out.println("it= " + it);
			if (opcode < 0) {
 
				// word satisfies conditions for removing ending

//				System.out.println("word= [" + word + "]");
				ne = nsuc - opcode;   // more length adjustment
//				System.out.println("ne= " + ne);

				end = m - n;
//				System.out.println("opcode= " + opcode + ", end= " + end);
				if (n == 0) {
//					System.out.println("alt new end= " + end);
					word.setLength(end);
					word.array[end] = last;
//					System.out.println("word= [" + word + "]");
				}
	
				end += ne;
//				System.out.println("reg new end= " + end);
				word.setLength(end);
//				System.out.println("root word= [" + word + "]");
				k = table[it];	
				if (0 < k & k < 4) {
//					System.out.println("add k= " + k);
					it++;
					for (i = 0; i < k; i++)
						word.append((byte)table[it++]);

//					System.out.println("extended word= [" + word + "]");
				}
				return isSUCC;
			}
 
			else if (opcode < nisa) {
 
				// check whether next character is in a specified subset

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
 
			else if (opcode < nbeg) {
 
				// conditionally enter logic block on matching a character sequence

				int lm = opcode - nisa;
				if (end - lm < 0)
					it += table[it];
				else {
					for (i = 1; i <= lm && word.array[end - i] == table[it + i]; i++) ;
					if (i > lm) {
						it  += lm + 1;
						end -= lm;
					}
					else
						it += table[it];
				}
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

	// restore a word to complete stem form
	
	private static int reflexen (
 
		Token word, // word token
		int    end, // word length
		int opcode  // operation
 
	) {

//		System.out.println("reflexen: opcode= " + opcode);

		if (opcode == 0) {
			restMore(word);
			return isSUCC;
		}
 
		else if (opcode < ndbl) {
			if (word.array[end - 1] == word.array[end - 2])
				return deflexen(word,end - 1,Table.undouble);
			else
				return deflexen(word,end,Table.special);
		}

		else if (opcode == Table.vo) {

			// vowel sequence checks

//			System.out.println("vo end= " + end);
			word.setLength(end); // trim token to length

			end -= 2;            // at possible vowel in stemming result

			if (end < 0 || Letter.cnx[word.array[end]])
				return isSUCC;

 			--end;               // vowel found; now check for preceding consonant

			if (end < 0 || Letter.vwx[word.array[end]])
 				return isSUCC;

 			if (end > 0)
				if (word.array[end] == Table.xu && word.array[end - 1] != Table.xq )
 					return isSUCC;
			word.append((byte)Table.xe);

			return isSUCC;
		}

		else if (opcode == Table.mo) {

			// invoke further logic

			word.setLength(end); // trim token to length
			return doMORE;
		}

		else {
//			System.out.println("fail!");
			return isFAIL;
		}
	}

	// do more restoration

	private static void restMore (

		Token word

	) {
		int  sta;
		int  len;
		byte[] w;

//		System.out.println("restMore" + " [" +  word + "]");
		sta = deflexen(word, word.length(),Table.restore);
		len = word.length();
		if (sta == doMORE) {
			w = word.toArray();
			if (len < 3)
				deflexen(word,len,Table.special);
			else if (w[len-1] == w[len-2]) {
				word.setLength(--len);
				last = w[len];
				deflexen(word,len,Table.undouble);
				last = (byte) 0xFF;
			}
			else
				deflexen(word,word.length(),Table.special);
		}
	}

 	// remove -s, -ed, -ing inflectional endings
 	
	public static int inflex (
 
		Token word  // word token
 
	) {
		int statk=0,statn;
		int leng;

		statn = deflexen(word,word.length(),Table.dropS);
		leng = word.length(); // word might have shortened

		if (statn == isNOTP) {
			statk = deflexen(word,leng,Table.dropED);
			if (statk > 1) restMore(word);		
		}

		if (statk == isNOTP) {
			statk = deflexen(word,leng,Table.dropING);
			if (statk > 1) restMore(word);		
		}

//		System.out.println("inflex word= [" + word + "]");
//		System.out.println("length= " + word.length());
 		return word.length();
	}

	// for support of morphological stemming

	public static int reflex (
 
		Token word, // word token
		int opcode  // operation
 	
	) {
//		System.out.println("reflex: [" + word + "], opcode=" + opcode);
		reflexen(word,word.length(),opcode);
//		System.out.println("to    : [" + word + "]");
		return word.length();
	}
	
}

