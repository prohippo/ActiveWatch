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
// Deriver.java : 11jan2022 CPM
// create an array of profiles from text definitions

package aw.derive;

import aw.AWException;
import aw.Parameter;
import aw.Map;
import aw.Item;
import aw.Attribute;
import aw.Profile;
import object.ProfileList;
import java.io.*;
import java.util.Date;

public abstract class Deriver {

	public  static final double TH = 7.5; // default threshold
	
	private static final byte   SP =  32; // blank fill
	
	private static final byte type = (byte)(Map.bD | Map.bA | Map.bU);

	private Map map = new Map();
	private Attribute att = new Attribute();
	private Item[]   none = new Item[0];
	private ProfileList emp = new ProfileList(none,0);
	
	private Profile nomatch = new Profile(); // a profile that will match nothing
	
	// initialization
	
	public Deriver ( boolean reset ) {
		Date d = new Date();
		att.stm.cdate = att.stm.rdate = att.stm.mdate = d.getTime();
		if (reset) {
			int lm = map.limit();
			for (int i = 1; i <= lm; i++)
				if (map.userType(i))
					map.clear(i);
		}
		nomatch.sgth = 1.0F;
		nomatch.gms[0] = (short)(Parameter.MXI + 2);
		nomatch.wts[0] = -1;
	}
	
	protected BufferedReader in; // for derive()
	protected String        kys; // for derive()
	
	protected String          r; // last input record
	
	private int count; // how many profiles created
	
	// main processing loop

	public void run ( BufferedReader in ) throws AWException {
		try {
			this.in = in;
			count = 0;
			while ((r = in.readLine()) != null && !r.startsWith("----"));
				
			// process file of profile definitions
			
			for (int ni = 1; r != null; ni++) {
				r = in.readLine();
   				if (r == null)
   				    break;

				int k = r.indexOf('=');
				float th = (float) TH;
				if (k >= 0) {
					r = r.substring(k + 1).trim();
					th = Double.valueOf(r).floatValue();
					r = in.readLine();
				}
				
				// create a profile by method defined elsewhere
				
				Profile pp = derive();
				if (pp == null)
					pp = nomatch;
				else
					count++;
				
				// save profile under a new number
				
				short n = map.findFree();
					
				System.out.print("definition " + ni + ": profile " + n);
				if (n < 0)
					break;
				if (pp == nomatch)
					System.out.print(" null");
				map.setType(n,type);
				pp.sgth = th;
				pp.save(n);
				emp.save(n,false);
				int lkys = kys.length();
				if (lkys > att.kys.length)
					lkys = att.kys.length;
				System.arraycopy(kys.getBytes(),0,att.kys,0,lkys);
				for (int i = lkys; i < att.kys.length; i++)
					att.kys[i] = SP;
				att.save(n);
				System.out.println();
			}

			if (count == 0)
				throw new AWException("no profile definitions");
			
			// save profile allocations
			
			map.save();
		} catch (IOException e) {
			throw new AWException(e);
		}
	}
	
	// this must be provided by a subclass
	
	protected abstract Profile derive ( ) throws AWException;
	
	// get count of created profiles
	
	public final int getCount ( ) { return count; }

}
