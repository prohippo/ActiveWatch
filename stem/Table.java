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
// AW file Table.java : 09Jan04 CPM
// inflectional stemming table definitions for English

package stem;

class Table {

	// define symbols for table entries

	static final short

		xa=  0, xb=  1, xc=  2, xd=  3, xe=  4,
		xf=  5, xg=  6, xh=  7, xi=  8, xj=  9,
		xk= 10, xl= 11, xm= 12, xn= 13, xo= 14,
		xp= 15, xq= 16, xr= 17, xs= 18, xt= 19,
		xu= 20, xv= 21, xw= 22, xx= 23, xy= 24, xz= 25,

		m1=-1, m2=-2, m3=-3, m4=-4, m5=-5, m6=-6,
		zz=0,
		p1= 1, p2= 2, p3= 3, p4= 4, p5= 5, p6= 6,
		
		no=0,
		
		y0=-1, y1=-2, y2=-3,
		y3=-4, y4=-5, y5=-6, 
		y6=-7, y7=-8, y8=-9, y9=-10, ya=-11,
		
		z1= 1, z2= 2, z3= 3, z4=  4, z5=  5,

		g1=118, g2=234, g3=160, g4=139, g5=204,

		is=8,

		lt=16, eq=17, ne=18, gt=19,

		v0=40, v1=41, v2=42,
			
		sp=24;


	// define inflectional rule tables copied from AW code in C

	static final short[] dropS = {
	  1,xs,z2, 4,xy,xs,no,lt, 3, 2,no,eq, 3,11,z1, 8,xd,is, 5, 2,xa,xi,
	 y0,no,eq, 4,47,z3, 5,xn,xe,xl,no,z2, 4,xa,xi,no,is, 5, 2,xi,xs,no,
	 z3, 8,xe,xx,xa,y8,p2,xi,xs,z1,11,xu,is, 7, 4,xm,xn,xp,xt,y0,no,z3,
	  6,xe,xo,xg,y8,zz,y0,z2,76,xe,xi,z4, 6,xt,xr,xo,xs,y0,z3, 5,xk,xo,
	 xo,y0,z2, 4,xv,xo,y0,z4, 6,xr,xo,xl,xa,y0,z4, 6,xp,xp,xu,xy,y0,z2,
	 22,xr,xe,z1,10,xs,z2, 6,xi,xm,y7,p1,xy,no,z2, 4,xt,xo,y0,y7,p1,xy,
	 z1,14,xt,is, 7, 2,xi,xu,y7,p1,xy,eq, 6, 2,y0,y7,p1,xy,z1,g2,xe,z1,
	 43,xo,eq, 5, 2,y0,is, 6, 2,xd,xg,y8,zz,z2, 4,xn,xa,y0,z2, 4,xr,xh,
	 y0,is, 4, 1,xh,y0,z1,12,xt,is, 8, 4,xa,xi,xs,xt,y8,zz,y0,y8,zz,z1,
	 g1,xs,z2, 5,xa,xg,y8,zz,z2, 5,xa,xi,y8,zz,z1,47,xu,z1,10,xc,is, 6,
	  2,xo,xr,y8,zz,y0,z1,10,xr,is, 6, 2,xi,xo,y8,zz,y0,z2, 5,xl,xp,y8,
	 zz,is, 7, 3,xi,xp,xt,y8,zz,eq, 5, 6,z1, 4,xb,y8,zz,y0,z2, 4,xe,xe,
	 y0,is, 8, 2,xe,xy,y8,p2,xi,xs,is, 5, 1,xs,y8,zz,z3, 8,xi,xr,xc,y8,
	 p2,xi,xs,z3, 8,xa,xt,xs,y8,p2,xi,xs,z4, 9,xa,xh,xp,xm,y8,p2,xi,xs,
	 z3, 6,xn,xe,xl,y8,zz,y0,z1,55,xh,is, 4, 1,xt,y0,z1,45,xc,eq, 5, 2,
	 y0,eq, 6,14,z2, 5,xi,xr,y8,zz,is, 5, 1,xn,y8,zz,y0,eq, 7,11,z2, 4,
	 xi,xl,y0,z2, 4,xe,xr,y0,z1, 9,xa,is, 6, 3,xd,xh,xr,y0,y8,zz,is, 5,
	  1,xx,y8,zz,z2, 5,xz,xt,y8,zz,y0,z1,48,xi,z2, 4,xn,xn,no,z2, 4,xm,
	 xe,y0,z2, 4,xs,xt,y0,z2, 4,xh,xc,y0,is, 6, 3,xn,xz,xq,y0,z1,17,xx,
	 z2, 4,xe,xh,no,z1, 8,xa,is, 5, 2,xl,xr,no,y0,no,z1,70,xu,z1, 8,xn,
	 z2, 4,xe,xm,y0,no,z2, 4,xa,xe,y0,z1,11,xt,is, 7, 4,xa,xc,xi,xo,no,
	 y0,z2, 4,xd,xn,y0,z2,11,xl,xu,is, 6, 3,xl,xt,xz,y0,no,z2, 4,xm,xu,
	 y0,z3, 5,xr,xu,xg,y0,z1,12,xa,z2, 4,xl,xu,y0,z1, 3,xu,y0,no,no,is,
	  5, 2,xs,xu,no,z1,39,xa,is, 5, 2,xv,xx,no,z2, 4,xs,xn,no,z2, 4,xi,
	 xl,no,z2, 4,xl,xg,no,z1,14,xm,is, 4, 1,xo,y0,is, 5, 2,xm,xe,y0,no,
	 y0,z2, 4,xr,xm,no,y0
	};

