package com.google.ar.core.examples.java.helloar;

import android.util.Log;


/**
 * Written by Bartosz Szczygiel <eziosoft@gmail.com>
 * Created on 01/10/2018.
 */
public class Target {
    float x, y, z;
    float range = 0.1f;

    public boolean isInRange(Robot robot) {
        float deltaX = Math.abs(x - robot.x) * Math.abs(x - robot.x);
        float deltaZ = Math.abs(z - robot.z) * Math.abs(z - robot.z);
        float distance = (float) Math.sqrt(deltaX + deltaZ);
        Log.d("aaa", "isInRange: " + range);
        return distance < range;
    }

    public Point getPoint() {
        return new Point(x, y, z);
    }

    public void setPoint(com.google.ar.core.examples.java.helloar.Point point) {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
    }

}
