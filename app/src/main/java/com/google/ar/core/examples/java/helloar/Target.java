package com.google.ar.core.examples.java.helloar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;


/**
 * Written by Bartosz Szczygiel <eziosoft@gmail.com>
 * Created on 01/10/2018.
 */
public class Target {
    public float x, y, z;
    public float range = 0.1f;

    private Paint paint = new Paint();

    public Target() {
        this.paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
    }

    public boolean isInRange(Robot robot) {
        float deltaX = Math.abs(x - robot.x) * Math.abs(x - robot.x);
        float deltaZ = Math.abs(z - robot.z) * Math.abs(z - robot.z);
        float distance = (float) Math.sqrt(deltaX + deltaZ);
//        Log.d("aaa", "isInRange: " + range);
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

    public void draw(Canvas c, float x, float y, float z, float viewScale) {
        c.drawCircle(x, z, range * viewScale, paint);
        c.drawLine(x - viewScale * range, z - viewScale * range, x + viewScale * range, z + viewScale * range, paint);
        c.drawLine(x + viewScale * range, z - viewScale * range, x - viewScale * range, z + viewScale * range, paint);
    }

}
