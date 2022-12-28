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
// AW file Map.java : 26dec2022 CPM
// profile allocation map

package aw;

import java.io.*;

public class Map {

	public  static final int MXSP = 960; // maximum number of profiles

	private static final String file = "maps";

	public  static final byte bX = (byte) 0; // unused byte
	public  static final byte bD = (byte) 1; // allocation bit
	public  static final byte bA = (byte) 2; // activation bit
	public  static final byte bN = (byte) 4; // new bit
	public  static final byte bU = (byte) 8; // user-definition bit

	protected short  lm; // maximum allocated profile
	private   short  na; // number of allocated profiles
	private   short  nu; //           user profiles
	private   byte[] aa; // map allocation array

	// create a profile map, loading from file if possible

	public Map (

	) {

		aa = new byte[MXSP+2];
		load();

	}

	// read current map form file

	public void load (

	) {

		DataInputStream in = null;

		try {

			// read from file if possible

			FileInputStream is = new FileInputStream(FileAccess.to(file));
			in = new DataInputStream(new BufferedInputStream(is));
			na = in.readShort();
			nu = in.readShort();
			lm = in.readShort();
			in.readFully(aa);
			in.close();

		} catch (IOException e) {

			System.out.println("new map created");

		} 

	}

	// write updated map to file

	public void save (

	) throws IOException {

		DataOutputStream out = null;

		try {

			FileOutputStream os = new FileOutputStream(FileAccess.to(file));
			out = new DataOutputStream(new BufferedOutputStream(os));
			out.writeShort(na);
			out.writeShort(nu);
			out.writeShort(lm);
			out.write(aa);
			out.close();

		} catch (IOException e) {

			throw e;

		}

	}

	// basic map accessors and operations

	public final void update ( ) {
		for (int i = 1; i <= lm; i++)
			if ((aa[i] & bN) != 0)
				aa[i] ^= bN;
	}

	public final void clear ( int n ) {
		if ((aa[n] & bU) != 0)
			--nu;
		if (aa[n] != 0)
			--na;
		aa[n] = 0;
	}

	public final boolean defined ( int n ) {
		return (n > 0 && n <= MXSP && aa[n] != 0);
	}

	public final boolean userType ( int n ) {
		return ((aa[n] & bU) != 0);
	}

	public final boolean newType ( int n ) {
		return ((aa[n] & bN) != 0);
	}

	public final boolean activeType ( int n ) {
		return ((aa[n] & bA) != 0);
	}

	public final boolean typeOf ( int n, byte b ) {
		return ((aa[n] & b) != 0);
	}

	public final void toggleActivation ( int n ) {
		aa[n] ^= bA;
	}

	public final short findFree ( ) {
		for (short i = 1; i <= lm; i++)
			if (aa[i] == 0)
				return i;
		if (lm < MXSP)
			return (short)(lm + 1);
		else
			return -1;
	}

	public final void setType ( short n, byte b ) {
		byte x = aa[n];

		aa[n] = b;
		if (b == 0) {
			if (x != 0)
				--na;
			if ((x & bU) != 0)
				--nu;
		}
		else {
			if (x == 0)
				na++;
			if ((b & bU) != 0 && (x & bU) == 0)
				nu++;
			if (lm < n)
				lm = n;
		}
	}

	public final int countAll ( ) {
		return na;
	}

	public final int countUser ( ) {
		return nu;
	}

	public final int limit ( ) {
		return lm;
	}

}
