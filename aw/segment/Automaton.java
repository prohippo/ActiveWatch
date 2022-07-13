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
// AW File Automaton.java : 12jul2022 CPM
// a finite-state automaton for text item segmentation

package aw.segment;

import java.io.*;
import match.*;
import aw.*;

// the finite automaton will be defined in terms
// of its transitions (arcs), to be specified in
// lines of standard input taking the format:
//			x y t a pat
// where
//  x   is the beginning state
//  y   is the ending state
//  t   is an event type for a delimiter match
//  a   is an action (+ . ?)
//  pat	is a string pattern
//
// (1) states are integer values from 0 to (NOS-1)
//     determining what sets of delimiters should
//     be matched to divide up items in a text file
// (2) event types are NIL, SOH, SBJ, SOT, EOT, EOM
// (3) possible actions on an arc are as follows
//      +  go to the next line
//      .  stay on the same input line
//      ?  raise a fatal error
// ------------------------------------------------

class Event {

	static final byte NIL=0; // event codes
	static final byte SOH=1;
	static final byte SBJ=2;
	static final byte SOT=3;
	static final byte EOT=4;
	static final byte EOM=5;
	static final String[] id = {"NIL","SOH","SBJ","SOT","EOT","EOM"};

	static int encode (
		String e // event label
	) {

		// convert event label to numerical code

		for (int i = 0; i < id.length; i++)
			if (e.equals(id[i]))
				return i;
		return -1;
		
	}
	
}

class Arc {

	static final int NOS=100; // maximum number of states
	
	byte  begin; // beginning state on arc
	byte  end;   // ending    state
	byte  type;  // event type = 0,1,2,3,4,5
	short act;   // match action = -1,0,1
	Match pat;   // pattern for arc

	private int skips (
		String s, // input line as string
		int k     // offset into input line
	) throws AWException {
		int m = k;
		for (; k < s.length() && Character.isWhitespace(s.charAt(k)); k++);
		if (k == m)
			throw new AWException("bad arc format: " + s);
		return k - m; // how many space chars to skip
	}

	public Arc (
		String s  // trimmed input line for defining automaton
	) throws AWException {
		int k = 0;
		int m = s.length();
		int n = 0;

		try {
		
			// get beginning and ending states for arc
					
			for (n = k; Character.isDigit(s.charAt(n)); n++);

			if (n > k)
				begin = (byte) Integer.parseInt(s.substring(k,n));
			else
				begin = NOS;

			k = n + skips(s,n);
			for (n = k; Character.isDigit(s.charAt(n)); n++);

			if (n > k)
				end   = (byte) Integer.parseInt(s.substring(k,n));
			else
				end   = NOS;

			if (begin >= NOS || end >= NOS)
				throw new AWException("bad states: " + s);

			// get event type for arc
			
			k = n + skips(s,n);
			if (m - k < 3)
				type = -1;	
			else
				type = (byte) Event.encode(s.substring(k,k + 3));
			if (type < 0)
				throw new AWException("bad event type: " + s);

			k += 3;
			k += skips(s,k);
			if (k >= m)
				throw new AWException("no match action: " + s);

			// get action on arc match
			
			char xc = s.charAt(k++);
			switch (xc) {
case '?':			act = -1; break;
case '.':			act =  0; break;
case '+':			act =  1; break;
default :			throw new AWException("bad action char=" + xc);
			}

			// get pattern for arc
			
			n = skips(s,k);
			if (n == 0)
				throw new AWException("no match pattern: " + s);
			k += n;
			for (n = k; n < s.length(); n++)
				if (Character.isWhitespace(s.charAt(k)))
					break;
			pat = new Match(s.substring(k,n));

		} catch (NumberFormatException e) {
			String x = s.substring(0,n) + "^" + s.substring(n);
			throw new AWException("bad number: " + x);
		}
	}
}

public class Automaton {

	static final int NOA=255; // maximum number of arcs
	
	static final int L=90;    // line length for auto wrap
	static final int H=32760; // ( = 2**15 - 8 )

	public int[] arcs = new int[Arc.NOS+1]; // arc index for states

	public Arc[] arc  = new Arc[NOA];       // arc listing
	
	public int  narcs;   // current arc count
	public int  where;   // save offset for backup
	public byte state;   // automaton state

	private String ellp = "\u2026"; // ellipsis
	
	private int level   = 0;        // for tracking execution
	
	public final void setLevel ( int lvl ) { level = lvl; } // for debugging output
	
	public Automaton (
		BufferedReader in       // for delimiter file
	) throws AWException {
	
		// load finite state automaton arcs from text file
		
		try {
		
			for (;;) {
			
				// get next arc of automaton definition
				
				String buffer = in.readLine();
				if (level > 1)
					System.out.println("in[" + buffer + "]");
				if (buffer == null)
					break;
				buffer = buffer.trim();

				if (buffer.length() <= 1 || buffer.charAt(0) == ';')
					continue;

//				System.out.println("[" + buffer + "]");

				if (narcs == NOA)
					throw new AWException("arc overflow");

				// parse arc specification
				
				if (level > 1)
					System.out.println("arc= " + buffer);
				Arc a = new Arc(buffer);

				// sort arcs by beginning state
				
				int j = narcs;
				for (; --j >= 0; ) {
					if (arc[j].begin <  a.begin)
						break;
					arc[j+1] = arc[j];
				}
				arc[j+1] = a;
				narcs++;
			}
				
		} catch (IOException e) {
			; // do nothing
		}
		
		if (narcs < 3)
			throw new AWException("too few automaton arcs= " + narcs);

		// find start of arcs for each beginning state

		int n = -1;
		for (int j = 0; j < narcs; j++) {
			while (arc[j].begin > n) {
				n++;
				arcs[n] = j;
			}
		}
		n++;
		while (n <= Arc.NOS)
			arcs[n++] = narcs;		

	}
	
