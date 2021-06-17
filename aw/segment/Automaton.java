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
// AW File Automaton.java : 04Apr02 CPM
// a finite-state automaton for text segmentation

package aw.segment;

import java.io.*;
import match.*;
import aw.*;
import aw.phrase.CharArray;

// the finite automaton will be defined in terms
// of its transitions (arcs), to be specified in
// standard input according the format:
//			x y t (u) a pat
// where
//  x   is the beginning state
//  y   is the ending state
//  t   is an event type for a delimiter match
//  u   is the next event type upon a match
//  a   is an action (- . + ? ! $ >)
//  pat	is a string delimiter pattern
//
// (1) states are integer values from 0 to (NOS-1)
//     determining what sets of delimiters should
//     be matched
// (2) event types are SOH, SBJ, SOT, EOT, or EOM
// (3) possible actions are as follows
//      +  begins a new segment at the start of the
//         current line and goes to the next line
//      .  changes a state without starting a new
//         segment
//      !  changes a state without starting a new
//         segment, but saves current offset
//      $  begins a new segment at previous saved
//         offset and goes to the next line
//      -  begins a new segment at the start of the
//         current line, but stays at that line
//      ?  indicates an error and restarts pattern
//         matching from the current line
//      >  apply an additional minimum length check
//         before accepting a match and revert to .
//         on passing check
// ------------------------------------------------

class Event {

	static final byte SOH=0; // event codes
	static final byte SBJ=1;
	static final byte SOT=2;
	static final byte EOT=3;
	static final byte EOM=4;
	static final String[] id = {"SOH","SBJ","SOT","EOT","EOM"};

	static int encode (
		String e // event label
	) throws AWException {

		// convert event label to numerical code

		for (int i = 0; i < id.length; i++)
			if (e.equals(id[i]))
				return i;
		throw new AWException("bad event= " + e);
		
	}
	
}

class Arc {

	static final int NOS=100; // maximum number of states
	
	byte  type;  // event type = 0,1,2,3,4
	byte  next;  // event type = 0,1,2,3,4
	byte  begin; // beginning state on arc
	byte  end;   // ending    state
	short act;   // match action = -1,0,1
	short min;   // minimum segment length
	Match pat;   // patterns for arc

	public Arc (
		String s
	) throws AWException {
		int k,n=0;

		try {
		
			// get beginning and ending contexts for arc
					
			for (k = 0; Character.isWhitespace(s.charAt(k)); k++);
			for (n = k + 1; Character.isDigit(s.charAt(n)); n++);

			begin = (byte) Integer.parseInt(s.substring(k,n));

			for (k = n; Character.isWhitespace(s.charAt(k)); k++);
			for (n = k + 1; Character.isDigit(s.charAt(n)); n++);

			end   = (byte) Integer.parseInt(s.substring(k,n));

			if (begin >= NOS || end >= NOS ||
				begin <  0   || end <  0)
				throw new AWException("bad state: " + s);

			// get event type for arc
			
			for (k = n; Character.isWhitespace(s.charAt(k)); k++);

			type  = (byte) Event.encode(s.substring(k,k + 3));

			for (k += 3; Character.isWhitespace(s.charAt(k)); k++);

			// get new event type for arc
			
			if (s.charAt(k++) != '(')
				throw new AWException("format error: " + s);
				
			next  = (byte) Event.encode(s.substring(k,k + 3));

			for (k += 4; Character.isWhitespace(s.charAt(k)); k++);

			// get action on arc match
			
			char xc = s.charAt(k++);
			switch (xc) {
case '$':		act = -3; break;
case '!':		act = -2; break;
case '-':		act = -1; break;
case '.':		act =  0; break;
case '+':		act =  1; break;
case '?':		act =  2; break;
case '>':
				while (!Character.isDigit(s.charAt(k))) k++;
				for (n = k + 1; Character.isDigit(s.charAt(n)); n++);

				min = (short) Integer.parseInt(s.substring(k,n));
				act = -4;
				k = n;
				break;
				
default :		throw new AWException("bad character=" + xc);
			}

			// get pattern for arc
			
			if (k == s.length()) {
				if (act != -1)
					throw new AWException("no arc pattern");
				pat = new Match("");
			}
			else {
				for (; k < s.length() ; k++)
					if (!Character.isWhitespace(s.charAt(k)))
						break;
				if (k == s.length())
					throw new AWException("empty arc pattern");
				for (n = k + 1; n < s.length(); n++)
					if (Character.isWhitespace(s.charAt(k)))
						break;

				pat = new Match(s.substring(k,n));
			}
		} catch (NumberFormatException e) {
			String x = s.substring(0,n) + "^" + s.substring(n);
			throw new AWException("bad number: " + x);
		}
	}
}

