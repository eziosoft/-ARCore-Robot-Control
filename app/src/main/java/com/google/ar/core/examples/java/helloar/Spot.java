package com.google.ar.core.examples.java.helloar;

import java.util.ArrayList;
import java.util.List;

/**
 * Written by Bartosz Szczygiel <eziosoft@gmail.com>
 * Created on 19/10/2018.
 */
public class Spot {

    public Point location = new Point(0, 0, 0);
    public boolean obstacle = false;
    private List<Spot> neighbors = new ArrayList<>();

    // f, g, and h values for A*
    float f = 0;
    float g = 0;
    float h = 0;

    int i, j;

    // Where did I come from?
    public Spot previous;

    public Spot() {
        previous = null;
    }

    // Did the maze algorithm already visit me?
    boolean visited = false;

    public List<Spot> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<Spot> neighbors) {
        this.neighbors = neighbors;
    }
}
