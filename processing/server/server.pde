
import processing.net.*;

Server myServer;
float cx, cy, cheading;

void setup() {
  fullScreen();
  noSmooth();
  background(0);
  //translate(140, 0);

  myServer = new Server(this, 65432);
}




void draw() {
  // Draw white points

  //background(0);

  Client thisClient = myServer.available();
  if (thisClient !=null) {
    String s = thisClient.readString();

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
              stroke(255);
              point( x, y);
            }
            catch(Exception e) {
            }
          } else
            if (tp[0].equals("c") && tp.length == 5)
            {
              fill(0);
              stroke(0);
              circle( cx, cy, 10);
              line(cx, cy, cx-(float)Math.sin(cheading) *20f, cy-(float)Math.cos(cheading) *20f);

              cx= width/2+Float.parseFloat(tp[1])*100f;
              cy=height/2+ Float.parseFloat(tp[3])*100f;
              cheading=Float.parseFloat(tp[4]);

              fill(255, 0, 0);
              stroke(255);
              circle( cx, cy, 10);
              line(cx, cy, cx-(float)Math.sin(cheading) *20f, cy-(float)Math.cos(cheading) *20f);
            }
        }
      }
    }
  }
}
