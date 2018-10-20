package com.google.ar.core.examples.java.helloar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.ar.core.examples.java.helloar.fromProcessing.PVector;
import com.google.ar.core.examples.java.helloar.helpers.MiniPID;

/**
 * Written by Bartosz Szczygiel <eziosoft@gmail.com>
 * Created on 28/09/2018.
 */
class Robot {
    float FB; //forward backward
    float LR; //left right
    private MiniPID pidLR = new MiniPID(0.25, 0.008, 0.001);
    private MiniPID pidSpeed = new MiniPID(0.25, 0.003, 0.001);

    float x = 1, y, z = -1, heading, speed;

    Target target = new Target();
    Paint paint = new Paint();


    Robot() {
        paint.setColor(Color.BLUE);
    }


    void move() {
        stear();
        speed();

        if (target.isInRange(this)) speed = 0;
        //move for simulation
        x = (float) (x + speed * Math.sin(heading));
        z = (float) (z + speed * Math.cos(heading));


        //change color if target is in range
        if (target.isInRange(this)) {
            paint.setColor(Color.YELLOW);
        } else {
            paint.setColor(Color.BLUE);
        }

    }


    private void stear() {
        float headingToTarget = headingToTarget();
        if ((headingToTarget - heading) < Math.toRadians(-180))
            headingToTarget += Math.toRadians(360);
        else if ((headingToTarget - heading) > Math.toRadians(180))
            headingToTarget -= Math.toRadians(360);


        pidLR.setSetpoint(headingToTarget);
        pidLR.setOutputLimits(1);
        LR = (float) pidLR.getOutput(heading);

        //for simulation
        heading += LR * 0.5;
//        if (heading > Math.toRadians(360) || heading < 0) heading = 0;
    }

    private void speed() {
        pidSpeed.setSetpoint(0);
        pidSpeed.setOutputLimits(1);
        FB = (float) pidSpeed.getOutput(getPoint().getDistance2D(target.getPoint()));

        //for simulation
        speed = (-0.1f * FB);
    }


    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    private float headingToTarget() {
        float i = (float) Math.atan2((target.x - x), (target.z - z));
        return i;
    }

//    float distanceToTarget() {
//        float deltaX = (x - target.x) * (x - target.x);
//        float deltaY = (z - target.z) * (z - target.z);
//        return (float) Math.sqrt(deltaX + deltaY);
//    }

    public void draw(Canvas c, float x, float y, float z) {
        c.drawCircle(x, z, 15, paint);
        c.drawLine(x, z, (float) (x + 50 * Math.sin(heading)), (float) (z + 50 * Math.cos(heading)), paint);
//        c.drawText(String.valueOf(distanceToTarget()), x, z, paint);
    }


    public Point getPoint() {
        return new Point(x, y, z);
    }
}
