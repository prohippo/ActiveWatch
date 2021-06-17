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
// AW file Gram.java : 17Sep98 CPM
// basic n-gram definitions

package gram;

public class Gram {

	// for lexical indices, including literals

	public static final short NLIT= 1024; // upper limit on literals
		
	public static final short IB1 = 7937; // base for 1-grams
	public static final short IB2 = 6641; // base for 2-grams
	public static final short IB3 = 1025; // base for 3-grams
	public static final short IBL =    1; // literal indices

	// bit table defining 2-letter seeds for 3-grams
		
	static final short [] t3lx = {
		0x6e, 0xb9, 0x7e, 0x44, 0x24, 0x49, 0x50, 0xd9,
		0xa4, 0x41, 0x44, 0x90, 0x04, 0x7f, 0xf9, 0xae,
		0xc4, 0x24, 0x49, 0x10, 0x99, 0x24, 0x41, 0x44,
		0x10, 0x04, 0x7f, 0x7c, 0x2e, 0x44, 0x00, 0x41,
		0x00, 0x11, 0x00, 0x40, 0x44, 0x12, 0x06, 0x13,
		0xd1, 0x10, 0xf4, 0x85, 0xf1, 0xf0, 0x96, 0xef,
		0x47, 0x64, 0xb2, 0x04, 0x00, 0x00, 0x10, 0x74,
		0xe5, 0xf9, 0x50, 0x99, 0xcc, 0x41, 0x64, 0x92,
		0x06, 0x5f, 0xb9, 0x0e, 0x44, 0x04, 0x01, 0x10,
		0x11, 0x04, 0x00, 0x00, 0x20, 0x00, 0x10, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00
	};

	// offsets for computing index number of seeds in bit table
		
	static final short[] t3lb = {
		  0,  5, 10, 16, 18, 20, 23, 25, 30, 33, 35, 37, 39, 40, 47, 53,
		 58, 61, 63, 66, 67, 71, 73, 75, 77, 78, 79, 86, 91, 95, 97, 97,
		 99, 99,101,101,102,104,106,108,111,115,116,121,124,129,133,137,
		144,148,151,155,156,156,156,157,161,166,172,174,178,182,184,187,
		190,192,198,203,206,208,209,210,211,213,214,214,214,215,215,216,
		216,216,216,216,216	
	};
	
	// for phonetic indices

	static final short PHL = 4; // maximum of of phonetic n-grams
	
	static final short B0s = 7976; // base for 0-grams
	static final short B1s = 7977; // base for 1-grams
	static final short B2s = 7991; // base for 2-grams
	static final short B3s = 8089; // base for 3-grams
	static final short B4s = 8775; // base for 4-grams
	static final short BNs =13577;
	
	static final short[] Bxs = { B0s, B1s, B2s, B3s, B4s, BNs };

	// consonant class definitions for all alphanumeric

	static final int nC = 7; // number of classes
			
	static final short[] Ceq = {
		0,1,6,3,0,1,6,0,0,2,6,4,5,
		5,0,1,6,7,2,3,0,1,0,2,2,2,
		0,0,0,0,0,0,0,0,0,0
	};

}