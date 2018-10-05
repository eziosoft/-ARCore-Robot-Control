package com.google.ar.core.examples.java.helloar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Written by Bartosz Szczygiel <eziosoft@gmail.com>
 * Created on 25/09/2018.
 */
public class CustomView extends android.support.v7.widget.AppCompatImageView {

    private int mWidth;
    private int mHeight;
    private int viewScale = 100;
    private int viewOffsetX, viewOffsetZ;
    private final Point cameraPosition = new Point(0f, 0f, 0f);
    private final Point cameraLastPosition = new Point(0f, 0f, 0f);
    private long lastMillis = 0;
    float speed_ms = 0;
    MovingAverage speedAvg = new MovingAverage(5);
    private float cameraHeading;

    private final Paint paint = new Paint();
    private final Paint paint1 = new Paint();
    private final Paint paint2 = new Paint();

    private final SparseArray<Plane> planes = new SparseArray<>();
    private final SparseArray<Point> points = new SparseArray<>();
    private final List<Point> cameraTracePoints = new ArrayList<>();

    Robot robot;
    private com.google.ar.core.examples.java.helloar.Target target;

    public void addPlanes(Collection<Plane> allPlanes) {
        synchronized (planes) {
            for (Plane plane : allPlanes) {
                if (plane.getTrackingState() != TrackingState.TRACKING || plane.getSubsumedBy() != null) {
                    continue;
                }

                if (plane.getType() == Plane.Type.HORIZONTAL_UPWARD_FACING)
                    planes.put(plane.hashCode(), plane);
            }
        }
    }


    public void addPoint(Point point) {
        synchronized (points) {
            while (points.size() > 10000) {
                points.delete(points.keyAt(ThreadLocalRandom.current().nextInt(0, 10000 + 1)));
            }
            points.put(point.hashCode(), point);
        }
    }


    public void setPosition(Point position, float heading) {
        synchronized (position) {
            this.cameraHeading = heading;
            this.cameraPosition.set(position);

            float px = viewScale * position.x + mWidth / 2;
            float pz = viewScale * position.z + mHeight / 2;

            viewOffsetX = (int) (mWidth / 2 - px);
            viewOffsetZ = (int) (mHeight / 2 - pz);


            speed_ms = speedAvg.next(cameraLastPosition.getDistanceTo(cameraPosition.getPoint()) / (System.currentTimeMillis() - lastMillis) * 1000);
            lastMillis = System.currentTimeMillis();
            cameraLastPosition.set(cameraPosition.getPoint());
        }

        synchronized (cameraTracePoints) {
            cameraTracePoints.add(position.getPoint());
            if (cameraTracePoints.size() > 100)
                cameraTracePoints.remove(0);
        }
    }


    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        paint1.setColor(Color.GREEN);
        paint1.setAlpha(50);

        paint2.setStrokeWidth(2);
        paint2.setColor(Color.YELLOW);
        paint2.setStyle(Paint.Style.STROKE);

        robot = new Robot();
        target = new com.google.ar.core.examples.java.helloar.Target();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;


    }

    private final Path pathTmp = new Path();
    private float[] buf = new float[3];
    private FloatBuffer fb;
    private Plane plane;

    protected void onDraw(Canvas c) {

        //draw cross in the middle
        c.drawLine(getX(-1f), getZ(0), getX(1f), getZ(0), paint1);
        c.drawLine(getX(0f), getZ(-1f), getX(0), getZ(1f), paint1);


        synchronized (planes) {
            for (int i = 0; i < planes.size(); i++) {
                plane = planes.valueAt(i);


                fb = plane.getPolygon();
                pathTmp.reset();
                while (fb.hasRemaining()) {
                    buf[0] = fb.get();
                    buf[2] = fb.get();

                    buf = plane.getCenterPose().transformPoint(buf);
                    if (pathTmp.isEmpty())
                        pathTmp.moveTo(viewOffsetX + buf[0] * viewScale + mWidth / 2, viewOffsetZ + buf[2] * viewScale + mHeight / 2);
                    else
                        pathTmp.lineTo(viewOffsetX + buf[0] * viewScale + mWidth / 2, viewOffsetZ + buf[2] * viewScale + mHeight / 2);
                }
                c.drawPath(pathTmp, paint1);
            }
        }


        paint.setStrokeWidth(2);
        boolean collision = false;
        synchronized (points) {
            for (int i = 0; i < points.size(); i++) {
                if (Math.abs(points.valueAt(i).y - cameraPosition.y) < 0.2) {
                    paint.setColor(Color.GREEN);
                    if (points.valueAt(i).getDistanceTo(cameraPosition.getPoint()) < 1)
                        collision = true;
                } else
                    paint.setColor(Color.GRAY);
                c.drawPoint(viewOffsetX + (points.valueAt(i).x) * viewScale + mWidth / 2, viewOffsetZ + (points.valueAt(i).z) * viewScale + mHeight / 2, paint);
            }
        }

        paint.setStrokeWidth(5);
        paint.setColor(collision ? Color.RED : Color.GREEN);
        synchronized (cameraPosition) {
            float px = viewOffsetX + viewScale * cameraPosition.x + mWidth / 2;
            float pz = viewOffsetZ + viewScale * cameraPosition.z + mHeight / 2;

            if (px < 0 || px > mWidth || pz < 0 || pz > mHeight) viewScale--;
            c.drawCircle(px, pz, 10, paint);
            c.drawLine(px, pz, (float) (px - 1 * viewScale * Math.sin((cameraHeading))), (float) (pz - 1 * viewScale * Math.cos((cameraHeading))), paint); // line is 1m long
        }


        Object p[] = cameraTracePoints.toArray();
        pathTmp.reset();
        for (Object aP : p) {
            float px = viewOffsetX + viewScale * ((Point) aP).x + mWidth / 2;
            float pz = viewOffsetZ + viewScale * ((Point) aP).z + mHeight / 2;
            if (pathTmp.isEmpty()) pathTmp.moveTo(px, pz);
            else
                pathTmp.lineTo(px, pz);
        }
        c.drawPath(pathTmp, paint2);

        target.setPoint(cameraPosition);
        robot.setTarget(target);
        robot.move();
        float px = (viewOffsetX + viewScale * robot.x + mWidth / 2);
        float pz = (viewOffsetZ + viewScale * robot.z + mHeight / 2);
        c.drawCircle(px, pz, 15, robot.paint);
        c.drawLine(px, pz, (float) (px + 50 * Math.sin((robot.heading))), (float) (pz + 50 * Math.cos((robot.heading))), paint);
        c.drawText(String.valueOf(robot.distanceToTarget()), px, pz, paint);

    }


    public int getmWidth() {
        return mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public int getPointsCount() {
        return points.size();
    }

    public int getPlanesCount() {
        synchronized (planes) {
            return planes.size();
        }
    }

    public int getViewScale() {
        return viewScale;
    }

    public void setViewScale(int viewScale) {
        this.viewScale = viewScale;
    }

    float getX(Point point) {
        return viewOffsetX + viewScale * point.x + mWidth / 2;
    }


    float getZ(Point point) {
        return viewOffsetZ + viewScale * point.z + mHeight / 2;
    }


    float getX(float x) {
        return viewOffsetX + viewScale * x + mWidth / 2;
    }


    float getZ(float z) {
        return viewOffsetZ + viewScale * z + mHeight / 2;
    }
}

