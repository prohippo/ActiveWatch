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
// CodedLink.java : 30sep2021 CPM
// encoded links for a binary tree (c.f. Knuth vol. III)

package stem;

import java.io.Serializable;

public class CodedLink implements Serializable {

	private   static final short zero = 0; // to code null link
	
	private   static final int   sign = 0x8000; // to get sign bit
	private   static final short mask = 0x7FFF; // to get link
	
	protected static final int   size = 2;
	
	protected short link = 0; // coded linkage field
	
	// for a compact binary tree stored in an array of nodes where the left
	// successor of a node will be the next node in the array and the right
	// successor is indicated by the lower bits of the link field; the sign
	// bit of the link field will be ON if there is no left successor.
	
	public final short   getLink ( ) { return (short)(link & mask); }
	public final void    setLink ( int node )	{ link |= node; }
	public final boolean isLeftEnd ( ) { return (link < 0); }
	public final void    setLeftEnd ( ) { link |= sign; }
	public final void    resetLeftEnd ( ) { link ^= sign; }
	
	// right successors should be set only once in tree-building
	
	public static final boolean isNull ( int node ) { return (node == zero); }
	
}
