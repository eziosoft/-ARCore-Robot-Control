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
    public List<Spot> neighbours= new ArrayList<>();

}
