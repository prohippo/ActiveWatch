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
// AW Source 19oct2022 : ByteTool.java
// utility to work with byte streams for UTF-8 data management,
// mapping to ASCII for simpler analysis, and data conversion

package aw;

//!! ActiveWatch (AW) was originally written in the C language to operate on ASCII
//!! input data. The current Java version has to work with UTF-8 input data, a big
//!! complication. To preserve the AW algorithms for stemming, entity recognition,
//!! n-gram extraction, and other language processing, Unicode characters are mapped
//!! into ASCII near-equivalents. This includes removal of diacritical marks from
//!! letters, conversion of special punctuation, and replacement of other non-ASCII
//!! characters by ASCII spaces. Random access to text in files must by a byte offset
//!! into UTF-8 encoding where a character might ine represented by one or more bytes.
//!! Byte counts are generally more than character counts for a text segment. Byte
//!! counts for text segments in files are generally more than character counts.

public class ByteTool {

	public static int L1BYTE = 0x0080;
	public static int L2BYTE = 0x0800;
	public static int L3BYTE = 0xFFFF; // limit for original Java 16-bit Unicode

	// this method should used only for text without supplementary chars
	// returns the utf-8 byte length for an array of Java Unicode chars
	// or -1 on any failure

	public static int utf8length ( char[] array ) {

		int bytl = 0;
		for (char x: array) {
			if (x < L1BYTE)
				bytl += 1;
			else if (x <  L2BYTE)
				bytl += 2;
			else if (x <= L3BYTE)
				bytl += 3;
			else
				return -1;  // should never happen with 16-bit chars
		}
		return bytl;

	}

	// tables to convert Unicode to ASCII for simple text analysis

	protected static final byte
		x0= 0, x1= 1, x2= 2, x3= 3, x4= 4,
		x5= 5, x6= 6, x7= 7, x8= 8, x9= 9;

	protected static final byte
		za=10, zb=11, zc=12, zd=13,
		ze=14, zf=15, zg=16, zh=17,
		zi=18, zj=19, zk=20, zl=21,
		zm=22, zn=23, zo=24, zp=25,
		zq=26, zr=27, zs=28, zt=29,
		zu=30, zv=31, zw=32, zx=33,
		zy=34, zz=35;

	// mapping of Unicode Basic Latin, Latin-1 Supplement, and Latin Extended A blocks

	protected static final byte nc[] = {
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,-1,-1,-1,-1,-1,-1,

		-1,za,zb,zc,zd,ze,zf,zg,zh,zi,zj,zk,zl,zm,zn,zo,
		zp,zq,zr,zs,zt,zu,zv,zw,zx,zy,zz,-1,-1,-1,-1,-1,
		-1,za,zb,zc,zd,ze,zf,zg,zh,zi,zj,zk,zl,zm,zn,zo,
		zp,zq,zr,zs,zt,zu,zv,zw,zx,zy,zz,-1,-1,-1,-1,-1,

		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,zs,-1,zo,-1,zz,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,zs,-1,zo,-1,zz,zy,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,x2,x3,-1,-1,-1,-1,-1,x1,x0,-1,-1,-1,-1,-1,

		za,za,za,za,za,za,za,zc,ze,ze,ze,ze,zi,zi,zi,zi,
		zt,zn,zo,zo,zo,zo,zo,-1,zo,zu,zu,zu,zu,zy,zt,zs,
		za,za,za,za,za,za,za,zc,ze,ze,ze,ze,zi,zi,zi,zi,
		zt,zn,zo,zo,zo,zo,zo,-1,zo,zu,zu,zu,zu,zy,zt,zy,

		za,za,za,za,za,za,zc,zc,zc,zc,zc,zc,zc,zc,zd,zd,
		zd,zd,ze,ze,ze,ze,ze,ze,ze,ze,ze,ze,zg,zg,zg,zg,
		zg,zg,zg,zg,zh,zh,zh,zh,zi,zi,zi,zi,zi,zi,zi,zi,
		zi,zi,zi,zi,zj,zj,zk,zk,zk,zl,zl,zl,zl,zl,zl,zl,

		zl,zl,zl,zn,zn,zn,zn,zn,zn,zn,zn,zn,zo,zo,zo,zo,
		zo,zo,za,za,zr,zr,zr,zr,zr,zr,zs,zs,zs,zs,zs,zs,
		zs,zs,zt,zt,zt,zt,zt,zt,zu,zu,zu,zu,zu,zu,zu,zu,
		zu,zu,zu,zu,zw,zw,zy,zy,zy,zz,zz,zz,zz,zz,zz,zs,

		zb,zb,zb,zb,-1,-1,zo,zo,zc,zd,zd,zd,zd,zd,ze,ze,
		ze,zf,zf,zg,zg,zh,zi,zi,zk,zk,zl,zl,zm,zn,zn,zo,
		zo,zo,zo,zo,zp,zp,zr,-1,-1,zs,zs,zt,zt,zt,zt,zu,
		zu,zu,zv,zy,zy,zz,zz,zz,zz,zz,zz,zt,-1,-1,-1,-1,

		-1,-1,-1,-1,zj,zj,zj,zl,zl,zl,zn,zn,zn,za,za,zi,
		zi,zo,zo,zu,zu
	};

	// for normalization of key form

	public static final char[] unmapping =
		 "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	// Unicode punctuation

	private static final char
		RSQm = '\u2019',   // right single quote (same as APX)
		LDQm = '\u201C',   // left  double quote
		RDQm = '\u201D',   // right double quote
		NDSh = '\u2013',   // en dash
		MDSh = '\u2014';   // em dash

	// map String to ASCII upper case

	public static final String bytify ( String s ) {
		StringBuffer sb = new StringBuffer();
		char cn;
		int ls = s.length();
		for (int i = 0; i < ls; i++) {
			char c = s.charAt(i);
			boolean cap = Character.isUpperCase(c);
			if (c < 128)
				cn = Character.toLowerCase(c);
			else if (c == RSQm)
				cn = '\'';
			else if (c == LDQm || c == RDQm)
				cn = ' ';
			else if (c == NDSh || c == MDSh)
				cn = '-';
			else if (c >= nc.length)
				cn = ' ';
			else if (Character.isLetter(c) || Character.isDigit(c))	
				cn = unmapping[nc[c]];
			else
				cn = ' ';

			if (cap) cn = Character.toUpperCase(cn);
			sb.append(cn);
		}
		return sb.toString();
	}

	// encoding and decoding short integers in a binary byte stream

	private static final short Mask=0x00FF;

	public static void  shortToBytes ( byte[] b , int p , short n ) {
		b[p++] = (byte)((n>>8)&Mask);      // no check for array overflow!
		b[p  ] = (byte)(n&Mask);           //
	}

	public static short bytesToShort ( byte[] b , int p ) {
		short m = b[p];
		short n = b[p+1];
		return (short)((m<<8) | (n&Mask)); // no check for array overflow!
	}

	////
	//// for debugging

	private static final char[] data = {
		'A' , 'a' , ' ' , '\u00D8' , LDQm, RDQm, '0', '1' ,
		'\u00A5' , '\u00e9' , '\u00DD' , '\u00FE' , '\u00FF'
	};

	public static void main ( String[] a ) {

		String s = (a.length > 0) ? a[0] : new String(data);
		System.out.println('=' + bytify(s));

		short n = 32767;
		byte[] b = new byte[2];
		String f = "%2x, %2x";
		shortToBytes(b,0,n);
		System.out.println(String.format(f,b[0],b[1]));
		System.out.println("=" + bytesToShort(b,0));
	}
}