public class Automaton {

	static final int NOA=255; // number of arcs
	
	static final int L=90;    // line length for auto wrap
	static final int H=32760; // ( = 2**15 - 8 )

	public int[] arcs = new int[Arc.NOS+1]; // arc index for states
	public Arc[] arc  = new Arc[NOA];
	
	public int  narcs;  // current arc count
	public int  where;  // save offset for backup
	public byte state;  // automaton state
	public byte event;  // event expected
	public short skip;  // offset of actual match in line
	
	private CharArray empty = new CharArray("");
	
	private boolean track=true; // for logging items
	
	public final void setTrack ( boolean sense ) { track = sense; }
	
	public Automaton (
		BufferedReader in
	) throws AWException {
	
		// load finite state automaton from file
		
		try {
		
			for (;;) {
			
				// get next line of automaton definition
				
				String buffer = in.readLine();
				if (buffer == null)
					break;
				buffer = buffer.trim();

				if (buffer.length() <= 1 || buffer.charAt(0) == ';')
					continue;

				if (narcs == NOA)
					throw new AWException("arc overflow");

				// parse arc specification
				
				Arc a = new Arc(buffer);

				// sort arcs by beginning state and type
				
				int j = narcs;
				for (; --j >= 0; ) {
					if (arc[j].begin <  a.begin)
						break;
					if (arc[j].begin == a.begin)
						if (arc[j].type <= a.type)
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
			throw new AWException("too few arcs= " + narcs);

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
	
	// match up a line with appropriate patterns for a state

	private final int matchIn (
		Inputs    data, // buffered text input
		CharArray line  // line to match
	) throws AWException {

		int m  = 0; // match flag
		int no = 0; // arc count
		
		int is = arcs[state];
		int ie = arcs[state + 1];
		for (; is < ie; is++) {
		
			// scan all arcs of proper type from current state
			
			if (arc[is].type < event) continue;
			if (arc[is].type > event) break;
			no++;

			if ((skip = (short) arc[is].pat.matchUp(line)) >= 0) {
				m = arc[is].act;
				if (m > 1)
				
					// user-defined error
					
					throw new AWException("segmentation error");
					
				else if (m == -2)
				
					// save current position
					
					where = data.position(0);

				else if (m == -3)
				
					// restore saved position
					
					data.reposition(where);
					
				else if (m == -4) {
				
					// segment length condition on arc
					
					if (line.length() < arc[is].min) {
						m = 0;
						continue;
					}
				}
				
				// on a match, change state
				
				state = arc[is].end;
				event = arc[is].next;
				break;
			}
		}
		if (no == 0)
			throw new AWException("no arcs for type " + Event.id[event]);
		return m;
		
	}
	
	private CharArray textline = null; // the next line to process

	// get next item from stream
		
	public int apply (
		int    in,     // item ID number
		Inputs stream, // buffered text stream
		Lines  starts, // line index
		Index  ix      // index record to fill
	) throws IOException, AWException {
	
		int    sl;     // segment length
		int    tos=0;  // text offset
		int    match;  // match flag
		byte   evn;    // event number

		starts.reset();

		// get next line of input if needed
		
		if (textline == null) {
			event = Event.SOH;
			state = 0;
			textline = stream.input();
		}

	 	for (;;) {

			// find next delimiter line
			
			skip = 0;
			match = 0;
			evn = event;

			while (textline != null) {

				match = matchIn(stream,textline);
				if (match != 0) break;

				// track lines in text segment
				
				if (event != Event.SOH && event != Event.EOM)
					starts.record(textline,L);

				// read in next line
				
				textline = stream.input();
                                
			}

			if (evn == event && match != 0)
			    ;
			else if (evn == Event.SOH) { // start of header
				if (match == 0) break;
				if (match > 1) {
					event = Event.SOH;
					state = 0;
				}
				else {
					ix.os = stream.position(1) + skip;
					ix.hs = ix.sj = ix.tl = 0;
					if (track)
						System.out.println(in + " @" + ix.os);
				}
			}
			else if (evn == Event.SBJ) { // subject line
				if (match == 0 && matchIn(stream,empty) >= 0) {
					System.err.println("no subject line for segment " + in);
					break;
				}
				if (match > 1) {
					event = Event.SOH;
					state = 0;
				}
				else {
					ix.sj = (short)(stream.position(0) - ix.os + skip);
                                        if (ix.sj > ix.hs + ix.tl)
                                            ix.sj = ix.hs;
                                }
                        }
			else if (evn == Event.SOT) { // start of text
				if (match == 0 && matchIn(stream,empty) >= 0) {
					System.err.println("no start of text for segment " + in);
					sl = stream.position(0) - ix.os - 1;
					if (sl < 0)
						sl = 0;
					ix.hs = (short)(sl < H ? sl : H);
					ix.tl = 0;
					skip = 0;
					break;
				}
				if (match > 1) {
					event = Event.SOH;
					state = 0;
				}
				else if (evn != event) {
					tos = stream.position(1) + skip;
					sl = tos - ix.os;
					if (sl < 0)
						sl = 0;
					if (sl <= H)
						ix.hs = (short)sl;
					else {
						System.err.println("header overflow for segment " + in + " (" + sl + " chars)");
						ix.hs = (short)H;
					}
					if (track)
						System.out.println("      header size= " + ix.hs);
				}
			}
			else if (evn == Event.EOT) { // end of text
				if (match == 0 && matchIn(stream,empty) >= 0) {
					System.err.println("no end of text for segment " + in);
					sl = stream.position(0) - tos - 1;
					if (sl < 0)
						sl = 0;
					ix.tl = (short)(sl < H ? sl : H);
					skip = 0;
					break;
				}
				if (match > 1) {
					event = Event.SOH;
					state = 0;
				}
				else if (evn != event)  {
					sl = stream.position(0) - tos + skip;
					if (sl <= H)
						ix.tl = (short)sl;
					else {
						System.err.println("text overflow for segment " + in + " (" + sl + " chars)");
						ix.tl = (short)H;
					}
					if (track)
						System.out.println("      text length= " + ix.tl);
					ix.se++;
				}
			}
			else if (evn == Event.EOM) { // end of item
				if (match == 0 && matchIn(stream,empty) >= 0) {
					if (track)
						System.err.println("no end of item for segment " + in);
					break;
				}
				else if (match > 1) {
					event = Event.SOH;
					state = 0;
				}

				ix.se = 0;
			}

			// check for end of input
			
			if (event == Event.SOH)
				if (starts.countAll() > 0)
					break;

			// record and get next line on a match
			
			if (match == 1) {
				if (event != Event.SOH && event != Event.EOM)
					starts.record(textline,L);
				textline = stream.input();
			}
		}
		if (match != 0)
			starts.record(textline,L);

		// adjust for any segment boundaries not at start of line
		
		if (starts.countAll() > 0 && !starts.register(ix.os + ix.hs,ix.tl))
			throw new AWException("inconsistent line boundaries");

		return starts.countAll(); // how many lines in item
		
	}
	
} 