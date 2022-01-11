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
// AW File StemExtension.java : 30sep2021 CPM
// to allow setting up a suffix table

package stem;

import aw.Letter;

public class StemExtension extends StemBase {

	// initialize table links

	protected StemExtension (
		int n
	) {
		super();
	
		for (int i = 0; i < Letter.NAN; i++)
			sufx[i] = -1;

		suftbl = new Node[n];
		for (int i = 0; i < n; i++)
			suftbl[i] = new Node();
	
	}
	
	// for access to suffix nodes
		
	protected final void putAlpha (
		int n,
		int alpha
	) {
		suftbl[n].alpha = (byte) alpha;
	}
	
	protected final int getAlpha (
		int n
	) {
		return suftbl[n].alpha;
	}
	
	protected final void putAction (
		int n,
		int cndn,
		int actn
	) {
		suftbl[n].cndn = (byte) cndn;
		suftbl[n].actn = (byte) actn;
	}
	
	protected final void branchLink (
		int n,
		int link
	) {
		suftbl[n].setLink(link);
	}
	
	protected final void stopLinkSequence (
		int n
	) {
		suftbl[n].setLeftEnd();
	}
	
	protected final void extendLinkSequence (
		int n
	) {
		suftbl[n].resetLeftEnd();
	}
	
	protected final boolean isLinkSequenceEnd (
		int n
	) {
		return suftbl[n].isLeftEnd();
	}
	
	protected final short getLink (
		int n
	) {
		return suftbl[n].getLink();
	}
		
}
