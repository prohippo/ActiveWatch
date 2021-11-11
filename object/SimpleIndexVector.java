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
// AW file SimpleIndexVector.java : 06nov2021 CPM
// n-gram index vector for text item

package object;

import aw.*;
import java.io.*;

// basic compressed vector

abstract class CodedVectorBase implements Serializable {

	// vector byte encodings

	protected static final int MAXSUM=32764; // limit for vector sum
	protected static final int BYTS=    256; // byte modulus
	protected static final int FULL= BYTS-1; // maximum unsigned byte value

	protected static final byte ZERO=  -128; // signed encoding of unsigned zero 
	protected static final byte ONE =  -127; // signed encoding of unsigned one
	protected static final int  SCLR=	 1; // index vector frequency scaling

	protected static final byte SKPL= (byte) (FULL + ZERO); // encoded FULL byte

	protected static final int  VSTART=   3; // start of extents in encoded vector

	protected int NX = Parameter.NEX; // extent count

	private transient int vcs; // vector start
	private transient int ssm; // saved sum
	private transient int lgm; // last n-gram
	private transient int exn; // current extent
	private transient int exo; // extent start

	protected byte[] bb; // byte buffer for encoded vector

	// close out extent in encoded vector

	private final int stopExtent (

		int bo  // buffer offset

	) {
		int exl;

		// check for extent overflow

		if ((exl = bo - exo) > FULL) {
			System.err.println("extent " + exn + " overflow= " + exl);
			exl = FULL;
			bo = exo + exl;
		}

		// store extent length

		bb[exo] = (byte) (exl + ZERO);
		return bo;
	}

	// set up for a new encoded vector in a buffer

	public final int startEncoding (

		int bo  // buffer offset

	) {
		vcs = bo;
		ssm = exn = 0;
		lgm = Parameter.EB[exn];
		exo = bo + VSTART;
		return exo + 1;
	}

	// fill out an encoded vector

	public final int endEncoding (

		int    bo, // buffer offset
		int   nex, // number of extents
		int mvssm  // minimum vector sum

	) {
		// close out remaining extents

		bo = stopExtent(bo);

		for (exn++; exn < nex; exn++)
			bb[bo++] = ONE;

		// store sum at start of vector

		if (ssm < mvssm)
			ssm = mvssm; 
		else if (ssm > MAXSUM) {
			ssm = MAXSUM;
			System.err.println("vector sum overflow");
		}
		bb[vcs  ] = (byte) (ssm/BYTS + ZERO);
		bb[vcs+1] = (byte) (ssm%BYTS + ZERO);
		bb[vcs+2] = 1;
		return bo;
	}

	// add an index and count to an encoded vector

	public final int encodeGramCount (

		int bo, // buffer offset
		int gm, // index to encode
		int kn  // count to encode

	) {
		// fill out vector extents up to index gm
		while (gm > Parameter.EB[exn+1]) {
			bo = stopExtent(bo);
			exo = bo++;
			lgm = Parameter.EB[++exn];
		}

		// encode current index

		int n;

		for (n = gm - lgm; n >= FULL; n -= FULL)
			bb[bo++] = SKPL;
		bb[bo++] = (byte) (n + ZERO);
		lgm = gm;

		// encode count

		ssm += kn;
		while (--kn > 0)
			bb[bo++] = ZERO;
		return bo;
	}

}

// compressed vector with decoding

abstract class CodedVector extends CodedVectorBase implements Serializable {

	protected transient short sum=0; // of counts in vector
	protected transient short gram;  // current n-gram index in vector
	protected transient short count; // for n-gram index
	protected transient short extent;// indicating current extent
	protected transient int   mark;  // save start of extent

	private transient int tp = VSTART; // traversal index
	private transient int tb = 0;  // traversal base
	private transient int xi = 0;  // index over extents
	private transient int nb = 0;  // count of bytes in extent

	// traverses a compressed vector in successive calls

	public final boolean next (

	) {
		int n;

		// scan vector

		for (; xi < NX; xi++) {
			extent = (short) xi;

			// check for start of next extent

			if (nb == 0) {
				mark = tp;
				nb = bb[tp++] - ZERO - 1;
				gram = Parameter.EB[xi];
			}

			// go to next n-gram reference in extent

			if (nb > 0) {

				// get next n-gram

				do {
					n = bb[tp++]; --nb;
					gram += n - ZERO;
				}  while (n == SKPL);

				// get its count

				for (count = 1; nb > 0 && bb[tp] == ZERO; --nb, tp++)
					count++;

				sum += count;

				// on end of extent, must advance extent index here

				if (nb == 0)
					xi++;
				return true;

			}
		}

		// vector exhausted

		return false;
	}

	// get gram + count tuple from scan

	public int[] getVectorTuple (

	) {
		int[] tu = { gram , count };
		return tu;
	}

	// skip to start of extent

	protected final void toExtent (

		int xin

	) {
		if (xin == extent)
			return;
		if (nb > 0)
			tp = mark;
		for (; xi < xin; xi++)
			tp += bb[tp] - ZERO;
		nb = 0;
	}

	// reset traversal to specified position

	protected final void reset (

		int tbs

	) {
		xi = nb = 0;
		tb = tbs;
		tp = tb + VSTART;
		sum = 0;
		mark = tp;
		gram = 0;
		extent = 0;
	}

	// reset traversal to start

	protected final void reset (

	) {
		reset(0);
	}

	// return position in traversal

	protected final int tell ( ) { return tp; }

	// start of traversal

	protected final int base ( ) { return tb; }

	// get the subsegment index stored in a vector

	public final int subsegmentIndex (

	) {
		int m = bb[tb + 2];
		if (m < 0)
			m += BYTS;
		return m;
	}

	// set the subsegment index for in a compressed vector

	public final void subsegmentIndex (
		int k
	) {
		bb[tb + 2] = (byte) k;
	}

	// decode stored vector sum

	public final int decodeSum (

	) {
		return (bb[tb] - ZERO)*BYTS + (bb[tb + 1] - ZERO);
	}

}

// basic AW n-gram index vector

public class SimpleIndexVector extends CodedVector implements Serializable {

	private int vln; // actual compressed vector length in buffer

	// create a compressed vector from a byte array

	public SimpleIndexVector (

		byte[] v,    // array
		int    mvsum // minimum vector sum

	) {
		super();
		int  n; // absolute n-gram index number
		int bo; // vector allocation offset

		bb = new byte[Parameter.MXV];

		// initialize to start of empty compressed vector in buffer

		bo = startEncoding(0);

		int nlm = Parameter.MXI + 1;
		if (nlm > v.length)
			nlm = v.length;

		// compress vector in array

		for (n = 1; n < nlm; n++)
			if (v[n] > 0)
				bo = encodeGramCount(bo,n,v[n]); // fill out vector up to index n

		// close out remaining extents

		vln = endEncoding(bo,NX,mvsum);

	}

	// to support subclasses with constructors of different signatures

	public SimpleIndexVector ( ) { }

	// unpack a vector and return it

	public byte[] expand (

	) {
		reset();
		byte[] v = allocate();
		while (next())
			v[gram] = (byte) count;
		reset();
		return v;
	}

	// allow overriding of allocation

	protected byte[] allocate ( ) { return new byte[Parameter.MXI+1]; }

	// get the stored sum in a vector

	public final int storedSum ( ) { return decodeSum(); }

	// get the actual vector sum as computed by traversal

	public final int computedSum ( ) { return sum; }

	// get the actual compressed vector length

	public final int compressedLength ( ) { return vln; }

}
