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
// AW file Table.java : 19dec2022 CPM
// inflectional stemming table definitions for English

package stem;

// tables currently are compiled from structured logic defined in text files
// read in by a C program definftb.c. This produces a virtual machine language
// to be interpreted by various components of the AW inflectional stemmer.

class Table {

	// define symbols for table entries (should be 2 chars)

	static final short

		xa=  0, xb=  1, xc=  2, xd=  3, xe=  4,
		xf=  5, xg=  6, xh=  7, xi=  8, xj=  9,
		xk= 10, xl= 11, xm= 12, xn= 13, xo= 14,
		xp= 15, xq= 16, xr= 17, xs= 18, xt= 19,
		xu= 20, xv= 21, xw= 22, xx= 23, xy= 24, xz= 25,

		zz=0,
		p1=1, p2=2, p3=3,

		no=0,

		y2=-2, y3=-3, y4=-4, y5= -5, y6= -6, 
		y7=-7, y8=-8, y9=-9, y0=-10, ya=-11,

		z1= 1, z2= 2, z3= 3, z4=  4, z5=  5,

 		g1 = 229, g2 = 485, g3 = 117, g4 = 165, g5 = 139, g6 = 209,

		is=8,

		lt=16, eq=17, ne=18, gt=19,

		vo=40,

		mo=50;


	// inflectional rule tables compiled from PyElly logic definition files
	// (n.b. PyElly defines two other tables not included here)

