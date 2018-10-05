package com.google.ar.core.examples.java.helloar;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Written by Bartosz Szczygiel <eziosoft@gmail.com>
 * Created on 28/09/2018.
 */
class Robot {
    float FB; //forward backward
    float LR; //left right
    private MiniPID pidLR = new MiniPID(0.25, 0.00, 0.0);
    private MiniPID pidSpeed = new MiniPID(0.25, 0.00, 0.0);

    float x = 1, y, z = -1, heading, speed;
    Target target = new Target();
    Paint paint = new Paint();


    Robot() {
        paint.setColor(Color.BLUE);

    }

    void move() {
        float headingToTarget = headingToTarget();
        if ((headingToTarget - heading) < Math.toRadians(-180))
            headingToTarget += Math.toRadians(360);
        else if ((headingToTarget - heading) > Math.toRadians(180))
            headingToTarget -= Math.toRadians(360);


        pidLR.setSetpoint(headingToTarget);
        pidLR.setOutputLimits(1);

        LR = (float) pidLR.getOutput(heading);

        heading += LR * 0.1;

        if (target.isInRange(this)) {
            paint.setColor(Color.YELLOW);
        } else {
            paint.setColor(Color.BLUE);
        }

        pidSpeed.setSetpoint(0);
        pidSpeed.setOutputLimits(1);

        FB = (float) pidSpeed.getOutput(distanceToTarget());

        speed = (float) (-0.1f * FB);
        x = (float) (x + speed * Math.sin(heading));
        z = (float) (z + speed * Math.cos(heading));


        //if (heading > 2 * Math.PI) heading = 0;
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

    float distanceToTarget() {
        float deltaX = (x - target.x) * (x - target.x);
        float deltaY = (z - target.z) * (z - target.z);
        return (float) Math.sqrt(deltaX + deltaY);
    }
}
