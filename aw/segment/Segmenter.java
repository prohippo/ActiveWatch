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
// AW file Segmenter.java : 14dec2022 CPM
// top-level class for text segmentation

package aw.segment;

import aw.*;
import aw.Lines;
import web.TimedURL;
import java.io.*;
import java.net.*;

public class Segmenter {

	private static final short L = 78;
	private static final short H = 32760;

	private static final int wait = 60; // seconds

	private static Subsegment ssNul = new Subsegment();

	private int upper = 2500;    // subsegmentation limits
	private int lower = 1000;    //

	private InputStream text;    // source text file
	public  Automaton automaton; // to guide segmentation
	private Inputs stream;       // source text with special buffering
	private Lines  lining;       // source text line index

	private Index ix;
	private int   ns = 0;    // subsegment count

	private Control control; // for current batch and counts

	// initialize input and finite-state automaton for it

	 public Segmenter (
                BufferedReader delimiter   // pattern file
        ) throws AWException {
		this(delimiter,0);
	}

	public Segmenter (
		BufferedReader delimiter,  // pattern file
		int level                  // for diagmostic output
	) throws AWException {
		automaton = new Automaton(delimiter);
		automaton.setLevel(level);
		control = new Control();
	}

	// set subsegmentation

	public void setLowerSubsegmentLimit (
		int lower
	) {
		this.lower = lower;
	}

	public void setUpperSubsegmentLimit (
		int upper
	) {
		this.upper = upper;
	}

	// process all items in a file

	public int run (
		String textFile  // text URL
	) throws AWException {

		// set up text input

		System.out.println("adding to batch " + control.cubn);

		Subsegment ss = null;
		Source     sr = null;
		ix   = null;
		text = null;

		try {

			TimedURL u = new TimedURL(textFile,wait);
			text = u.openStream();
			stream = new Inputs(text);
			lining = new Lines();

			ss = new Subsegment(control.cubn,-1);

			sr = new Source(textFile);

			ix = new Index(control.cubn,-1);
			ix.si = (short)Source.count(control.cubn);

			sr.save(control.cubn);

			Source.close();

		} catch (FileNotFoundException e) {
			throw new AWException("no input text",e);
		} catch (IOException e) {
			Source.close();
			Subsegment.close();
			Index.close();
			if (text != null)
				try {
					text.close();
				} catch (IOException x) {
				}
			throw new AWException("I/O error",e);
		}

		// append items to those already in batch

		ix.sx = (short)ss.count(control.cubn);

		int k;  // index of new items for batch
		int m;  // how many items already in batch

		try {

			for (k = m = ix.count(control.cubn);; k++) {

//				System.out.println("item number= " + k);

				// match patterns for segment boundaries

				if (automaton.apply(k,stream,lining,ix) == 0)
					break;

//				System.out.println("Se x= " + lining);
//				lining.dump();

				// divide up long text segment into subsegments

				Subsegmenter s = new Subsegmenter(lining,upper,lower);

				short n = (short) s.count();

//				System.out.println("subsegment count= " + n);

				if (n > 0) {
					for (int j = 0; j < n; j++) {
						ss = s.get();
						ss.it = k;
						ss.save(control.cubn);
					}
					control.noms[control.cubn] += n;
				}
				else {
					ssNul.it = k;
					ssNul.sn = 1;
					ssNul.save(control.cubn);
					n++;
				}

				ns += n;
				ix.ns  = n;
				ix.save(control.cubn);
				ix.sx += n;
			}

		} catch (IOException e) {
			throw new AWException("I/O error",e);			
		} finally {
			System.out.println("closing data files");
			try {
				Subsegment.close();
				Index.close();
				text.close();
			} catch (IOException e) {
			}
		}

		// save updated control

		System.out.println("updating batch " + control.cubn);

		try {
			control.save();
//			control.dump();
		} catch (IOException e) {
			System.err.println(e);
			System.exit(0);
		}

		// item count returned

		return k - m;  // how many net new items
	}

	// get subsegment count

	public final int count (
	) {
		return ns;
	}

}
