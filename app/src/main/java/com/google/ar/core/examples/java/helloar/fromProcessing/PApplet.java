package com.google.ar.core.examples.java.helloar.fromProcessing;

/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2012-17 The Processing Foundation
  Copyright (c) 2004-12 Ben Fry and Casey Reas
  Copyright (c) 2001-04 Massachusetts Institute of Technology

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License version 2.1 as published by the Free Software Foundation.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

//package processing.core;

import java.util.Random;

public class PApplet {

    Random internalRandom;



    static public final float sin(float angle) {
        return (float)Math.sin(angle);
    }

    static public final float cos(float angle) {
        return (float)Math.cos(angle);
    }


    /**
     * ( begin auto-generated from random.xml )
     *
     * Generates random numbers. Each time the <b>random()</b> function is
     * called, it returns an unexpected value within the specified range. If
     * one parameter is passed to the function it will return a <b>float</b>
     * between zero and the value of the <b>high</b> parameter. The function
     * call <b>random(5)</b> returns values between 0 and 5 (starting at zero,
     * up to but not including 5). If two parameters are passed, it will return
     * a <b>float</b> with a value between the the parameters. The function
     * call <b>random(-5, 10.2)</b> returns values starting at -5 up to (but
     * not including) 10.2. To convert a floating-point random number to an
     * integer, use the <b>int()</b> function.
     *
     * ( end auto-generated )
     * @webref math:random
     * @param low lower limit
     * @param high upper limit
     * @see PApplet#randomSeed(long)
     * @see PApplet#noise(float, float, float)
     */
    public final float random(float low, float high) {
        if (low >= high) return low;
        float diff = high - low;
        float value = 0;
        // because of rounding error, can't just add low, otherwise it may hit high
        // https://github.com/processing/processing/issues/4551
        do {
            value = random(diff) + low;
        } while (value == high);
        return value;
    }



    /**
     *
     */
    public final float random(float high) {
        // avoid an infinite loop when 0 or NaN are passed in
        if (high == 0 || high != high) {
            return 0;
        }

        if (internalRandom == null) {
            internalRandom = new Random();
        }

        // for some reason (rounding error?) Math.random() * 3
        // can sometimes return '3' (once in ~30 million tries)
        // so a check was added to avoid the inclusion of 'howbig'
        float value = 0;
        do {
            value = internalRandom.nextFloat() * high;
        } while (value == high);
        return value;
    }

    static public final float lerp(float start, float stop, float amt) {
        return start + (stop-start) * amt;
    }
}

//