	static final short[] dropED = {
      2,xd,xe,z1,28,xi,eq, 4, 3,ya,zz,is, 5, 2,xx,xk,y0,z1,11,xt,is, 6,
      1,xp,y8,p1,xy,ya,zz,y8,p1,xy,z1,38,xe,z1,18,xr,eq, 4, 2,no,gt, 4,
      8,is, 6, 3,xb,xc,xg,no,ya,zz,z2, 5,xt,xn,ya,zz,eq, 4, 8,is, 6, 2,
     xp,xt,ya,zz,no,is, 5, 1,xu,ya,zz,z3,16,xg,xn,xi,eq, 6, 8,is, 6, 3,
     xr,xw,xk,y0,ya,zz,z1,19,xh,z1,14,xs,is, 6, 3,xr,xd,xk,no,z2, 4,xl,
     xo,no,y9,m1,z1,28,xb,z2, 4,xm,xa,no,is,10, 6,xi,xo,xu,xl,xm,xb,y9,
     m1,z1, 8,xr,is, 4, 1,xe,no,y0,no,z2, 4,xw,xn,no,eq, 4,59,is,11, 7,
     xc,xw,xo,xk,xs,xg,xy,ya,zz,is, 4, 1,xx,y0,z2, 7,xr,xb,ya,p2,xe,xd,
     z2, 7,xp,xs,ya,p2,xe,xd,is, 4, 1,xh,no,z1,18,xl,is, 4, 1,xs,no,is,
      6, 1,xf,ya,p1,xe,ya,p2,xe,xd,no,eq, 3,18,is, 7, 1,xf,ya,p2,xe,xd,
     is, 7, 1,xl,ya,p2,xa,xd,no,z3, 8,xl,xs,xi,ya,p2,xa,xd,z3, 8,xf,xr,
     xe,ya,p2,xe,xd,z2, 4,xm,xh,no,z1,15,xf,is, 8, 4,xa,xf,xi,xo,y9,m1,
     ya,p2,xe,xd,z1,39,xr,is, 7, 4,xf,xt,xh,xd,no,z2, 4,xa,xr,no,z1,10,
     xc,z3, 6,xa,xs,xs,ya,zz,no,z2, 7,xb,xn,ya,p2,xe,xd,z2, 4,xy,xt,y0,
     y9,m1
	};
 
