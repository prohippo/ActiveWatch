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
// AW File Transform.java : 08Jul2021 CPM
// convert Unicode to equivalent uppercase ASCII with special handling of
// punctuation and digits

package aw;

class Transform {

	private static final int LSQm = 0x2018;  // left  single quote
	private static final int RSQm = 0x2019;  // right single quote (same as APX)
	private static final int LDQm = 0x201C;  // left  double quote
	private static final int RDQm = 0x201D;  // right double quote
	private static final int HYPH = 0x2010;  // Unicode hyphen only
	
	private static final int SUB0 = 0x2080;  // SUBscript and SUPerscript digits
	private static final int SUP0 = 0x2070;  // (these have irregular code points)
	private static final int SUB1 = 0x2081;
	private static final int SUP1 = 0x00B9;  // !
	private static final int SUB2 = 0x2082;
	private static final int SUP2 = 0x00B2;  // !
	private static final int SUB3 = 0x2083;
	private static final int SUP3 = 0x00B3;  // !
	private static final int SUB4 = 0x2084;
	private static final int SUP4 = 0x2074;
	private static final int SUB5 = 0x2085;
	private static final int SUP5 = 0x2075;
	private static final int SUB6 = 0x2086;
	private static final int SUP6 = 0x2076;
	private static final int SUB7 = 0x2087;
	private static final int SUP7 = 0x2077;
	private static final int SUB8 = 0x2088;
	private static final int SUP8 = 0x2078;
	private static final int SUB9 = 0x2089;
	private static final int SUP9 = 0x2079;

	private static final char[] latin = (  // first two Unicode blocks
		"          \n     " +
		"                "  +
		" !\"#$%&'()*+,-./" +
		"0123456789:;<=>?"  +
		"@ABCDEFGHIJKLMNO"  +
		"PQRSTUVWXYZ[\\]^_" +
		"`ABCDEFGHIJKLMNO"  +
		"PQRSTUVWXYZ{|}~ "  +
		"                "  +
		"                "  +
		"                "  +
		"  23     1      "  +
		"AAAAAAECEEEEIIII"  +
		"DNOOOOO OUUUUYTS"  +
		"AAAAAAECEEEEIIII"  +
		"DNOOOOO OUUUUYTY"
	).toCharArray();

	public static void map ( char[] cs ) { // rewrites entire input char array
		for (int i = 0; i < cs.length; i++){
			int c = cs[i];
			if (c < latin.length) {
				cs[i] = latin[c];
			}
			else {
				switch (c) {
case LSQm:
case RSQm:				cs[i] = '\'';
					break;
case LDQm:
case RDQm:				cs[i] = '"';
					break;

case HYPH:				cs[i] = '-';
					break;

case SUB0:
case SUP0:				cs[i] = '0';
					break;

case SUB1:				cs[i] = '1';
					break;
case SUB2:				cs[i] = '2';
					break;
case SUB3:				cs[i] = '3';
					break;
case SUB4:
case SUP4:				cs[i] = '4';
					break;
case SUB5:
case SUP5:				cs[i] = '5';
					break;
case SUB6:
case SUP6:				cs[i] = '6';
					break;
case SUB7:
case SUP7:				cs[i] = '7';
					break;
case SUB8:
case SUP8:				cs[i] = '8';
					break;
case SUB9:
case SUP9:				cs[i] = '9';
					break;
default:
					cs[i] = ' ';
				}
			}
		}
	}

	// unit test

	public static void main(String[] as) {
		char[] cs;
		if (as.length > 0) {
			cs = as[0].toCharArray();
		}
		else {
			cs = "éÉº¹²ß\n".toCharArray();
		}
		map(cs);
		for (char c : cs)
			System.out.print("[" + c + "]");
		System.out.println();
	}

}