	static final short[] dropS = {
		  1,xs,z2, 4,xy,xs,no,lt, 3, 2,no,eq, 3,19,z1, 8,xd,is, 5, 2,xa,xi,
		 y0,z2, 7,xa,xh,y9,p2,xv,xe,no,eq, 4,64,z3, 5,xn,xe,xl,no,z3, 5,xw,
		 xe,xn,no,z2, 4,xa,xi,no,is, 5, 2,xi,xs,no,z3, 8,xe,xx,xa,y8,p2,xi,
		 xs,z1,16,xu,z2, 4,xn,xa,no,is, 7, 4,xm,xn,xp,xt,y0,no,z3, 6,xe,xo,
		 xg,y8,zz,z3, 5,xd,xu,xs,no,y0,z2,96,xe,xi,z4, 6,xt,xr,xo,xs,y0,z3,
		  5,xk,xo,xo,y0,z3, 5,xn,xw,xo,y0,z2, 4,xv,xo,y0,z4, 6,xr,xo,xl,xa,
		 y0,z4, 6,xp,xp,xu,xy,y0,z4, 6,xh,xt,xo,xo,y0,z2,22,xr,xe,z1,10,xs,
		 z2, 6,xi,xm,y7,p1,xy,no,z2, 4,xt,xo,y0,y7,p1,xy,z3, 6,xl,xa,xk,y8,
		 zz,z1,14,xt,is, 7, 2,xi,xu,y7,p1,xy,eq, 6, 2,y0,y7,p1,xy,z1,g2,xe,
		 z1,61,xo,z3, 6,xt,xe,xv,y8,zz,eq, 5, 2,y0,is, 6, 2,xd,xg,y8,zz,z2,
		  9,xn,xa,z1, 4,xc,y8,zz,y0,z2, 4,xr,xh,y0,z1,10,xh,is, 6, 2,xa,xc,
		 y8,zz,y0,z1,12,xt,is, 8, 4,xa,xi,xs,xt,y8,zz,y0,y8,zz,z1,g1,xs,z2,
		  5,xa,xg,y8,zz,z1,16,xs,z2, 5,xa,xg,y7,zz,z2, 5,xu,xb,y7,zz,y8,zz,
		 z2, 5,xa,xi,y8,zz,z4, 7,xi,xn,xe,xp,y8,zz,z1,64,xu,z1,11,xc,is, 7,
		  3,xo,xr,xu,y8,zz,y0,z1,10,xr,is, 6, 2,xi,xo,y8,zz,y0,z2, 5,xl,xp,
		 y8,zz,is, 7, 3,xi,xp,xt,y8,zz,z1,15,xn,z1, 4,xo,y8,zz,z1, 7,xa,eq,
		  6, 3,y8,zz,eq, 5, 6,z1, 4,xb,y8,zz,y0,z2, 4,xe,xe,y0,is, 8, 2,xe,
		 xy,y8,p2,xi,xs,is, 6, 2,xs,xd,y8,zz,z3, 8,xi,xr,xc,y8,p2,xi,xs,z1,
		 27,xa,z2, 7,xt,xs,y8,p2,xi,xs,z1, 6,xo,y8,p2,xi,xs,z3, 8,xh,xp,xm,
		 y8,p2,xi,xs,y0,z3, 6,xn,xe,xl,y8,zz,z1,31,xo,z1,12,xh,is, 8, 2,xc,
		 xp,y8,p2,xi,xs,y0,z2, 7,xr,xu,y8,p2,xi,xs,z2, 7,xe,xh,y8,p2,xi,xs,
		 z1,18,xp,z2, 7,xo,xn,y8,p2,xi,xs,z2, 7,xa,xn,y8,p2,xi,xs,z4, 7,xi,
		 xl,xo,xp,y8,zz,y0,z1,57,xh,is, 4, 1,xt,y0,z1,47,xc,eq, 5, 2,y0,eq,
		  6,16,z2, 5,xi,xr,y8,zz,is, 7, 3,xn,xr,xt,y8,zz,y0,eq, 7,11,z2, 4,
		 xi,xl,y0,z2, 4,xe,xr,y0,z1, 9,xa,is, 6, 3,xd,xh,xr,y0,y8,zz,is, 5,
		  1,xx,y8,zz,z1,28,xz,z1, 4,xt,y8,zz,z1,20,xz,z2, 5,xe,xf,y7,zz,z2,
		  5,xi,xu,y7,zz,z2, 5,xi,xh,y7,zz,z1,96,xv,z3, 7,xo,xo,xh,y7,p1,xf,
		 z1,35,xl,z2, 6,xo,xw,y7,p1,xf,z1, 3,xo,y0,z1,10,xa,is, 7, 2,xc,xh,
		 y7,p1,xf,z1, 9,xe,z1, 3,xd,no,y7,p1,xf,no,z2, 7,xi,xw,y7,p2,xf,xe,
		 z2,24,xr,xa,z2, 6,xc,xs,y7,p1,xf,z2, 6,xh,xw,y7,p1,xf,z2, 6,xw,xd,
		 y7,p1,xf,z1,16,xa,z2, 6,xo,xl,y7,p1,xf,z2, 6,xe,xh,y7,p1,xf,y0,z1,
		 89,xi,z2, 4,xn,xn,no,z2, 4,xm,xe,y0,z3, 5,xm,xa,xn,y0,z2, 4,xs,xt,
		 y0,z2, 4,xh,xc,y0,z2, 4,xj,xo,y0,is, 6, 3,xn,xz,xq,y0,z3, 5,xl,xa,
		 xk,y0,z3, 5,xd,xa,xh,y0,z3, 5,xd,xu,xa,y0,z3, 5,xl,xe,xa,y0,z3, 5,
		 xt,xa,xr,y0,z1,17,xx,z2, 4,xe,xh,no,z1, 8,xa,is, 5, 2,xl,xr,no,y0,
		 no,z1,70,xu,z1, 8,xn,z2, 4,xe,xm,y0,no,z2, 4,xa,xe,y0,z1,11,xt,is,
		  7, 4,xa,xc,xi,xo,no,y0,z2, 4,xd,xn,y0,z2,11,xl,xu,is, 6, 3,xl,xt,
		 xz,y0,no,z2, 4,xm,xu,y0,z3, 5,xr,xu,xg,y0,z1,12,xa,z2, 4,xl,xu,y0,
		 z1, 3,xu,y0,no,no,is, 5, 2,xs,xu,no,z1,39,xa,is, 5, 2,xv,xx,no,z2,
		  4,xs,xn,no,z2, 4,xi,xl,no,z2, 4,xl,xg,no,z1,14,xm,is, 4, 1,xo,y0,
		 is, 5, 2,xm,xe,y0,no,y0,z3, 5,xp,xe,xc,no,z2, 4,xr,xm,no,y0
	};

