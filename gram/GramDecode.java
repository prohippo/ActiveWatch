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
// AW file GramDecode.java : 18nov2022 CPM
// show the string representation of an n-gram index

package gram;

import aw.AWException;
import aw.Parameter;
import aw.Letter;

public class GramDecode extends Gram {

	private LiteralDecoding ld; // for literal n-grams
	private GramMap gm;         // for longer lexical n-grams
	
	// initialization
	
	public GramDecode (
		GramMap         gmap,
		LiteralDecoding ldec
	) throws AWException {
		gm = gmap;
		ld = ldec;
	}
	
	private static final String[] lexicalSeed = {
		"ab", "ac", "ad", "af", "ag", "ai", "al", "am",
		"an", "ap", "ar", "as", "at", "au", "av", "aw",
		"ba", "be", "bi", "bl", "bo", "br", "bu", "ca",
		"cc", "ce", "ch", "ci", "ck", "cl", "co", "cr",
		"ct", "cu", "da", "de", "di", "do", "dr", "du",
		"ea", "eb", "ec", "ed", "ee", "ef", "eg", "ei",
		"el", "em", "en", "eo", "ep", "er", "es", "et",
		"ev", "ex", "fa", "fe", "ff", "fi", "fl", "fo",
		"fr", "fu", "ga", "ge", "gh", "gi", "gl", "go",
		"gr", "gu", "ha", "he", "hi", "ho", "hu", "ia",
		"ib", "ic", "id", "ie", "if", "ig", "ik", "il",
		"im", "in", "io", "ir", "is", "it", "iv", "ja",
		"je", "jo", "ju", "ke", "ki", "la", "le", "li",
		"ll", "lo", "lt", "lu", "ma", "mb", "me", "mi",
		"mm", "mo", "mp", "mu", "na", "nc", "nd", "ne",
		"nf", "ng", "ni", "nn", "no", "ns", "nt", "nu",
		"nv", "oa", "ob", "oc", "od", "of", "og", "oi",
		"ol", "om", "on", "oo", "op", "or", "os", "ot",
		"ou", "ov", "ow", "pa", "pe", "ph", "pi", "pl",
		"po", "pp", "pr", "pu", "qu", "ra", "rc", "rd",
		"re", "rg", "ri", "rl", "rm", "rn", "ro", "rr",
		"rs", "rt", "ru", "rv", "sa", "sc", "se", "sh",
		"si", "sl", "so", "sp", "ss", "st", "su", "ta",
		"te", "th", "ti", "tl", "to", "tr", "tt", "tu",
		"ua", "ub", "uc", "ud", "ue", "ug", "ui", "ul",
		"um", "un", "up", "ur", "us", "ut", "va", "ve",
		"vi", "vo", "wa", "we", "wi", "wo", "xp", "ye"
	};

        // get the character representation for
	// an n-gram from its index number
	
	public String toString (
		int ng  // n-gram index number
	) {

		// index number must be in range

		if (ng <= 0 || ng >= Parameter.MXI)
			return "";

		// alphabetic 5-gram?

		if (ng >= IB5) {
			ng -= IB5;
			return gm.decode5g(ng);
		}

		// alphabetic 4-gram

		if (ng >= IB4) {
			ng -= IB4;
			return gm.decode4g(ng);
		}

		StringBuffer s = new StringBuffer();

		// alphabetic 3-gram?

		if (ng >= IB3) {
			ng -= IB3;
			s.append(lexicalSeed[ng/Letter.NA]);
			s.append(Letter.from[ng%Letter.NA]);
		}

		// alphanumeric 2-gram?

		else if (ng >= IB2) {
			ng -= IB2;
			s.append(Letter.from[ng/Letter.NAN]);
			s.append(Letter.from[ng%Letter.NAN]);
		}
 
		// literal n-gram?

		else if (ng >= IBL)
			return ld.decode(ng-IBL);
 
		return s.toString();
	}

}
