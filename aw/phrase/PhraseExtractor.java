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
// PhraseExtractor.java : 24sep2022 CPM
// select phrases from a text sample for profile

package aw.phrase;

import aw.AWException;
import object.KeyTextAnalysis;
import object.FullProfile;
import java.io.*;

class TaggedPhrase {

	String  phrase;    // normalized as string
	short[] signature; // of weighted words
	int     order;     // number of non-zero entries in signature
	int     score;     // total phrase score for profile

	// standard constructor

	public TaggedPhrase (
		int n  // how many signature slots to allocate
	) {
		phrase = "";
		signature = new short[n];
	}

	// special constructor for debugging

	public TaggedPhrase (
		String  txt, // normslixed text of phrase
		short[] sig, // signature
		int     ord, // non-zero signature entries
		int     sco  // computed score
	) {
		phrase    = txt;
		signature = sig;
		order     = ord;
		score     = sco;
	}

	//// for debugging
	////

	public String toString () {
		StringBuffer sb = new StringBuffer(100);
		sb.append("[" + phrase + "] [");
		if (order > signature.length) order = signature.length;
		for (int i = 0; i < order && signature[i] > 0; i++) {
			sb.append(" ");
			sb.append(signature[i]);
		}
		sb.append("] ((" + score + "))");
		return sb.toString();
	}

}

public class PhraseExtractor {

	private int nwd; // how big to make signature

	private PhraseScorer    sr;
	private PhraseAnalysis  an;
	private KeyTextAnalysis ka;

	private PhraseElement[] pe; // actual phrase as array of elements

	// set up to get phrases from text

	public PhraseExtractor (

		int nwd  // maximum word count for phrase

	) throws AWException {
		this.nwd = nwd;
		ka = new KeyTextAnalysis();
		sr = new PhraseScorer(ka);
		pe = new PhraseElement[nwd];
		for (int i = 0; i < nwd; i++)
			pe[i] = new PhraseElement();
	}

	// change to new phrase source

	public void reset (

		String  tx, // text source for new extraction
		Parsing pp  // its phrase analysis

	) throws IOException, AWException {
		an = new PhraseAnalysis(tx,pp);
		an.initialize(0);
	}

	// change to new weighting

	public void reset (

		byte[]  wv  // n-gram weights for phrase selection

	) {
		sr.setWeighting(wv);
	}

	// sequentially produce all tagged phrases

	public TaggedPhrase next (

	) throws AWException {
		for (int i = 0; i < nwd; i++)
			pe[i].clear();
		int ne = an.getNextPhrase(pe);
//		System.out.println("get ne= " + ne);
		if (ne == 0)
			return null;
//		for (int i = 0; i < ne; i++)
//			System.out.println(i + ") " + pe[i].word());	
		TaggedPhrase ph = new TaggedPhrase(ne);
		StringBuffer sb = new StringBuffer(50);
		for (int i = 0; i < ne; i++)
			sb.append(" " + pe[i].word());
		ph.phrase = sb.toString().trim();
		sr.score(pe,ne,ph);
		ph.phrase = an.toPhraseString(pe,ne);
//		System.out.println(">>>>>> [" + ph.phrase + "]");
		return ph;
	}

	////
	//// for debugging

	public void hashdump ( ) {
		sr.hashdump();
	}

	private static final String tx = // sample text
		"President Donald Trump stayed on script for more than a week as he crisscrossed " +
		"through Asia — and then Russian President Vladimir Putin showed up. " +
		"After chatting with Putin on the sidelines of the Asia Pacific Economic Cooperation " +
		"summit here, Trump abandoned the diplomatic tone the White House had carefully scripted " +
		"for his five-country tour, once again contradicting the overwhelming consensus among " +
		"current and former U.S. officials that the Russian leader manipulated the 2016 election.  ";

	private static int count  =  23;
	private static int length = 405;

	private static final byte Bxfc = (byte) 0xfc; // definitions required for byte hex
	private static final byte Bxfd = (byte) 0xfd;
	private static final byte Bxfe = (byte) 0xfe;
	private static final byte Bx80 = (byte) 0x80;
	private static final byte Bx88 = (byte) 0x88;

