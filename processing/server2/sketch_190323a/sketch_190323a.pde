import peasy.*;
import peasy.org.apache.commons.math.*;
import peasy.org.apache.commons.math.geometry.*;
import processing.net.*;


PeasyCam cam;
PShader pointShader;
PShape shp;
ArrayList<PVector> vectors = new ArrayList<PVector>();

Server myServer;
float cx, cy, cheading;


void setup() {
  myServer = new Server(this, 65432);

  size(1300, 900, P3D);
  frameRate(1000);
  smooth(8);

  cam = new PeasyCam(this, 500);
  cam.setMaximumDistance(width);
  perspective(60 * DEG_TO_RAD, width/float(height), 2, 6000);

  double d = cam.getDistance()*3;

  pointShader = loadShader("pointfrag.glsl", "pointvert.glsl");
  pointShader.set("maxDepth", (float) d);


  //for (int i = 0; i < 100000; i++) {
  //  vectors.add(new PVector(random(width), random(width), random(width)));
  //}

  shader(pointShader, POINTS);
  strokeWeight(2);
  stroke(255);

  shp = createShape();
  shp.beginShape(POINTS);
  shp.translate(-width/2, -width/2, -width/2);  
  for (PVector v : vectors) {
    shp.vertex(v.x, v.y, v.z);
  }
  shp.endShape();
}

void draw() {
  background(0);
  shape(shp, 0, 0);

  println(frameRate);

  server();
}




void server() {
  Client thisClient = myServer.available();
  if (thisClient !=null) {
    String s = thisClient.readString();
    print(s);
    if (s != null) {
      String[] p = s.split("\n");

      for (int i=0; i <p.length; i++)
      {
        if (p[i].contains(";")) {
          String[] tp = p[i].split(";");

          if (tp[0].equals("p") && tp.length == 4)
          {
            try {
              float x= width/2+(Float.parseFloat(tp[1]))*100f;
              float y= height/2+(Float.parseFloat(tp[3]))*100f;
              float z= height/2+(Float.parseFloat(tp[2]))*100f;
              vectors.add(new PVector(x, y, z));
            }
            catch(Exception e) {
            }
          } else
            if (tp[0].equals("c") && tp.length == 5)
            {
              //fill(0);
              //stroke(0);
              //circle( cx, cy, 10);
              //line(cx, cy, cx-(float)Math.sin(cheading) *20f, cy-(float)Math.cos(cheading) *20f);

              cx= width/2+Float.parseFloat(tp[1])*100f;
              cy=height/2+ Float.parseFloat(tp[3])*100f;
              cheading=Float.parseFloat(tp[4]);

              //fill(255, 0, 0);
              //stroke(255);
              //circle( cx, cy, 10);
              //line(cx, cy, cx-(float)Math.sin(cheading) *20f, cy-(float)Math.cos(cheading) *20f);
            }
        }
      }

      shp = createShape();
      shp.beginShape(POINTS);
      shp.translate(-width/2, -width/2, -width/2);  
      for (PVector v : vectors) {
        shp.vertex(v.x, v.y, v.z);
      }
      shp.endShape();
    }
  }
}
