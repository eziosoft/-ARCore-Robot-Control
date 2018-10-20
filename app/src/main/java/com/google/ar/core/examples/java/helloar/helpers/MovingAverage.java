package com.google.ar.core.examples.java.helloar.helpers;

//https://stackoverflow.com/questions/3793400/is-there-a-function-in-java-to-get-moving-average
public class MovingAverage {
    private float[] window;
    private int n, insert;
    private float sum;

    public MovingAverage(int size) {
        window = new float[size];
        insert = 0;
        sum = 0;
    }

    public float next(float val) {
        if (n < window.length) n++;
        sum -= window[insert];
        sum += val;
        window[insert] = val;
        insert = (insert + 1) % window.length;
        return sum / n;
    }
}