	private static byte[] pb = { // phrase analysis for above text

		Bxfe,0x00,0x00,Bxfd,0x00,0x00,Bxfc,0x00,0x00,0x28,0x00,0x00,0x00,0x00,0x09,0x00,0x00,0x00,0x00,0x01,
		0x06,0x00,0x00,0x00,0x00,0x01,0x05,0x14,0x0c,0x00,0x00,0x01,0x06,Bxfc,0x00,0x04,0x17,0x00,0x00,0x00,
		0x00,0x06,Bxfc,0x00,0x0f,0x40,0x02,0x00,0x00,0x00,0x01,0x18,0x00,0x00,0x00,0x01,0x04,Bxfc,0x00,0x07,
		0x14,0x0c,0x00,0x00,0x00,0x0c,Bxfc,0x00,0x09,0x18,0x00,0x00,0x00,0x00,0x04,Bxfc,0x00,0x07,0x11,0x02,
		0x00,0x00,0x00,0x04,0x17,0x00,0x00,0x00,0x01,0x07,0x28,0x00,0x00,0x00,0x01,0x09,0x22,0x00,0x00,0x00,
		0x01,0x08,0x00,0x00,0x00,0x00,0x01,0x05,0x14,0x0c,0x00,0x00,0x01,0x06,Bxfd,0x00,0x05,Bxfc,0x00,0x06,
		0x14,0x0c,0x00,0x00,0x00,0x08,Bxfc,0x00,0x06,0x00,0x00,0x00,0x00,0x00,0x05,Bxfc,0x00,0x04,0x40,0x22,
		0x00,0x00,0x00,0x03,0x18,0x0c,0x00,0x00,0x01,0x09,Bxfc,0x00,0x01,0x60,0x02,0x00,0x00,0x00,0x02,0x40,
		0x22,0x00,0x00,0x01,0x03,0x18,0x00,0x00,0x00,0x01,0x04,0x1d,0x00,0x00,0x00,0x01,0x07,0x1d,0x00,0x00,
		0x00,0x01,0x08,0x17,0x00,0x00,0x00,0x01,0x0b,Bxfc,0x00,0x01,0x00,0x00,0x00,0x00,0x00,0x06,Bxfc,0x00,
		0x07,0x00,0x00,0x00,0x00,0x00,0x05,0x14,0x0c,0x00,0x00,0x01,0x09,Bxfc,0x00,0x01,0x40,0x22,0x00,0x00,
		0x00,0x03,0x1d,0x00,0x00,0x00,0x01,0x0a,0x00,0x00,0x00,0x00,0x01,0x04,0x40,0x22,0x00,0x00,0x01,0x03,
		0x00,0x00,0x00,0x00,0x01,0x05,0x00,0x00,0x00,0x00,0x01,0x05,Bxfc,0x00,0x05,0x1e,0x02,0x00,0x00,0x00,
		0x09,0x14,0x0c,0x00,0x00,0x01,0x08,Bxfc,0x00,0x05,0x38,0x12,0x00,0x00,0x00,0x03,0x31,0x00,0x00,0x00,
		0x01,0x04,0x17,0x00,0x00,0x00,0x01,0x07,0x00,0x00,0x00,0x00,0x01,0x04,Bxfc,0x00,0x0d,0x14,0x0c,0x00,
		0x00,0x00,0x0d,Bxfc,0x00,0x01,0x40,0x22,0x00,0x00,0x00,0x03,0x14,0x0c,0x00,0x00,0x01,0x0c,Bxfc,0x00,
		0x01,0x00,0x00,0x00,0x00,0x00,0x09,Bxfc,0x00,0x07,0x00,0x00,0x00,0x00,0x00,0x07,Bxfc,0x00,0x05,0x00,
		0x00,0x00,0x00,0x00,0x06,0x00,0x01,0x00,0x00,0x01,0x04,0x1d,0x0c,0x00,0x00,0x01,0x09,Bxfc,0x00,0x06,
		0x40,0x22,0x00,0x00,0x00,0x03,0x17,0x00,0x00,0x00,0x01,0x07,0x28,0x00,0x00,0x00,0x01,0x06,0x14,0x0c,
		0x00,0x00,0x01,0x0b,Bxfc,0x00,0x01,0x40,0x22,0x00,0x00,0x00,0x03,0x31,0x00,0x00,0x00,0x01,0x04,0x17,
		0x00,0x00,0x00,0x01,0x08,Bx80
	};

	public static void main ( String[] a ) {
		try {
			int cn = (a.length > 0) ? Integer.parseInt(a[0]) : 1;
			System.out.println("test phrase extraction");
			PhraseExtractor pe = new PhraseExtractor(8);
			FullProfile fp = new FullProfile(cn);
			fp.dump();
			byte[] fv = fp.vector();
			if (fp.count() == 0) {
				System.err.println("bad profile= " + cn);
				System.exit(1);
			}
			Parsing pp = new Parsing(count,length,pb);
			pe.reset(tx,pp);
			pe.reset(fv);
			TaggedPhrase tph;
			while ((tph = pe.next()) != null) {
				System.out.println(tph);
			}
		} catch (AWException e) {
			System.err.println(e);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
