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
// IndexVectorCentroid.java : 06Oct02 CPM
//

package object;

import aw.Parameter;
import aw.AWException;

public class IndexVectorCentroid {

    public static SimpleIndexVector from (
        SimpleIndexVector[] vs,
        int[] ws,
        int   n
    ) throws AWException {
        short[] fv = new short[Parameter.MXI];
        byte [] cv = new byte [Parameter.MXI];
        int k = vs.length;
        for (int i = 0; i < k; i++) {
            ClusterIndexVector cliv = new ClusterIndexVector(vs[i]);
            cliv.aggregate(ws[i],fv,cv);
        }
        for (int j = 0; j < Parameter.MXI; j++)
            if (cv[j] == 1)
                cv[j] = 0;
            else if (cv[j] > 1) {
                int x = (fv[j]<<1)/k;
                cv[j] = (byte)(x>>1);
            }
        return new SimpleIndexVector(cv,10);
     }

}
