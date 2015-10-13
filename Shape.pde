// -----------------------------------------------------  Shape
void dragShape(){
  dragShape ^=true;
}
void brushShape() {
  shapeSystem.setPos(currPoint, f, mouseX, mouseY);
}
void shapeDisplay(){
  shapeDraw();
  if(pShape!=null)shape(pShape);
}
void shapeAddFrames(){
  for(Point p :shapeSystem.points){
    PVector v = p.pos.get(f-1);
    p.pos.add(v);
  }
}
void shapeAddPoint() {
  shapeSystem.addPoint(mouseX, mouseY);
}
void shapePointUp() {
  if (currPoint<=shapeSystem.points.size()-1) {
    if (currPoint==shapeSystem.points.size()-1) {
      currPoint=0;
    } else {
      currPoint++;
    }
  }
}
void shapePointDown() {
  if (currPoint>=0) {
    if (currPoint==0) {
      currPoint=shapeSystem.points.size()-1;
    } else {
      currPoint--;
    }
  }
}
void shapeDraw() {
  pShape = createShape();
  pShape.beginShape();
  pShape.noFill();
  pShape.stroke(0);
  pShape.strokeWeight(2);
  for (int i = 0; i<shapeSystem.points.size (); i++) {
    Point p = shapeSystem.points.get(i); 
    PVector v = p.pos.get(f);
    float x = v.x;
    float y = v.y;
    if (i==0) {
      pShape.curveVertex(x, y);
    }
    pShape.curveVertex(x, y);
    if (i==shapeSystem.points.size()-1) {
      Point p2 = shapeSystem.points.get(0); 
      PVector v2 = p2.pos.get(f);
      float x2 = v2.x;
      float y2 = v2.y;
      pShape.curveVertex(x2, y2);
    }
    if (i==currPoint) {
      fill(0, 255, 0);
    } else {
      fill(0);
    }
    ellipse(x, y, 10, 10);
    text(i, x, y);
  }
  pShape.endShape(CLOSE);
}
class ShapeSys {
  ArrayList<Point> points;
  ShapeSys() {
    points = new ArrayList<Point>();
    addPoint(0, 0);
  }
  void addPoint(int x, int y) {
    Point p = new Point(x, y);
    points.add(p);
  }
  void setPos(int pNum, int frame, int x, int y) {
    points.get(pNum).setPos(frame, x, y);
  }
}
class Point {
  ArrayList<PVector> pos;
  Point(int x, int y) {
    pos = new ArrayList<PVector>();
    for (int i = 0; i<f_total; i++) {
      PVector v = new PVector(x, y);
      pos.add(v);
    }
  }
  void setPos(int index, int x, int y) {
    if (pos.size()<f_total) {
      while (pos.size ()<f_total) {
        PVector v = new PVector(0, 0);
        pos.add(v);
      }
    }
    PVector v = new PVector(x, y);
    pos.set(index, v);
  }
}

// -----------------------------------------------------  Shape end