	static final short[] dropING = {
      3,xg,xn,xi,eq, 4, 2,no,is, 5, 2,xe,xo,y0,z1,45,xy,z2, 4,xt,xp,y0,
     is, 7, 1,xt,y8,p2,xi,xe,z1,13,xd,is, 6, 3,xa,xo,xu,y0,y8,p2,xi,xe,
     z1,14,xl,is, 7, 4,xl,xf,xe,xp,y0,y8,p2,xi,xe,y0,eq, 5,47,z2, 6,xk,
     xe,y9,p1,xe,is, 9, 5,xc,xs,xu,xk,xx,y9,m1,z2, 6,xw,xo,y9,p1,xe,z2,
      6,xw,xa,y9,p1,xe,z2, 6,xg,xa,y9,p1,xe,z2, 6,xp,xa,y9,p1,xe,no,z1,
     35,xh,z1,21,xt,is, 5, 2,xy,xr,no,z2, 4,xo,xn,no,z2, 4,xe,xm,no,y9,
     p1,xe,z1, 8,xs,is, 5, 2,xd,xr,no,y9,m1,z3,44,xg,xn,xi,is, 7, 2,xh,
     xp,y9,p1,xe,z2, 6,xr,xf,y9,p1,xe,z2, 6,xr,xc,y9,p1,xe,z2, 6,xw,xt,
     y9,p1,xe,eq, 7, 8,is, 6, 1,xt,y9,p1,xe,y0,z3, 5,xk,xe,xp,no,z1,45,
     xr,z3, 5,xr,xe,xh,no,z3, 5,xr,xa,xe,no,z2, 6,xc,xa,y9,p1,xe,z2, 4,
     xy,xt,y0,z2,10,xu,xd,eq, 6, 2,no,y9,p1,xe,is, 5, 2,xp,xt,no,y9,m1,
     z2, 4,xw,xt,no,z2, 4,xn,xt,no,z3,10,xn,xn,xi,eq, 6, 2,no,y9,m1,z1,
      3,xj,no,z1,99,xl,z2, 9,xe,xg,z2, 4,xd,xu,y0,no,z2, 8,xr,xa,z1, 3,
     xn,y0,no,z2, 4,xr,xe,no,z2, 4,xh,xt,no,z2, 4,xb,xi,no,z3, 5,xp,xm,
     xu,no,z3, 5,xp,xa,xs,no,z5, 7,xp,xi,xr,xt,xs,no,z4, 6,xd,xn,xu,xo,
     no,z4, 6,xd,xe,xe,xs,no,z4, 6,xk,xc,xu,xd,no,z3, 5,xs,xo,xg,no,z3,
      5,xe,xr,xi,no,z2, 4,xt,xa,no,z2, 4,xf,xl,no,y9,m1
	};
 
	static final short[] restore = {
      0,z1,29,xs,is, 4, 1,xs,y0,z2, 7,xu,xb,eq, 3, 2,y0,z3, 5,xu,xc,xo,
     y0,z2, 4,xa,xi,y0,y9,p1,xe,z1,58,xg,is,12, 7,xa,xe,xd,xl,xr,xi,xu,
     y9,p1,xe,is, 5, 1,xg,y8,zz,z1,35,xn,z1,10,xo,is, 6, 1,xp,y9,p1,xe,
     y0,z1,18,xa,is, 6, 1,xr,y9,p1,xe,eq, 4, 2,y0,z2, 4,xl,xc,y0,y9,p1,
     xe,y0,z4, 6,xt,xe,xr,xp,y0,z3,29,xk,xc,xi,z2, 5,xn,xc,y8,zz,z2, 5,
     xf,xf,y8,zz,z2, 5,xt,xi,y8,zz,z2, 5,xm,xi,y8,zz,y0,is,12, 9,xt,xn,
     xr,xd,xb,xm,xp,xk,xz,sp,is, 8, 3,xc,xv,xu,y9,p1,xe,z1,g3,xl,z1,81,
     xl,z3,14,xo,xr,xt,z1, 3,xs,y0,eq, 5, 2,y0,y8,zz,z3, 6,xu,xn,xn,y8,
     zz,z1,35,xe,z1,13,xp,eq, 5, 2,y0,z2, 4,xs,xs,y0,y8,zz,is, 5, 1,xg,
     y8,zz,gt, 4,10,is, 8, 4,xc,xd,xb,xv,y8,zz,y0,z1,19,xa,z1, 4,xn,y8,
     zz,z2, 5,xh,xs,y8,zz,z2, 5,xt,xo,y8,zz,y0,is,15,10,xt,xs,xd,xg,xb,
     xp,xc,xf,xk,xz,y9,p1,xe,is, 4, 1,xe,y0,z1,31,xi,lt, 5, 2,v2,z2, 4,
     xr,xe,y0,z2, 6,xu,xg,y9,p1,xe,z1,11,xv,is, 5, 2,xi,xa,y0,y9,p1,xe,
     v2,z1,21,xa,is, 7, 4,xt,xv,xn,xd,y0,z2, 4,xu,xq,y0,z2, 4,xh,xs,y0,
     v2,v0,z2,12,xf,xa,is, 7, 2,xr,xh,y9,p1,xe,y0,z2,18,xw,xa,z2, 6,xn,
     xu,y9,p1,xe,z2, 6,xr,xe,y9,p1,xe,y0,z1,38,xh,z1,22,xc,z1, 9,xa,lt,
      5, 4,y9,p1,xe,y0,z1, 8,xi,lt, 6, 4,y9,p1,xe,y0,z1,12,xt,is, 9, 4,
     xa,xe,xi,xo,y9,p1,xe,y0
   	};
 
