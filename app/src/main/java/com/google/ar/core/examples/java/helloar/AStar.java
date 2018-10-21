package com.google.ar.core.examples.java.helloar;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Written by Bartosz Szczygiel <eziosoft@gmail.com>
 * Created on 21/10/2018.
 */
public class AStar {
    private List<Spot> openSet = new ArrayList<>();
    private List<Spot> closedSet = new ArrayList<>();
    private Spot current;
    private Spot lastCheckedNode;
    private Spot end;


    public void setStartSpot(Spot start) {
        start.obstacle = false;
        openSet.add(start);
    }

    public void setEnd(Spot end) {
        end.obstacle = false;
        this.end = end;
    }

    public void reset() {
        openSet.clear();
        closedSet.clear();
        current = null;
        lastCheckedNode = null;
        end = null;
    }

    public int step() {
        if (openSet.size() > 0) {

            // Best next option
            int winner = 0;
            for (int i = 1; i < openSet.size(); i++) {
                if (openSet.get(i).f < openSet.get(i).f) {
                    winner = i;
                }
                //if we have a tie according to the standard heuristic
                if (openSet.get(i).f == openSet.get(winner).f) {
                    //Prefer to explore options with longer known paths (closer to goal)
                    if (openSet.get(i).g > openSet.get(winner).g) {
                        winner = i;
                    }
                    //if we're using Manhattan distances then also break ties
                    //of the known distance measure by using the visual heuristic.
                    //This ensures that the search concentrates on routes that look
                    //more direct. This makes no difference to the actual path distance
                    //but improves the look for things like games or more closely
                    //approximates the real shortest path if using grid sampled data for
                    //planning natural paths.

                }
            }
            current = openSet.get(winner);
            lastCheckedNode = current;

            // Did I finish?
            if (current == end) {

                log("DONE!");
                return 1;
            }

            // Best option moves from openSet to closedSet
            openSet.remove(current);
            closedSet.add(current);

            // Check all the neighbors
            List<Spot> neighbors = current.getNeighbors();

            for (int i = 0; i < neighbors.size(); i++) {
                Spot neighbor = neighbors.get(i);

                // Valid next spot?
                if (!closedSet.contains(neighbor)) {
                    // Is this a better path than before?
                    float tempG = current.g + heuristic(neighbor, current);

                    // Is this a better path than before?
                    if (!openSet.contains(neighbor) && !neighbor.obstacle) {
                        openSet.add(neighbor);
                    } else if (tempG >= neighbor.g) {
                        // No, it's not a better path
                        continue;
                    }

                    neighbor.g = tempG;
                    neighbor.h = heuristic(neighbor, end);
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.previous = current;
                }

            }
            return 0;
            // Uh oh, no solution
        } else {

            log("no solution");
            return -1;
        }
    }

    private float heuristic(Spot a, Spot b) {
        return  Math.abs(a.i - b.i) + Math.abs(a.j - b.j);
        //return a.location.getDistance2D(b.location);
    }


    public List<Spot> calcPath(Spot endNode) {
        // Find the path by working backwards
        List<Spot> path = new ArrayList<>();
        Spot temp = endNode;
        path.add(temp);
        while (temp.previous != null) {
            path.add(temp.previous);
            temp = temp.previous;
        }
        return path;
    }


    private void log(String s) {
        Log.d("aaa", "log: " + s);
    }


    public List<Spot> getOpenSet() {
        return openSet;
    }

    public List<Spot> getClosedSet() {
        return closedSet;
    }

    public Spot getCurrent() {
        return current;
    }

    public Spot getLastCheckedNode() {
        return lastCheckedNode;
    }

    public Spot getEnd() {
        return end;
    }
}




