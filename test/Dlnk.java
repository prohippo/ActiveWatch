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
// Dlnk.java : 03Mar99 CPM
// dump saved links from similarity measures computed for pairs of squeezed vectors

package test;

import aw.*;
import object.LinkMapping;
import java.io.*;

public class Dlnk {

	public static void main ( String[] av ) {
		int m,n;
		int  nl;
		float v;
		String x;
		Subsegment ss;
		
		float minv = (av.length > 0) ? Float.valueOf(av[0]).floatValue() : 0;
		
		try {
			LinkMapping lm = new LinkMapping();
			
			DataInputStream in;
			File f = new File("links");
			int nlm = (int)(f.length()/Link.size);
			FileInputStream fs = new FileInputStream(f);
			in = new DataInputStream(new BufferedInputStream(fs));
			for (nl = 0; nl < nlm; nl++) {
				m = in.readShort();
				n = in.readShort();
				if (m > Link.MXTC || n > Link.MXTC)
					break;
				Item itm = lm.fromLinkIndex(m);
				Item itn = lm.fromLinkIndex(n);
				
				v = in.readFloat();
				if (v < minv)
					continue;
					
				ss = new Subsegment(itm.bn,itm.xn);
				x = " " + itm.bn + ":" + ss.it;
				for (int k = x.length(); k < 7; k++)
					System.out.print(" ");
				System.out.print(x + " x");
				ss = new Subsegment(itn.bn,itn.xn);
				x = " " + itn.bn + ":" + ss.it;
				for (int k = x.length(); k < 7; k++)
					System.out.print(" ");
				System.out.print(x);
				System.out.println(" = " + Format.it(v,5,2));
			}
			System.out.println(nl + " links");
		} catch (EOFException e) {
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AWException e) {
			e.printStackTrace();
		}
	}

}


