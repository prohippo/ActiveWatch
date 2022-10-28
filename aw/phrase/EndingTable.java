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
// EndingTable.java : 27oct2022 CPM
// encoded reversed word endings for syntax indentification

package aw.phrase;

import aw.AWException;
import aw.Letter;
import stem.CodedLink;
import object.QuickSorting;
import java.io.*;

public class EndingTable extends EndingBase {

	public  static final String input = "endings";

	public  static final int EndN = 720; // maximum number of link nodes

	private static final int EndL =  12; // maximum ending length
	private static final int EndR = 256; // maximum number of endings

	private int freeNode = 0;   // first free node

	private CombinedSymbolTable stb;

	// initialize

	public EndingTable (

	) throws AWException {
		super(EndN);
		try {
			stb = new CombinedSymbolTable();
		} catch (IOException e) {
			throw new AWException(e);
		}
	}

	// build table from text input

	public void build (

		BufferedReader in

	) throws AWException {

		String[] records = new String[EndR+2]; // sorting array
		String line;

		Syntax.initialize(stb);

		int index = 0;
		records[index++] = QuickSorting.LoSentinel;

		// read endings and associated syntax

		for (;;) {

			try {
				if ((line = in.readLine()) == null)
					break;
			} catch (IOException e) {
				break;
			}

			line = line.trim();
			if (line.length() <= 2 || line.charAt(0) == ';')
				continue;

		 	// find ending within line
		 	
		 	int lp = 0;
		 	int ll = line.length();

			for (; lp < ll && !Character.isWhitespace(line.charAt(lp)); lp++);
			if (lp == ll)
				throw new AWException("no syntactic specification: " + line);

			StringBuffer rb = new StringBuffer(line.substring(0,lp));
			String r = rb.reverse() + line.substring(lp);

			// check for proper form

			if (!Character.isLetter(r.charAt(0)))
				throw new AWException("no word ending: " + line);

			// drop trailing comment

			int n = r.indexOf(';');
			if (n >= 0)
				r = r.substring(0,n);

			// save for sorting

			records[index++] = r;

		}
 
		records[index] = QuickSorting.HiSentinel;
		QuickSorting.sort(records,index-1,EndL);
		convert(records,index-1);

	}

	// code records as table nodes

	private void convert (
		String[] records,
		int      count
	) throws AWException {

		SyntaxSpec unknownSyntax = new SyntaxSpec();
		unknownSyntax.type = Syntax.unknownType;
		unknownSyntax.modifiers = unknownSyntax.semantics = 0;

		int link;
		SyntaxPatt patt = new SyntaxPatt();

		// add sorted endings to the binary tree

		for (int i = 1; i <= count; i++) {
			String r = records[i];
			int rp = 0;
			int k = Letter.toByte(r.charAt(rp));

			if (endx[k] == EndingBase.Empty)

				// no ending yet with this final character

				endx[k] = (short) freeNode;

			else {

				// merge into tree with other endings

				int n = endx[k];

				for (;;) {

					char x = r.charAt(rp);
					if (!Character.isLetter(x))
						break;
					byte b = Letter.toByte(x);

					if (b == endtbl[n].alpha) {
						rp++;

						if (!endtbl[n].isLeftEnd())
							n++;
						else {

							// extension of previous ending requires
							// changing terminal status of the node

							endtbl[n].resetLeftEnd();
							break;

						}

					}
					else if ((link = endtbl[n].getLink()) > 0)

						// look for alternative nodes

						n = link;

					else {

						// if no more alternatives, make new one

						endtbl[n].setLink(freeNode);
						break;
					}
				}
			}

			// add the remaining part of an ending to the tree

			char x;
			Node node;

			for (;;) {
				x = r.charAt(rp++);
				if (!Character.isLetter(x))
					break;
				if (freeNode == EndN)
					throw new AWException("endings overflow");
				node = endtbl[freeNode++];
				node.alpha = Letter.toByte(x);
				node.syntax.copy(unknownSyntax);
			}

			node = endtbl[freeNode-1];
			node.setLeftEnd();
			node.constraint = (x == '!') ? Syntax.capitalFeature : 0;
			stb.parseSyntax(r.substring(rp+1).trim(),patt);
			String rs = String.format("%-24.24s",r);
			System.out.println(rs + ": syntax= " + patt);
			Syntax.patternToSpecification(patt,node.syntax);
		}


		System.out.println(freeNode + " nodes allocated out of " + EndN);

	}

	// write word ending index and tree to file

	public void save (

	) throws AWException {
		try {
			FileOutputStream fs = new FileOutputStream(file);
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(fs));
			save(out,freeNode);
			out.close();
		} catch (IOException e) {
			throw new AWException("cannot write endings",e);
		}
		System.out.println("table saved");
	}

}
