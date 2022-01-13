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
// Profiler.java : 12jan2022 CPM
// explicitly create profiles from keys for matching against items

package aw.derive;

import aw.AWException;
import aw.Profile;
import object.KeyProfile;
import java.io.*;

public class Profiler extends Deriver {

	private StringBuffer sb = new StringBuffer();

	// initialization

	public Profiler ( boolean reset ) {
		super(reset);
	}

	// this non-abstract method is required by superclass

	protected Profile derive ( ) throws AWException {
		sb.setLength(0);
		try {
			while (r != null && !r.startsWith("----")) {
				sb.append(r.trim());
				sb.append(' ');
				r = in.readLine();
			}
		} catch (IOException e) {
			throw new AWException(e);
		}
		sb.append('|');
		kys = sb.toString();
		KeyProfile kp = new KeyProfile(kys);
		return kp.profile;
	}

}
