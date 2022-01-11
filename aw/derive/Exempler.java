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
// Exempler.java : 10jan2022 CPM
// create profiles based on example items

package aw.derive;

import aw.AWException;
import aw.Item;
import aw.Profile;
import aw.Reference;
import object.ClusterProfile;
import object.ClusterWeighting;
import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer;

public class Exempler extends Deriver {

	private static final int  MLTP =  10; // default minimum count for profile index
	private static final int  MLNG =  16; // default minimum number of profile indices
	private static final double TH = 7.5; // default minimum link threshold
	
	private int  mltp; // actual parameters for computation
	private int  mlng; //
	private double th; //

	// initialize

	public Exempler ( boolean reset, int mltp, int mlng, double th ) {
		super(reset);
		this.mltp = mltp;
		this.mlng = mlng;
		this.th   = th;
		kys = "|"; // no keys
	}
	
	// initialize with defaults
	
	public Exempler ( boolean reset ) {
		this(reset,MLTP,MLNG,TH);
	}
	
	private static final int MNH = 8; // set half threshold for more items than this
	private static final int MNL = 4; // set nominal threshold for (MNH >= n > MNL) items
	
	private Vector<Item> v = new Vector<Item>();  // to accumulate items
	
	// required by superclass to create actual profile

	protected Profile derive ( ) throws AWException {
	
		// collect example items for profile
		
		try {
			v.setSize(0);
			while (r != null && !r.startsWith("----")) {
			    r = r.trim();
			    if (r.charAt(0) != ';') {
				    StringTokenizer st = new StringTokenizer(r," ");
				    while (st.hasMoreTokens())
					    v.addElement(Reference.to(st.nextToken()));
				}
				r = in.readLine();
			}
		} catch (IOException e) {
			throw new AWException(e);
		}
		int m = v.size();
		if (m == 0)
			return null;
		Item[] it = new Item[m];
		v.copyInto(it);
		
		ClusterWeighting cw = new ClusterWeighting(it,m);
		int[] wt = cw.score(th);
		int wn = 0;
		for (int i = 0; i < m; i++)
		    if (wn < wt[i])
		        wn = wt[i];
		
		// set minimum link threshold for items
		
		int wm = (m > MNH) ? (m/2) : (m > MNL) ? MNL : (m - 1);
		if (wm > wn)
		    wm = wn;
		
		// filter out items with too few links
		
		int k = 0;
		int d = (m > MNL) ? 1 : 0;
		for (int i = 0; i < m; i++)
			if (wt[i] >= wm) {
				it[k  ] = it[i];
				wt[k++] = wt[i] - d;
			}
		
		// create profile
		
		ClusterProfile cp = new ClusterProfile(k,it,wt,mltp,mlng);
		return cp.profile;
	}

}