	static final short[] dropED = {
		  2,xd,xe,z1,30,xi,eq, 4, 3,ya,zz,is, 5, 2,xx,xk,y0,z1,13,xt,is, 8,
		  3,xm,xp,xr,y8,p1,xy,ya,zz,y8,p1,xy,z1,50,xe,z1,30,xr,eq, 4, 2,no,
		 gt, 4,20,z2, 5,xg,xa,ya,zz,z2, 5,xc,xe,ya,zz,is, 6, 3,xb,xc,xg,no,
		 ya,zz,z2, 5,xt,xn,ya,zz,eq, 4, 8,is, 6, 2,xp,xt,ya,zz,no,is, 5, 1,
		 xu,ya,zz,z3,17,xg,xn,xi,eq, 6, 9,is, 7, 4,xd,xr,xw,xk,y0,ya,zz,z1,
		 22,xh,z1,18,xs,eq, 4, 2,no,is, 6, 3,xr,xd,xk,no,z2, 4,xl,xo,no,mo,
		 z1,27,xb,z2, 4,xm,xa,no,is, 9, 6,xi,xo,xu,xl,xm,xb,mo,z1, 8,xr,is,
		  4, 1,xe,no,y0,no,z2, 4,xw,xn,no,eq, 4,67,is,11, 7,xc,xw,xo,xk,xs,
		 xg,xy,ya,zz,is, 4, 1,xx,y0,z2, 7,xr,xb,ya,p2,xe,xd,z2, 7,xp,xs,ya,
		 p2,xe,xd,z2, 7,xl,xp,ya,p2,xa,xd,is, 4, 1,xh,no,z1,18,xl,is, 4, 1,
		 xs,no,is, 6, 1,xf,ya,p1,xe,ya,p2,xe,xd,no,eq, 3,18,is, 7, 1,xf,ya,
		 p2,xe,xd,is, 7, 1,xl,ya,p2,xa,xd,no,z1,24,xl,z2, 7,xs,xi,ya,p2,xa,
		 xd,z4, 6,xi,xv,xe,xd,y0,z3, 5,xa,xr,xi,y0,mo,z3, 8,xf,xr,xe,ya,p2,
		 xe,xd,z1,85,xm,z1, 3,xh,no,z3, 5,xa,xh,xo,no,z1,10,xm,z3, 5,xa,xh,
		 xo,no,y8,zz,z1, 9,xe,is, 4, 1,xe,y0,ya,zz,z1,10,xi,is, 5, 2,xa,xu,
		 y0,ya,zz,is, 5, 2,xl,xr,y0,z1,10,xa,is, 5, 2,xe,xo,y0,ya,zz,is, 5,
		  1,xu,ya,zz,z3, 5,xo,xs,xn,y0,z2, 4,xo,xo,y0,z1, 4,xo,ya,zz,y8,zz,
		 z1,38,xf,z1,10,xf,z3, 6,xi,xo,xc,y8,zz,y0,z2, 4,xe,xi,y0,z1, 3,xl,
		 y0,z2, 4,xe,xe,y0,is, 6, 3,xa,xi,xo,mo,ya,p2,xe,xd,z3, 6,xv,xv,xe,
		 y8,zz,z1,48,xr,is, 7, 4,xf,xt,xh,xd,no,z2, 4,xa,xr,no,z1,10,xc,z3,
		  6,xa,xs,xs,ya,zz,no,z1,16,xb,is,12, 5,xn,xo,xr,xt,xs,y9,p3,xe,xe,
		 xd,y0,z2, 4,xy,xt,y0,mo
	};
 
	static final short[] dropING = {
		  3,xg,xn,xi,eq, 4, 2,no,is, 5, 2,xe,xo,y0,z1,71,xy,z2, 4,xt,xp,y0,
		 is, 7, 1,xt,y8,p2,xi,xe,z1,13,xd,is, 6, 3,xa,xo,xu,y0,y8,p2,xi,xe,
		 z1,22,xl,z2, 7,xe,xb,y8,p2,xi,xe,is, 7, 4,xl,xf,xe,xp,y0,y8,p2,xi,
		 xe,z1, 9,xv,eq, 5, 5,y8,p2,xi,xe,z2, 7,xm,xy,y8,p2,xi,xe,y0,eq, 5,
		 46,z2, 6,xk,xe,y9,p1,xe,is, 8, 5,xc,xs,xu,xk,xx,mo,z2, 6,xw,xo,y9,
		 p1,xe,z2, 6,xw,xa,y9,p1,xe,z2, 6,xg,xa,y9,p1,xe,z2, 6,xp,xa,y9,p1,
		 xe,no,z1,34,xh,z1,21,xt,is, 5, 2,xy,xr,no,z2, 4,xo,xn,no,z2, 4,xe,
		 xm,no,y9,p1,xe,z1, 8,xs,is, 5, 2,xd,xr,no,mo,z2,84,xg,xn,z1,53,xi,
		 z1, 5,xh,y9,p1,xe,z2, 6,xr,xf,y9,p1,xe,z2, 6,xr,xc,y9,p1,xe,z2, 6,
		 xw,xt,y9,p1,xe,z1, 5,xb,y9,p1,xe,eq, 7, 9,is, 6, 1,xt,y9,p1,xe,y0,
		 z1, 5,xp,y9,p1,xe,y0,z2,10,xa,xh,eq, 7, 2,y0,y9,p1,xe,z2, 6,xa,xr,
		 y9,p1,xe,is, 7, 2,xe,xu,y9,p1,xe,y0,z3, 5,xk,xe,xp,no,z1,44,xr,z3,
		  5,xr,xe,xh,no,z3, 5,xr,xa,xe,no,z2, 6,xc,xa,y9,p1,xe,z2, 4,xy,xt,
		 y0,z2,10,xu,xd,eq, 6, 2,no,y9,p1,xe,is, 5, 2,xp,xt,no,mo,z2, 4,xw,
		 xt,no,z2, 4,xn,xt,no,z3, 9,xn,xn,xi,eq, 6, 2,no,mo,z1, 3,xj,no,z2,
		  4,xs,xd,y0,z3, 6,xv,xv,xe,y8,zz,z1,g3,xl,z2, 9,xe,xg,z2, 4,xd,xu,
		 y0,no,z2, 8,xr,xa,z1, 3,xn,y0,no,z2, 4,xr,xe,no,z2, 4,xh,xt,no,z2,
		  4,xb,xi,no,z2, 4,xg,xd,no,z3, 5,xp,xm,xu,no,z3, 5,xp,xa,xs,no,z5,
		  7,xp,xi,xr,xt,xs,no,z4, 6,xd,xn,xu,xo,no,z4, 6,xd,xe,xe,xs,no,z4,
		  6,xk,xc,xu,xd,no,z3, 5,xs,xo,xg,no,z3, 5,xe,xr,xi,no,z3, 5,xa,xr,
		 xi,y0,z2, 4,xt,xa,no,z2, 4,xf,xl,no,z4, 6,xi,xv,xe,xd,y0,z2, 4,xp,
		 xn,no,mo
	};
 
