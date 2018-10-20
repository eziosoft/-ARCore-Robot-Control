package com.google.ar.core.examples.java.helloar;

import android.graphics.Paint;

/**
 * Written by Bartosz Szczygiel <eziosoft@gmail.com>
 * Created on 19/10/2018.
 */
public class Map {
    public Spot[][] spots;
    public int size = 10;
    public float spot_size_m = 0.1f; //in m


    public Map(int size, float spot_size_m) {
        this.size = size;
        this.spot_size_m = spot_size_m;
        spots = new Spot[size][size];
        clear();
    }


    public void clear() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                spots[i][j] = new Spot();
                spots[i][j].location.x = (i - size / 2f) * spot_size_m;
                spots[i][j].location.y = 0;
                spots[i][j].location.z = (j - size / 2f) * spot_size_m;
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

            }
        }
    }

    public void setObstacle(Point point, boolean obstacle) {
        if (getSpot(point) != null)
            getSpot(point).obstacle = obstacle;
    }

    public Spot getSpot(Point point) {
        int i = (int) (point.x / spot_size_m + size / 2);
        int j = (int) (point.z / spot_size_m + size / 2);

        if (i > 0 && j > 0 && i < size && j < size)
            return spots[i][j];
        else
            return null;
    }
}
