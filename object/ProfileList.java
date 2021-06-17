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
// AW file ProfileList.java : 04Feb99 CPM
// profile assignment list class

package object;

import aw.*;
import java.io.*;

//	load and save message lists of arbitrary length
//	stored in files HEADS and LISTS, consisting
//	respectively of list heads and list nodes.  The
//	first record in the file of heads is for a free
//	list of deallocated nodes

// all lists and list nodes have index numbers > 0

class ListHead {

	private static final int size = 8;

	int alln; // count of items recorded
	int snod; // starting node

	// read list head
	
	public void load (
		RandomAccessFile in,
		int               n
	) throws IOException {
		in.seek(n*size);
		alln = in.readInt();
		snod = in.readInt();
	}
	
	// write list head
	
	public void save (
		RandomAccessFile out,
		int                n
	) throws IOException {
		out.seek(n*size);
		out.writeInt(alln);
		out.writeInt(snod);
	}
	
}

// for list entries in file

class ListNode {

	public static final int NI   = 24;
	public static final int size = 4 + NI*Item.size;

	int    ovfl = 0;            // overflow link to next ListNode
	Item[] list = new Item[NI]; // partial item list

	// constructor for empty node
	
	public ListNode (
	) {
		for (int i = 0; i < NI; i++)
			list[i] = new Item(0,0,0.0);
	}
	
	// read node
	
	public void load (
		RandomAccessFile in,
		int               n
	) throws IOException {
		in.seek((n-1)*size);
		ovfl = in.readInt();
		for (int i = 0; i < NI; i++)
			list[i] = new Item(in);
	}
	
	// write node
	
	public void save (
		RandomAccessFile out,
		int                n
	) throws IOException {
		out.seek((n-1)*size);
		out.writeInt(ovfl);
		for (int i = 0; i < NI; i++)
			list[i].save(out);
	}
	
	// index of next node after last
	
	public static int last (
		RandomAccessFile out
	) throws IOException {
		return (int)(out.length()/size) + 1;
	}
	
	// read overflow link of nth node
	
	public static int link (
		RandomAccessFile in,
		int               n
	) throws IOException {
		in.seek((n-1)*size);
		return in.readInt();
	}
	
	// write overflow link of nth node
	
	public static void link (
		RandomAccessFile out,
		int                n,
		int                k
	) throws IOException {
		out.seek((n-1)*size);
		out.writeInt(k);
	}
	
}

public class ProfileList {

	private static ListHead free = new ListHead(); // head of free list for nodes

	public  static final String headFile = "heads";
	public  static final String nodeFile = "lists";
	
	private RandomAccessFile io; // list node file
	private ListNode node = new ListNode(); // current node in memory
	private ListHead head = new ListHead(); // list head for all nodes
	
	protected Item[] list; // actual list as single array
	protected int   count; // number of items in list

	// get index for empty list node
	
	private int allocateNode (
		int n  // index of node to continue
	) throws IOException {
		int k = free.snod;

		node.ovfl = n;
		if (k > 0)
			free.snod = ListNode.link(io,k);
		else {
			k = ListNode.last(io);
			node.save(io,k);
		}
		return k;
	}

	// free up list nodes

	private void deallocateNodes (
		int k  // index of first node to release
	) throws IOException {
		int kn,ko;
		
		if (k == 0)
			return;
		ko = free.snod;
		free.snod = k;
		
		for (;;) {
			kn = ListNode.link(io,k);
			if (kn == 0)
				break;
			k = kn;
		}
		ListNode.link(io,k,ko);
	}

	// allocate Item array for list and load it from file
	
	public ProfileList (
	
		int n  // list number

	) throws AWException {
		RandomAccessFile hin;
		int m,ne;

		if (n <= 0)
			throw new AWException("bad list number=" + n);

		// get head of free list and of list n

		try {
		
			hin = new RandomAccessFile(FileAccess.to(headFile),"r");
			
			if (free.alln == 0)
				free.load(hin,0);

			head.load(hin,n);

			hin.close();
			
		} catch (IOException e) {
			throw new AWException("no list head: ",e);
		}

		if (head.alln == 0) {

			// load empty list

			list  = null;
			count = 0;
			return;
			
		}
		else {

			// copy items into array from list nodes

			list = new Item[head.alln];

			try {
			
				io = new RandomAccessFile(FileAccess.to(nodeFile),"r");			

				ne = head.alln%ListNode.NI;
				if (ne == 0)
					ne = ListNode.NI;
				m = head.alln - ne;
				
				for (int next = head.snod; next > 0; ) {
					node.load(io,next);
					next = node.ovfl;
					
					// reverse ordering of nodes
					
					for (int i = 0; i < ne; i++)
						list[m+i] = node.list[i];
					m -= ListNode.NI;
					ne = ListNode.NI;
				}
				
				io.close();
				
			} catch (IOException e) {
				throw new AWException("list " + n + " read failure");
			}
			
		}

		count = head.alln;
		return;
	}

	// create a list from compiled items
	
	public ProfileList (

		Item[]  l, // list array
		int    nm  // size of list

	) {
		list  = l;
		count = nm;
	}

	// save listed items in files
		
	public void save (
	
		int          n, // list number
		boolean append  // logical flag for adding to list
	
	) throws AWException {

		RandomAccessFile hio = null;
		int next;	
		int m;

		if (n <= 0)
			throw new AWException("bad list number=" + n);

		if (append && count == 0)
			return;

		try {
		
			// get free list head and list head n

			hio = new RandomAccessFile(FileAccess.to(headFile),"rw");

			if (free.alln == 0 && hio.length() > 0)
				free.load(hio,0);

			head.alln = head.snod = 0;
			if (n < free.alln)
				head.load(hio,n);
			else {
				for (int i = free.alln; i < n; i++)
					head.save(hio,n);
				free.alln = n + 1;
			}

			// get list node file

			io = new RandomAccessFile(FileAccess.to(nodeFile),"rw");

			// empty list is special case

			if (count == 0) {
			
				deallocateNodes(head.snod);
				free.save(hio,0);
				head.alln = head.snod = 0;
				head.save(hio,n);
				hio.close();
				io.close();
				return;
				
			}

			// get last node of list

			next = head.snod;
			if (next > 0)
				node.load(io,next);
			else {
				head.alln = 0;
				next = head.snod = allocateNode(0);
			}

			if (!append) {
			
				// free current list and create new one

				m = 0;
				if (head.alln > 0) {
					deallocateNodes(head.snod);
					head.snod = next = allocateNode(0);
				}
				head.alln = count;
				
			}
			else {
			
				// append to current list

				m = head.alln%ListNode.NI;
				if (m == 0 && head.alln > 0)
					m = ListNode.NI;
				head.alln += count;
				
			}
			
			// fill in list nodes

			for (int i = 0; i < count; i++) {
				if (m == ListNode.NI) {
					node.save(io,next);
					next = allocateNode(next);
					m = 0;
				}
				node.list[m++] = list[i];
			}
			head.snod = next;
			node.save(io,next);

			// update list head

			free.save(hio,0);
			head.save(hio,n);
			
		} catch (IOException e) {
		
			throw new AWException("cannot save list: " + n,e);
			
		} finally {
		
			// clean up
		
			try {
				if (io != null)
					io.close();
				if (hio != null)
					hio.close();
			} catch (IOException e) {
			}
				
		}
	}
	
	// needed to handle clearing of files
	
	public final void reset ( ) { free.alln = free.snod = 0; }
	
	// accessors
	
	public final int getCount ( ) { return count; }
	
	public final Item[] getList ( ) { return list; }
	
}