	private CharArray textline = null; // the next line to process

	// match up a line with appropriate patterns for current state

	private final int tryArcs ( ) {

		int im = -1; // match index
		
		int is = arcs[state];
		int ie = arcs[state + 1];
                if (is == ie)
			return im;

		for (; is < ie; is++) {
		
			// scan all arcs from current state
			
			if (arc[is].pat.matchUp(textline) >= 0) {
				im = is;
				break;
			}
		}
		return im;
		
	}
	
	private static final int MX = 24;

	// get next delimited item from input stream
		
	public int apply (
		int    idn,    // item ID number
		Inputs stream, // buffered text stream
		Lines  starts, // line index for item
		Index  ix      // index record to fill for item
	) throws IOException, AWException {
	
		int    sl;     // segment length
		int    tos=0;  // text offset
		Arc    arcm;   // arc matched
		int    actn;   // action for arc
		int    event;  // last event seen
		int    nline;  // track line count
		int    m;

		state = 0;
		starts.reset();
		event = Event.NIL;
		ix.hs = ix.tl = 0;
		nline = 0;

		System.out.println("automaton: next item " + idn);
		if (level > 0) {
			System.out.println("hs= " + ix.hs + ", tl= " + ix.tl);
			System.out.println("debugging at level= " + level + "\n");
		}

	 	while (event != Event.EOM) {  // main loop reading in lines for next text item

			if (level > 2)
				System.out.println("state= " + state + " read more: " + (textline == null));

			// get next line of input if needed
		
			if (textline == null) {
				textline = stream.input();
				if (textline == null) {
					if (level > 0) {
						System.out.println("end of text input");
						if (event == Event.SOT)
							System.out.println("*** possible incomplete final item!");
					}
					break;
				}
				textline.remap();
				if (level > 0)
					System.out.println("remapped=" + textline);
				nline++;
			}

			// find next delimiter line

			if (level > 2) {
				String t = textline.getSubstring(0,MX);	
				if (t.length() < textline.length())
					t += ellp;
//				System.out.println("line: [" + t + "]");
			}
			
			m = tryArcs();
			if (level > 1)
				System.out.println("match= " + m + ", state= " + state);

			if (m < 0) {
				if (level > 1)
					System.out.println("unknown: " + textline);
				if (event != Event.EOT && event != Event.EOM)
					starts.record(textline,L);
				textline = null;
				continue;
			}

			if (level > 2)
				System.out.println("matched arc= " + m);

			arcm = arc[m];
			actn = arcm.act;
			if (actn < 0)
				throw new AWException("forced fatal error");

			if (level > 2)
				System.out.println("event " + arcm.type);

			switch (arcm.type) {

case Event.NIL:			// no event
				if (level > 2)
					System.out.println("NIL event");
				break;

case Event.SOH:			// start of header
				event = Event.SOH;

				ix.os = stream.position(1);
				ix.hs = ix.sj = ix.tl = 0;
				if (level > 2)
					System.out.println(idn + " @" + ix.os);
				break;

case Event.SBJ:			// subject line
				event = Event.SBJ;

				ix.sj = (short)(stream.position(0) - ix.os);
				if (ix.sj > ix.hs + ix.tl)
					ix.sj = ix.hs;
				break;

case Event.SOT:			// start of text to index
				event = Event.SOT;

				tos = stream.position(1);
				sl = tos - ix.os;
				if (sl < 0)
					sl = 0;
				if (sl <= H)
					ix.hs = (short)sl;
				else {
					System.err.println("header overflow for segment " + idn + " (" + sl + " chars)");
					ix.hs = (short)H;
				}
				System.out.println("      header size= " + ix.hs);
				starts.skipTo(nline - 1);

				break;

case Event.EOT:			// end of text to index
                                event = Event.EOT;

				sl = stream.position(0) - tos;
//				System.out.println("tos= " + tos + ", sl= " + sl);
				if (sl <= H)
					ix.tl = (short)sl;
				else {
					System.err.println("text overflow for segment " + idn + " (" + sl + " chars)");
					ix.tl = (short)H;
				}
				System.out.println("      text length= " + ix.tl);
				ix.se++;
				break;

case Event.EOM:			// end of item
				event = Event.EOM;

				ix.se = 0;
				if (level > 1)
					System.out.println("      end of item");

			}

			if (level > 1) {
				System.out.println("  sx= " + ix.se + ", sj= " + ix.sj);
				System.out.println("  hs= " + ix.hs + ", tl= " + ix.tl);
			}

			state = arcm.end;
			if (level > 2)
				System.out.println("to state " + state);
			if (arcm.act > 0) {
				if (event != Event.EOT && event != Event.EOM)
					starts.record(textline,L);
				textline = null;
			}
		}

//		System.out.println("Au x= " + starts);

		////
		//   special check to avoid infinite loops

//		System.out.println("Au x= " + starts);
//		System.out.println("hs= " + ix.hs + ", tl= " + ix.tl);
		int counting = (ix.hs > 0 || ix.tl > 0) ? starts.countAll() : 0;

//		System.out.println("Au x= " + starts);
		if (level > 0)
			System.out.println(counting + " lines");

		return counting; // how many lines in item
		
	}
	
}
