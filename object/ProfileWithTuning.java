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
// ProfileWithTuning.java : 09Oct02 CPM
// for incremental retraining of profile

package object;

import aw.*;

//  profile with adjustment of weights to particular target vectors

public class ProfileWithTuning extends ProfileWithSimilarity {

    private int[]   xa = new int[Profile.MXP]; // index array for sorting profile weights
    private int     nx; // number of non-zero weights for n-gram indices
    private float[] pp; // packed probabilities for indices
    private short[] pw; // profile weigths
    
    // initialize

    public ProfileWithTuning ( Profile pro, float[] pp ) {
        super(pro);
        sortWeights();
        this.pp = pp;
    }
    
    // initialize
    
    public ProfileWithTuning ( Profile pro ) throws AWException {
        super(pro);
        sortWeights();
        PackedProbabilities ps = new PackedProbabilities(gms,nx);
        pp = ps.pack();
    }
    
    private void sortWeights ( ) {
        for (nx = 0; nx < Profile.MXP; nx++) {
            if (gms[nx] > Parameter.MXI)
                break;
            int k = nx - 1;
            for (; k >= 0; --k) {
                if (wts[xa[k]] >= wts[nx])
                    break;
                xa[k+1] = xa[k];
            }
            xa[k+1] = nx;
        }
        pw = new short[nx];
    }
    
    // adjust to specified vector
    
    private static final byte minus2 = (byte) -2; // increments predefined
    private static final byte  plus2 = (byte)  2; //
    private static final byte minus1 = (byte) -1; //
    private static final byte  plus1 = (byte)  1; //
    
    public void reweightFor ( byte[] v, int vl ) {
        byte delta = 0;
        float ref = match(v,vl);
        for (int i = 1; i < nx; i++) {
            adjust(i,minus1);
            float sim;
            sim = match(v,vl);
            if (ref < sim) {
                ref = sim;
                delta = minus1;
            }
            else {
                adjust(i,plus2);
                sim = match(v,vl);
                if (ref < sim) {
                    ref = sim;
                    delta = plus1;
                }
                else {
                    adjust(i,minus1);
                    continue;
                }
            }
            for (;;) {
                adjust(i,delta);
                sim = match(v,vl);
                if (ref >= sim)
                    break;
                ref = sim;
            }
            adjust(i,(byte) -delta);
        }
    }
    
    // change weights incrementally and update noise model
    
    private void adjust ( int n, byte d ) {
        for (; n < nx; n++) {
            int k = xa[n];
            int w = wts[k] + d;
            if (w >= 0 && w <= 127)
                wts[k] = (byte) w;
        }
        for (int i = 0; i < nx; i++)
            pw[i] = wts[i];
        remodel();
    }
    
    // recompute model parameters
        
    private void remodel ( ) {
        ProfileModel mod = new ProfileModel(nx,pw,pp);
        uexp = mod.expectedValue();
        uvar = mod.variance();
    }
    
    // increment or decrement weights toward those of another profile
    
    public void adaptTo ( Profile p, double c ) {
        int sum = 0;
        for (int i = 0, j = 0; i < nx;) {
            int g = p.gms[j];
            if (g > Parameter.MXI)
                break;
            else if (gms[i] < g) {
                pw[i] = 0;
                i++;
            }
            else if (gms[i] > g)
                j++;
            else {
                pw[i] = (short)(wts[i]*(p.wts[j] - wts[i]));
                sum += wts[i];
                i++; j++;
            }
        }
        int mw = 0;
        for (int i = 0; i < nx; i++) {
            double dw = (pw[i]*c)/sum;
            int rw = (int)(wts[i] + dw);
            if (rw < 0)
                rw = 0;
            if (mw < rw)
                mw = rw;
            pw[i] = (short) rw;
        }
        if (mw != 127) {
            int de = mw - 127;
            for (int i = 0; i < nx; i++) {
                int nw = pw[i] - de;
                if (nw < 0)
                    nw = 0;
                pw[i] = (short) nw;
            }
        }
        for (int i = 0; i < nx; i++)
            wts[i] = (byte) pw[i];
        remodel();
    }
    
    // assign tuning changes to another profile
    
    public void assignTo ( Profile p ) {
        p.uexp = uexp;
        p.uvar = uvar;
        p.gms  = gms;
        p.wts  = wts;
    }

}
