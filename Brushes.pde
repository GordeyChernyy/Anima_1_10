void brushDraw(){
  ArrayList<Frame> l = layers.get(activeLayer);
  Frame frame = l.get(f);
  if(frame.img!=null) {
    ps.run(frame.img);
  }
}
void brushErase(PGraphics pg) { 
  noStroke();
  fill(255);
  rect(0, 0, w, h);
  color c = color(0, 0);
  pg.beginDraw();
  pg.loadPixels();
  for (int x=0; x<pg.width; x++) {
    for (int y=0; y<pg.height; y++ ) {
      float distance = dist(x, y, mouseX, mouseY);
      if (distance <= eraseSize) {
        int loc = x + y*pg.width;
        pg.pixels[loc] = c;
      }
    }
  }
  pg.updatePixels();
  pg.endDraw();
  image(pg, 0, 0);
  noFill();
  stroke(0);
  ellipse(mouseX ,mouseY,eraseSize*2,eraseSize*2);
}
void brushRound(PGraphics p){
  float pr = 8 * tablet.getPressure(); 
  p.beginDraw();
  p.strokeWeight(pr);
  p.stroke(0);
  p.line(mouseX,mouseY,pmouseX,pmouseY);
  p.endDraw();
}
void brushDream(PGraphics g) {
  //opacity = 255;
  PVector m = new PVector(mouseX, mouseY, 0);
  history.add(m);
  distthresh = hisBrushSize*tablet.getPressure() ;
  //distthresh = 300;
  g.beginDraw();
  for (int p=0; p<history.size (); p++) {   
    PVector v = (PVector) history.get(p);
    float joinchance = p/history.size() + m.dist(v)/distthresh;
    if (joinchance < random(0.4) ) {
        g.stroke(col, 90); 
        g.strokeWeight(0.5);
        g.line(m.x, m.y, v.x, v.y);
    }
  }
  g.endDraw();
}
void brushFill(){
  thread("cleanPgfill");
  pgfill = createGraphics(w,h,JAVA2D);
  pgfill.beginDraw();
  pgfill.beginShape();
  pgfill.noStroke();
  pgfill.fill(255);
  for (int i = 0; i < history.size(); ++i) {    
    PVector v = (PVector) history.get(i);
    pgfill.vertex(v.x, v.y);
  }
  pgfill.endShape(CLOSE);
  pgfill.endDraw();
}
void brushFillRelease(){
  thread("cleanBuffImage");
  buffImage = layers.get(activeLayer).get(f).img.get();
  PGraphics img = layers.get(activeLayer).get(f).img;
  img.beginDraw();
  img.clear();
  img.image(pgfill, 0, 0);
  img.image(buffImage, 0, 0);
  img.endDraw();
}
void cleanBuffImage(){
  g.removeCache(buffImage);
  System.gc();
}
void cleanPgfill(){
  g.removeCache(pgfill);
  System.gc();
}
void brushVector(){
  PVector mc = new PVector(mouseX, mouseY); 
  //float dist = dist(mouseX,mouseY, pmouseX, pmouseY);
  PVector vd = new PVector(10, 0); // distance 
  PVector v1 = new PVector(0, 0);
  PVector v2 = new PVector(0, 0);
  PVector dir1, dir2;
  float x = mouseX - pmouseX;
  float y = mouseY - pmouseY;
  
  float angle = atan2(y,x);
  
  vd.rotate(angle+radians(90));
  v1.add(mc);
  //v1.add(vd);
  
  vd.rotate(radians(180));
  v2.add(mc);
  v2.add(vd);
  
  
  dir1 = PVector.fromAngle(angle+radians(90));
  dir2 = PVector.fromAngle(angle+radians(270));
  
  ps.addParticle(v1,dir1);
}
class ParticleSystem {
  ArrayList<Particle> particles;
  PVector origin;  
  ParticleSystem(PVector location) {
    origin = location.get();
    particles = new ArrayList<Particle>();
  }
  void addParticle(PVector v1, PVector dir1) {
    particles.add(new Particle(v1, dir1));
  }
  void run(PGraphics pg) {
    pg.beginDraw();  
    pg.beginShape(); 
    Iterator<Particle> itr1 = particles.iterator();
    while(itr1.hasNext()){
      Particle p = itr1.next();
      p.run() ;
      PVector pos = p.pos;
      float op = p.op;
      pg.strokeWeight(wcLineWidth);
      pg.fill(col, op*wcFillOpacity);
      pg.stroke(col, op*wcStrokeOpacity);      
      pg.curveVertex(pos.x, pos.y);
      if(p.isDead()){
        itr1.remove();
      }
    }
    pg.endShape();
    pg.endDraw();
  }
}
class Particle {
  PVector pos;
  PVector dir;
  float lifespan;
  float op=20;
  Particle(PVector l, PVector v) {
    dir = v;
    pos = l;
    lifespan = random(wcLifeMin, wcLifeMax);   
  }
  void run() {
    PVector vr = PVector.random2D();
    vr.mult(wcRandMult);
    pos.add(vr);
    pos.add(dir);
    lifespan -= wcLifeSpeed;
    op+= wcOpacitySpeed;
  }
  boolean isDead() {
    if (lifespan < 0.0) {
      return true;
    } else {
      return false;
    }
  }
}
