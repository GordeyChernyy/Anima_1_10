PShape ps;
boolean drag = false;
ShapeSys shapeSystem;
int f = 0;
int f_total = 1;
int currPoint = 0;
void setup() {
  size(1200, 800, OPENGL);
  shapeSystem =  new ShapeSys();
}
void draw() {
  background(255);
  shapeDraw();
  if(ps!=null)shape(ps);
  println(shapeSystem.points.size());
}
void mouseDragged() {
  brushShape();
}
void mouseMoved() {
}

void mouseReleased() {
}

void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  if (e>0) {
    shapePointDown();
  }
  if (e<0) {
    shapePointUp();
  }
}