	static final short[] restore = {
		  0,z1,39,xs,z1, 9,xs,z2, 5,xa,xg,y8,zz,y0,z2, 7,xu,xb,eq, 3, 2,y0,
		 z3, 5,xu,xc,xo,y0,z2, 4,xa,xi,y0,z2, 4,xd,xu,y0,y9,p1,xe,z1,58,xg,
		 is,12, 7,xa,xe,xd,xl,xr,xi,xu,y9,p1,xe,is, 5, 1,xg,y8,zz,z1,35,xn,
		 z1,10,xo,is, 6, 1,xp,y9,p1,xe,y0,z1,18,xa,is, 6, 1,xr,y9,p1,xe,eq,
		  4, 2,y0,z2, 4,xl,xc,y0,y9,p1,xe,y0,z4, 6,xt,xe,xr,xp,y0,z2,58,xk,
		 xc,z4, 7,xa,xl,xl,xe,y8,zz,z4, 7,xa,xu,xo,xv,y8,zz,z1,37,xi,z1,10,
		 xn,is, 6, 2,xa,xc,y8,zz,no,z2, 5,xl,xo,y8,zz,z2, 5,xf,xf,y8,zz,z2,
		  5,xt,xi,y8,zz,z2, 5,xm,xi,y8,zz,y0,is,12, 9,xt,xn,xr,xd,xb,xm,xp,
		 xk,xz,mo,is, 8, 3,xc,xv,xu,y9,p1,xe,z1,g4,xl,z1,88,xl,z3, 6,xo,xt,
		 xx,y8,zz,z3,14,xo,xr,xt,z1, 3,xs,y0,eq, 5, 2,y0,y8,zz,z3, 6,xu,xn,
		 xn,y8,zz,z1,35,xe,z1,13,xp,eq, 5, 2,y0,z2, 4,xs,xs,y0,y8,zz,is, 5,
		  1,xg,y8,zz,gt, 4,10,is, 8, 4,xc,xd,xb,xv,y8,zz,y0,z1,19,xa,z1, 4,
		 xn,y8,zz,z2, 5,xh,xs,y8,zz,z2, 5,xt,xo,y8,zz,y0,is,15,10,xt,xs,xd,
		 xg,xb,xp,xc,xf,xk,xz,y9,p1,xe,z1, 3,xe,y0,z1,30,xi,lt, 5, 2,vo,z2,
		  4,xr,xe,y0,z2, 6,xu,xg,y9,p1,xe,z1,10,xv,z2, 4,xi,xa,y0,y9,p1,xe,
		 vo,z1,21,xa,is, 7, 4,xt,xv,xn,xd,y0,z2, 4,xu,xq,y0,z2, 4,xh,xs,y0,
		 vo,vo,z2,12,xf,xa,is, 7, 2,xr,xh,y9,p1,xe,y0,z2,18,xw,xa,z2, 6,xn,
		 xu,y9,p1,xe,z2, 6,xr,xe,y9,p1,xe,y0,z1,38,xh,z1,22,xc,z1, 9,xa,lt,
		  5, 4,y9,p1,xe,y0,z1, 8,xi,lt, 6, 4,y9,p1,xe,y0,z1,12,xt,is, 9, 4,
		 xa,xe,xi,xo,y9,p1,xe,y0
   	};
 