	static final short[] special = {
	  0,z1,73,xn,z1,21,xe,z1,17,xv,is, 7, 2,xn,xr,y9,p1,xe,z2, 6,xa,xr,
	 y9,p1,xe,y0,z1,47,xo,eq, 3, 4,y9,p1,xe,z3, 5,xd,xn,xa,y0,z2, 4,xd,
	 xr,y0,z2, 4,xr,xi,y0,z2, 4,xt,xt,y0,z2, 4,xb,xb,y0,is, 9, 6,xs,xo,
	 xi,xk,xm,xe,y0,y9,p1,xe,v0,z3, 5,xm,xo,xt,y0,z3, 5,xm,xo,xs,y0,z1,
	  9,xz,z1, 3,xt,y0,y9,p1,xe,z1,g4,xr,is, 8, 3,xt,xb,xd,y9,p1,xe,z1,
	 47,xe,z2,22,xv,xe,is, 6, 1,xr,y9,p1,xe,eq, 5, 2,y0,z2, 4,xl,xi,y0,
	 y9,p1,xe,z2, 6,xh,xd,y9,p1,xe,z2, 6,xh,xo,y9,p1,xe,z2, 6,xf,xr,y9,
	 p1,xe,y0,z1,66,xo,z1,12,xb,is, 6, 3,xr,xa,xh,y0,y9,p1,xe,z1,12,xh,
	 is, 6, 3,xt,xb,xc,y0,y9,p1,xe,z1,10,xt,is, 6, 1,xs,y9,p1,xe,y0,is,
	  8, 5,xs,xr,xv,xo,xm,y0,z2, 4,xn,xo,y0,z2, 4,xl,xo,y0,z2, 4,xl,xi,
	 y0,y9,p1,xe,z3, 5,xa,xl,xl,y0,z3, 5,xa,xt,xr,y0,v0,z1,g5,xt,z2,14,
	 xs,xa,is, 9, 4,xw,xp,xt,xb,y9,p1,xe,y0,z2,18,xa,xe,z2, 6,xr,xc,y9,
	 p1,xe,z2, 6,xm,xr,y9,p1,xe,y0,z1,89,xi,eq, 3, 4,y9,p1,xe,is, 5, 2,
	 xf,xx,y0,z1,18,xb,z2, 4,xr,xo,y0,z2, 4,xa,xh,y0,z2, 4,xi,xh,y0,v2,
	 z1,11,xs,z3, 7,xo,xp,xm,y9,p1,xe,y0,z2, 4,xm,xi,y0,z2, 4,xc,xi,y0,
	 z2, 4,xr,xi,y0,z2, 4,xr,xe,y0,z2, 4,xd,xu,y0,z2,17,xd,xe,eq, 4, 2,
	 y0,is, 6, 3,xn,xe,xr,y0,y9,p1,xe,v2,z1,25,xo,is,10, 5,xm,xn,xd,xt,
	 xu,y9,p1,xe,z1,10,xv,is, 4, 1,xi,y0,y9,p1,xe,y0,z2, 6,xa,xi,y9,p1,
	 xe,z2, 6,xa,xu,y9,p1,xe,z3, 5,xa,xb,xm,y0,z1,30,xe,eq, 3, 4,y9,p1,
	 xe,z2, 4,xl,xl,y0,is, 7, 2,xl,xr,y9,p1,xe,z3, 7,xp,xm,xo,y9,p1,xe,
	 y0,v0,z3, 7,xd,xa,xu,y9,p1,xe,z3, 7,xd,xi,xu,y9,p1,xe,z3,17,xp,xo,
	 xl,is, 4, 1,xl,y0,z2, 4,xe,xv,y0,y9,p1,xe,v1
	};
 
	static final short[] undouble = {
	  0,eq, 2, 8,is, 4, 1,xp,y0,ya,zz,eq, 3,36,z1,16,xz,is, 6, 2,xa,xu,
	 ya,zz,z2, 5,xi,xf,ya,zz,y0,z2,10,xr,xu,is, 4, 1,xf,y0,ya,zz,z3, 6,
	 xt,xu,xb,ya,zz,z4, 7,xt,xo,xc,xy,ya,zz,y0
	};
	
}