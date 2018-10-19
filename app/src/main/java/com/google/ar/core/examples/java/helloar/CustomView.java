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


    int degreesCounter = 0;

    protected void onDraw(Canvas c) {
        drawBackground(c);
        drawPlanes(c);
        boolean collision = drawPoints(c, robot.getPoint(), 0.5f, 0.2f);
        drawCamera(c, collision);
        drawTrace(c);

        //move target in figure 8
        degreesCounter++;
        if (degreesCounter > 360) degreesCounter = 1;
        target.x = (float) Math.sin(Math.toRadians(degreesCounter)) + cameraPosition.x;
        target.z = (float) (Math.sin(Math.toRadians(2 * degreesCounter))) + cameraPosition.z;
        target.draw(c, getX(target.x), 0, getZ(target.z), viewScale);


        robot.setTarget(target);
        robot.y = cameraPosition.y;
        robot.move();
        robot.draw(c, getX(robot.x), robot.y, getZ(robot.z));
        addTracePoint(robot.getPoint());
    }


    private void drawBackground(Canvas c) {
        //draw cross in the middle
        c.drawLine(getX(-1f), getZ(0), getX(1f), getZ(0), paint1);
        c.drawLine(getX(0f), getZ(-1f), getX(0), getZ(1f), paint1);

        float gridSize = 0.2f; // in m
        float gridArea = 5;

        // gridArea = (float) Math.sqrt(gridArea );
        paint1.setStyle(Paint.Style.STROKE);
        for (float i = -gridArea; i < gridArea; i += gridSize) {
            for (float j = -gridArea; j < gridArea; j += gridSize) {
                if (isInside(getX(i), getZ(j)))
                    c.drawRect(getX(i), getZ(j), getX(i + gridSize), getZ(j + gridSize), paint1);
            }
        }

    }

    private void drawPlanes(Canvas c) {
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
                        pathTmp.moveTo(getX(buf[0]), getZ(buf[2]));
                    else
                        pathTmp.lineTo(getX(buf[0]), getZ(buf[2]));
                }
                c.drawPath(pathTmp, paint1);
            }
        }
    }


    private boolean drawPoints(Canvas c, Point robot, float collisionDistance, float Ydistance) {
        paint.setStrokeWidth(2);
        boolean collision = false;
        synchronized (points) {
            for (int i = 0; i < points.size(); i++) {
                if (Math.abs(points.valueAt(i).y - robot.y) < Ydistance) {
                    paint.setStrokeWidth(1 * viewScale * 0.2f);
                    paint.setColor(Color.MAGENTA);
                    if (points.valueAt(i).getDistanceTo(robot.getPoint()) < collisionDistance) {
                        collision = true;
                        c.drawLine(getX(points.valueAt(i).x), getZ(points.valueAt(i).z), getX(robot.x), getZ(robot.z), paint1);
                    }
                } else {
                    paint.setStrokeWidth(2);
                    paint.setColor(Color.GRAY);
                }
                if (isInside(getX(points.valueAt(i).x), getZ(points.valueAt(i).z)))
                    c.drawPoint(getX(points.valueAt(i).x), getZ(points.valueAt(i).z), paint);
            }
        }

        return collision;
    }


    private void drawCamera(Canvas c, boolean collision) {
        //draw camera
        paint.setStrokeWidth(5);
        paint.setColor(collision ? Color.RED : Color.GREEN);
        synchronized (cameraPosition) {
            if (getX(cameraPosition.x) < 0 || getX(cameraPosition.x) > mWidth || getZ(cameraPosition.z) < 0 || getZ(cameraPosition.z) > mHeight)
                viewScale--;
            c.drawCircle(getX(cameraPosition.x), getZ(cameraPosition.z), 10, paint);
            c.drawLine(getX(cameraPosition.x), getZ(cameraPosition.z), (float) (getX(cameraPosition.x) - 1 * viewScale * Math.sin((cameraHeading))), (float) (getZ(cameraPosition.z) - 1 * viewScale * Math.cos((cameraHeading))), paint); // line is 1m long
        }
    }

    private void drawTrace(Canvas c) {
        //draw camera trace
        Object p[] = cameraTracePoints.toArray();
        pathTmp.reset();
        for (Object aP : p) {
            if (pathTmp.isEmpty()) pathTmp.moveTo(getX(((Point) aP).x), getZ(((Point) aP).z));
            else
                pathTmp.lineTo(getX(((Point) aP).x), getZ(((Point) aP).z));
        }
        c.drawPath(pathTmp, paint2);
    }


    private void addTracePoint(Point point) {
        synchronized (cameraTracePoints) {
            cameraTracePoints.add(point);
            if (cameraTracePoints.size() > 100)
                cameraTracePoints.remove(0);
        }
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


    boolean isInside(float x, float z) {
        return (x > 0 && x < mWidth && z > 0 && z < mHeight);
    }
}

