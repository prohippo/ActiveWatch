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
// AW file ThreadTimer.java : 14Jun21 CPM
// to try to do something for a specified number of seconds

package web;

public class ThreadTimer {

	private Thread action; // what to try
	private int     limit; // how long to try in milliseconds

	// constructor
	
	public ThreadTimer (
		Runnable what, // code to try in Runnable object
		int   howlong  // how many seconds to try
	) {
		action = new Thread(what); // action thread from run() of what
		action.setDaemon(true);
		limit = 1000*howlong;
	}
	
	// execute action as separate thread
	
	public boolean run (
	
	) {
		try { // go until action finishes or until time limit
		    action.start();
		    action.join(limit); 
		} catch (InterruptedException e) {
		}
		boolean status = !action.isAlive();
		Thread.yield();
		while (action.isAlive())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		return status;
	}

}
