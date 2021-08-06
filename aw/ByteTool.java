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
// AW Source 30jul2021 : ByteTool.java
// utility for UTF-8 data indexing

package aw;

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
}