	static final short[] special = {
		  0,z1,79,xn,z1,21,xe,z1,17,xv,is, 7, 2,xn,xr,y9,p1,xe,z2, 6,xa,xr,
		 y9,p1,xe,y0,z1,53,xo,eq, 3, 4,y9,p1,xe,z3, 5,xd,xn,xa,y0,z2, 4,xd,
		 xr,y0,z2, 4,xr,xi,y0,z2, 4,xt,xt,y0,z2, 4,xb,xb,y0,z3, 5,xh,xp,xi,
		 y0,is, 9, 6,xs,xo,xi,xk,xm,xe,y0,y9,p1,xe,vo,z3, 5,xm,xo,xt,y0,z3,
		  5,xm,xo,xs,y0,z1, 9,xz,z1, 3,xt,y0,y9,p1,xe,z1,g5,xr,is, 8, 3,xt,
		 xb,xd,y9,p1,xe,z1,47,xe,z2,22,xv,xe,is, 6, 1,xr,y9,p1,xe,eq, 5, 2,
		 y0,z2, 4,xl,xi,y0,y9,p1,xe,z2, 6,xh,xd,y9,p1,xe,z2, 6,xh,xo,y9,p1,
		 xe,z2, 6,xf,xr,y9,p1,xe,y0,z1,66,xo,z1,12,xb,is, 6, 3,xr,xa,xh,y0,
		 y9,p1,xe,z1,12,xh,is, 6, 3,xt,xb,xc,y0,y9,p1,xe,z1,10,xt,is, 6, 1,
		 xs,y9,p1,xe,y0,is, 8, 5,xs,xr,xv,xo,xm,y0,z2, 4,xn,xo,y0,z2, 4,xl,
		 xo,y0,z2, 4,xl,xi,y0,y9,p1,xe,z3, 5,xa,xl,xl,y0,z3, 5,xa,xt,xr,y0,
		 vo,z1,g6,xt,z2,14,xs,xa,is, 9, 4,xw,xp,xt,xb,y9,p1,xe,y0,z2,18,xa,
		 xe,z2, 6,xr,xc,y9,p1,xe,z2, 6,xm,xr,y9,p1,xe,y0,z1,94,xi,eq, 3, 4,
		 y9,p1,xe,is, 5, 2,xf,xx,y0,z1,18,xb,z2, 4,xr,xo,y0,z2, 4,xa,xh,y0,
		 z2, 4,xi,xh,y0,vo,z1,11,xs,z3, 7,xo,xp,xm,y9,p1,xe,y0,z2, 4,xm,xi,
		 y0,z2, 4,xm,xo,y0,z2, 4,xc,xi,y0,z2, 4,xr,xi,y0,z2, 4,xr,xe,y0,z2,
		  4,xd,xu,y0,z2,17,xd,xe,eq, 4, 2,y0,is, 6, 3,xn,xe,xr,y0,y9,p1,xe,
		 vo,z1,25,xo,is,10, 5,xm,xn,xd,xt,xu,y9,p1,xe,z1,10,xv,is, 4, 1,xi,
		 y0,y9,p1,xe,y0,z2, 6,xa,xi,y9,p1,xe,z2, 6,xa,xu,y9,p1,xe,z3, 5,xa,
		 xb,xm,y0,z1,30,xe,eq, 3, 4,y9,p1,xe,z2, 4,xl,xl,y0,is, 7, 2,xl,xr,
		 y9,p1,xe,z3, 7,xp,xm,xo,y9,p1,xe,y0,vo,z3, 7,xd,xa,xu,y9,p1,xe,z3,
		  7,xd,xi,xu,y9,p1,xe,z3,17,xp,xo,xl,is, 4, 1,xl,y0,z2, 4,xe,xv,y0,
		 y9,p1,xe,vo
	};
 
	static final short[] undouble = {
		  0,eq, 2, 8,is, 4, 1,xp,y0,ya,zz,eq, 3,36,z1,16,xz,is, 6, 2,xa,xu,
		 ya,zz,z2, 5,xi,xf,ya,zz,y0,z2,10,xr,xu,is, 4, 1,xf,y0,ya,zz,z3, 6,
		 xt,xu,xb,ya,zz,z5, 8,xt,xo,xc,xy,xo,ya,zz,y0
	};

}
