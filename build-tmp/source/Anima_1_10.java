import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 
import controlP5.*; 
import codeanticode.tablet.*; 
import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Anima_1_10 extends PApplet {

int w = 1280;
int h = 720;

// -----------------------------------------------------  Layers
ArrayList<ArrayList<Frame>> layers = new ArrayList<ArrayList<Frame>>();
int activeLayer = 0;
int layerCount = 1;
// -----------------------------------------------------  Buffer
PImage buffImage;
int buffCount = 150;
int ImgCount = 0;
boolean audioThread = true;
// -----------------------------------------------------  Project
boolean pause = true;
String projState;
String projPath = "/Users/gordey/Projects/Singularity/";
String sc;
String scPath;
PrintWriter output;
boolean foldSelect = false;
// -----------------------------------------------------  Frame manegment
int f = 0;
int f_total = 1;
int in = 0;
int out = 0;
// -------------s----------------------------------------  FPS and play
int[] inoutStore = {0, 0};
boolean playSwitcher = false;
int playTwoOut;
boolean play = false;
int fpsSpeed = 5;
int fpsCount = 0;
// -----------------------------------------------------  UI
int uiSize = 100;
int uiFSize = 15;

ControlP5 cp5;
boolean cpHide = true;
// -----------------------------------------------------  Colors and Drawing

boolean enableFill = true;
int blendind = 0;
ShapeSys shapeSystem;
int currPoint = 0;
PShape pShape;
boolean dragShape = true;

PGraphics draft;
PGraphics pgfill;
boolean isFill = true;
int fillFrames = 0;
boolean showDraft = true;
int drafOpacity = 120;
int imgOpacity = 255;

int storeLastBrush;
int swrow = 0; // swatches
int col = color(0);

Tablet tablet;
int colorBg = color(220);
int brushSelect = 0;
ArrayList <PVector> wcHisUp = new ArrayList<PVector>();
ArrayList <PVector> wcHisDn = new ArrayList<PVector>();
int hisBrushSize = 300;
ParticleSystem ps; // watercolor
float wcLineWidth = 1.2f;
float wcFillOpacity = 0.067f;
float wcStrokeOpacity = 1.0f;
int wcLifeMin = 20;
int wcLifeMax = 50;
int wcRandMult = 2;
float wcLifeSpeed = 2.0f;
int wcOpacitySpeed = 45;
ArrayList history = new ArrayList();
float distthresh = 60;
int eraseSize = 20;
// -----------------------------------------------------  Onion skin
boolean showOnion = true;
boolean moveOnionUp = false;
int onionOpacity = 20;
// -----------------------------------------------------  Render
boolean render = false;
PGraphics renderImage;
int renderCounter = 0;
// -----------------------------------------------------  Sound
int framesInScenes = 0;
String audiopath;

Minim minim;
boolean mute = false;
AudioPlayer player;
int playersCount = 0;
ArrayList players = new ArrayList();

boolean audioExist = false;
// -----------------------------------------------------  Key
boolean CMD = false;
// -----------------------------------------------------  Translate
boolean moveImgX = false;
boolean moveImgY = false;
int imgPosX = 0;
int imgPosY = 0;
int moveStep = 10;
// -----------------------------------------------------  Translate

public void setup(){
  size(w,h+uiSize, OPENGL);
  noCursor();  
  cp5Setup();
  for (int i = 0; i < 3; ++i) {
    AudioPlayer pl;
    players.add(pl);
  }
//  shapeSystem = new ShapeSys();
  draft = createGraphics(w,h,JAVA2D);
  minim = new Minim(this);
  tablet = new Tablet(this);   
  ps = new ParticleSystem(new PVector(0,0));
  frameRate(60);
}
public void draw(){
  background(colorBg);
  if(!pause){
    brushDraw();
    if(play)play();
    uiDraw(); 
//    if(dragShape)brushShape();
//    shapeDisplay();
    if(render){
      renderFrames();
    }else{
      display();
      if(frameCount%2000==0) thread("saveProj");
    }
    if(cpHide) uiInfo();
  }
}
public void mousePressed() {
}
public void mouseReleased( ){
  if(!pause ){
    if (enableFill && brushSelect == 0 && !cp5.isMouseOver() && history.size()>0) {
      brushFill();
      if (play) {
        fillFrames = out - in +1;
      }else{
        brushFillRelease();
      }
    }
    history.clear();
  }
}
public void mouseDragged(){
  if(!pause){
    if(!cp5.isMouseOver()){
      ArrayList<Frame> l = layers.get(activeLayer);
      Frame frame = l.get(f);
      if(frame.img==null){frame.makeImg();}
      frame.setChange(true);
      switch(brushSelect){
        case 0:{
          brushDream(frame.img);
          break;
        }
        case 1:{
          brushVector();
          break;
        }
        case 2:{
          brushErase(frame.img);
          break;
        }
        case 3:{
          brushRound(frame.img);
          break;
        }
        case 4:{
          brushRound(draft);
          break;
        }
      }
    }
  }
}
public void mouseWheel(MouseEvent event) {
  if(!pause){
    float e = event.getCount();
    if(moveImgX){
      if(e>0){imgPosX+=moveStep;}
      if(e<0){imgPosX-=moveStep;}
      translateImg();
    }else if(moveImgY){
      if(e>0){imgPosY+=moveStep;}
      if(e<0){imgPosY-=moveStep;}
      translateImg();
    }else
    {
      if(e>0){nextFrame();}
      if(e<0){prevFrame();}
    }
  }
}
// ----------------------------------------------------- Audio
public void muteAudio(){
  if(player.isMuted()){
    player.unmute();
    mute = false;
  }else{
    player.mute();
    mute = true;
  }
}
public void playAudio(){
  audioThread = false;
  if(audioExist && !mute && (f+framesInScenes)*1000/12< player.length()){
      AudioPlayer player2 = (AudioPlayer) players.get(playersCount);
      player2.setLoopPoints((f+framesInScenes)*1000/12, ((f+framesInScenes)+1)*1000/12);
      player2.loop(0);
   }
   if (playersCount==players.size()-1) {
     playersCount = 0;
   }else{
      playersCount++;
   }
  audioThread = true;
}
public void playAudioFromIn(){
  audioThread = false;
  if(audioExist && !mute && (f+framesInScenes)*1000/12< player.length()){
    player.setLoopPoints((in+framesInScenes)*1000/12, (out+framesInScenes)*1000/12);
    player.loop();
    player.cue((f+framesInScenes)*1000/12);
  }
  audioThread = true;
}
public void playAudioSync(){  
audioThread = false;
 if(audioExist && !mute && (f+framesInScenes)*1000/12< player.length()){
      player.cue((f+framesInScenes)*1000/12);
      player.play();
   }
  audioThread = true;
}
public void LoadAudio(){
  selectFolder("Select Audio:", "audioSelected");
}
public void audioSelected(File selection){
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
    audiopath = selection.getAbsolutePath();
    loadAudio();
  }
}
public void loadAudio(){
  File audiofile = new File(audiopath);
  if(audiofile.exists()){
    player = minim.loadFile(audiopath);
    audioExist = true;
    println("Audio loaded!");
  }
}
// -----------------------------------------------------  Base controls
public void showOnion(){ // button
  showOni();
}
public void showOni(){
  showOnion ^= true; 
}
public void test(){
  Frame frame = new Frame(1);
  boolean b = layers.get(0).contains(frame);
  println(b);
}
public void addFrame(){
  f++; 
  for(ArrayList<Frame> layer: layers){
    Frame frame = new Frame(f);
    layer.add(f,frame);
  }
  f_total++; 
  out++; inoutStore[1] = out;
  thread("buffNewFrame");
  resetInOut();
//  shapeAddFrames();
}
public void deleteF(){
  for(ArrayList<Frame> layer: layers){
    if(layer.get(f).imgExist){
      ImgCount--;
    }
    layer.remove(f);
  }
  f--;
  f_total--; 
  out--;
  inoutStore[1] = out;
}
public void nextFrame(){
  if(f<out)f++;
  thread("buffNextFrame"); 
  if(!mute)thread("playAudio");
}
public void prevFrame(){
  if(f>in)f--;
  thread("buffPrevFrame");
  if(!mute)thread("playAudio");
}
public void deleteFrame(){
  Frame frame = layers.get(activeLayer).get(f);
  if(frame.loop){
    Frame frameN = new Frame(f);
    layers.get(activeLayer).set(f,frameN);  // set blank 
  }else{
    frame.removeImg();
    frame.removeFile();
  }
}
public void brushDraw(){
  ArrayList<Frame> l = layers.get(activeLayer);
  Frame frame = l.get(f);
  if(frame.img!=null) {
    ps.run(frame.img);
  }
}
public void brushErase(PGraphics pg) { 
  noStroke();
  fill(255);
  rect(0, 0, w, h);
  int c = color(0, 0);
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
public void brushRound(PGraphics p){
  float pr = 8 * tablet.getPressure(); 
  p.beginDraw();
  p.strokeWeight(pr);
  p.stroke(0);
  p.line(mouseX,mouseY,pmouseX,pmouseY);
  p.endDraw();
}
public void brushDream(PGraphics g) {
  //opacity = 255;
  PVector m = new PVector(mouseX, mouseY, 0);
  history.add(m);
  distthresh = hisBrushSize*tablet.getPressure() ;
  //distthresh = 300;
  g.beginDraw();
  for (int p=0; p<history.size (); p++) {   
    PVector v = (PVector) history.get(p);
    float joinchance = p/history.size() + m.dist(v)/distthresh;
    if (joinchance < random(0.4f) ) {
        g.stroke(0, 90); 
        g.strokeWeight(0.5f);
        g.line(m.x, m.y, v.x, v.y);
    }
  }
  g.endDraw();
}
public void brushFill(){
  thread("cleanPgfill");
  pgfill = createGraphics(w,h,JAVA2D);
  pgfill.beginDraw();
  pgfill.beginShape();
  pgfill.noStroke();
  pgfill.fill(col);
  for (int i = 0; i < history.size(); ++i) {    
    PVector v = (PVector) history.get(i);
    pgfill.vertex(v.x, v.y);
  }
  pgfill.endShape(CLOSE);
  pgfill.endDraw();
}
public void brushFillRelease(){
  thread("cleanBuffImage");
  buffImage = layers.get(activeLayer).get(f).img.get();
  PGraphics img = layers.get(activeLayer).get(f).img;
  img.beginDraw();
  img.clear();
  img.image(pgfill, 0, 0);
  img.image(buffImage, 0, 0);
  img.endDraw();
}
public void cleanBuffImage(){
  g.removeCache(buffImage);
  System.gc();
}
public void cleanPgfill(){
  g.removeCache(pgfill);
  System.gc();
}
public void brushVector(){
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
  public void addParticle(PVector v1, PVector dir1) {
    particles.add(new Particle(v1, dir1));
  }
  public void run(PGraphics pg) {
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
  public void run() {
    PVector vr = PVector.random2D();
    vr.mult(wcRandMult);
    pos.add(vr);
    pos.add(dir);
    lifespan -= wcLifeSpeed;
    op+= wcOpacitySpeed;
  }
  public boolean isDead() {
    if (lifespan < 0.0f) {
      return true;
    } else {
      return false;
    }
  }
}
// -----------------------------------------------------  Buffering
public void translateImg(){
  Frame frame = layers.get(activeLayer).get(f);
  if(frame.img==null){
    frame.makeImg();
  }
  PGraphics pg = frame.img;
  pg.beginDraw();
  pg.clear();
  pg.image(buffImage,imgPosX,imgPosY);
  pg.endDraw();  
}
public void moveImgX(){
  if(buffImage!=null) moveImgX = true;
}
public void moveImgY(){
  if(buffImage!=null) moveImgY = true;
}
public void moveImgXUp(){
  moveImgX = false;
}
public void moveImgYUp(){
  moveImgY = false;
}
public void copyImage(){
  imgPosX = 0 ; imgPosY = 0;
  buffImage = layers.get(activeLayer).get(f).img;
} 
public void pasteImage(){
  Frame frame = layers.get(activeLayer).get(f);
  if(frame.img==null){
    frame.makeImg();
  }
  PGraphics pg = frame.img;
  pg.beginDraw();
  pg.clear();
  pg.image(buffImage,0,0);
  pg.endDraw();
}
public void addImage(){
  Frame frame = layers.get(activeLayer).get(f);
  if(frame.img==null){
    frame.makeImg();
  }
  PGraphics pg = frame.img;
  pg.beginDraw();
  pg.image(buffImage,0,0);
  pg.endDraw();
}
public void buffSelected(){
  if(ImgCount<buffCount){
    
    for(int i = in; i <= out; i++){
      for(ArrayList<Frame> layer: layers){
        Frame frame = layer.get(i);
        
        if(!frame.imgExist && frame.fileExist ){
          frame.loadImg();
          
        }
      }
    }
  }
}
public void buffNewFrame(){
  if(ImgCount>buffCount){buffClearFromStart();}    
}
public void buffPrevFrame(){
  for(ArrayList<Frame> layer: layers){
    Frame frame = layer.get(f);
    if(!frame.imgExist && frame.fileExist ){frame.loadImg();}
  }
  while(ImgCount>buffCount){buffClearFromEnd();}
}
public void buffNextFrame(){
  for(ArrayList<Frame> layer: layers){
    Frame frame = layer.get(f);
    if(!frame.imgExist && frame.fileExist ){frame.loadImg();}
  }
  while(ImgCount>buffCount){buffClearFromStart();}   
}
public void buffClearFromStart(){
  for(int c = 0;c <= f_total; c++){
    boolean stop = false;
    for(int r = 0; r<layers.size(); r++){
      Frame frame = layers.get(r).get(c);
      if(frame.imgExist){
        if(frame.change){frame.saveImg();}
        frame.removeImg();
        stop = true;
        break;
      }
    }
    if(stop){break;}
  }
}
public void buffClearFromEnd(){
  for(int c = f_total-1; c >=0; c--){
    boolean stop = false;
    for(int r = 0; r<layers.size(); r++){
      Frame frame = layers.get(r).get(c);
      if(frame.imgExist){
        if(frame.change){frame.saveImg();}
        frame.removeImg();
        stop = true;
        break;
      }
    }
    if(stop){break;}
  }
}
// -----------------------------------------------------  Display frames, onion, draft
public void display(){
  int fBefore = -1;
  int fAfter = 1;
  ArrayList<PImage> images = new ArrayList<PImage>();
  ArrayList<PImage> onions = new ArrayList<PImage>();
  for(ArrayList<Frame> layer: layers){
    int fB = f+fBefore;
    int fA = f+fAfter;    
    if(fB >= 0){ 
      Frame frameBefore = layer.get(fB);
      if(frameBefore.img!=null)onions.add(frameBefore.img);
    }
    if(fA <= out){
      Frame frameAfter = layer.get(fA);
      if(frameAfter.img!=null)onions.add(frameAfter.img);
    }
    if(fA == f_total){
      Frame frameAfter = layer.get(0);
      if(frameAfter.img!=null)onions.add(frameAfter.img);
    }
    Frame frame = layer.get(f);
    if(frame.img!=null)images.add(frame.img); 
  }
  if(showOnion){
    tint(255, onionOpacity);
    for(PImage img: onions) {
      if(img!=null)image(img, 0, 0);
    }
    tint(255, 255);
  }

  if(showDraft){
    tint(255, drafOpacity);
    image(draft, 0, 0);
    tint(255, 255);
  }
  tint(255, imgOpacity);
  for(PImage img: images){
    if(img!=null)image(img, 0, 0);
  }
  tint(255, 255);
}
// -----------------------------------------------------  Swatches, drawing, brushes
public void brushEraseUp(){
  brushSelect = storeLastBrush;
}
public void brushErase(){
  storeLastBrush = brushSelect;
  brushSelect = 2;
}
public void brushDraft(){
  storeLastBrush = brushSelect;
  brushSelect = 4;
}
public void brushDraftUp(){
  brushSelect = storeLastBrush;
}
public void draftErase(){
  draft.beginDraw();
  draft.clear();
  draft.endDraw();
}
public void brushPlain(){
  brushSelect = 0;
}
public void brushWC(){
  brushSelect = 1;
}
public void colorChange(int num){
  col = swatches[swrow][num];
}
public void swatchChange(boolean up){
  if(up){
    if(swrow<swatches.length-1)swrow++;
  }else{
    if(swrow>0)swrow--; 
  }
}
class Frame{
  int posX = 0;
  int posY = 0;
  int frameId;
  int randomId;
  PGraphics img;
  boolean fileExist = false;
  boolean imgExist = false;
  boolean change = false;
  boolean loop = false;
  int col;
  Frame(int f_ID){
    frameId = f_ID;
  }
  
  public void makeImg(){
    img = createGraphics(w,h, JAVA2D);
    ImgCount++;
    imgExist = true;
  }
  public void saveImg(){
    if(!fileExist) randomId = PApplet.parseInt(random(1000));
    String savePath = scPath+
                      frameId+"_"+
                      randomId+
                      ".png";  
    if(img!=null)img.save(savePath);
    fileExist = true;
    change = false;
  }
  public void loadImg(){
    img = createGraphics(w,h, JAVA2D);
    String loadPath = scPath+
                      frameId+"_"+
                      randomId+
                      ".png";
    PImage tempImg = loadImage(loadPath);
    img.beginDraw();
    img.image(tempImg,0,0);
    ImgCount++;
    imgExist = true;
  }
  public void removeImg(){
    g.removeCache(img);
    System.gc();
    img = null;   
    imgExist = false;
    ImgCount--;
  }
  public void removeFile(){
    fileExist = false;
  }
  public void setChange(boolean b){
    change = b;
  }
  public PGraphics getImg(){
    return img;
  }
  public boolean fileExist(){
    return fileExist;
  }
  public boolean imgExist(){
    return imgExist;
  }
}
// -----------------------------------------------------  Layers
public void newLayer(){
  ArrayList<Frame> layer = new ArrayList<Frame>();
  for(int i=0; i<f_total; i++){
    layer.add(new Frame(i));
  }
  layers.add(layer);
}
public void layerUp(){
  if(activeLayer<layers.size()-1 && activeLayer>=0){activeLayer++;}
}
public void layerDown(){
  if(activeLayer<layers.size() && activeLayer>0){activeLayer--;}
}
/**

-------- Audio
muteAudio()
playAudio()
playAudioFromIn()
LoadAudio()
audioSelected(File selection)
loadAudio()

-------- Base
showOnion() -- button
showOni()
addFrame()


*/
// -----------------------------------------------------  Controls
public void cp5Setup(){
  cp5 = new ControlP5(this);
  PFont font = createFont("Tahoma",11); 
  int buttonColor = color(255, 255, 255);
  cp5.setControlFont(font, 11);
  cp5.setColorLabel(0);
  Group g1 = cp5.addGroup("Controls")
                .setPosition(50,50)
                .setBackgroundHeight(700)
                .setWidth(200)
                .setBackgroundColor(color(200));
                
 cp5.addTextfield("input")
     .setPosition(250,0)
     .setSize(100,20)
     .setGroup(g1)
     .linebreak();
     ;
  cp5.addButton("loadProj")
     .setColorBackground(buttonColor)
     .setSize(100,20)
     .setGroup(g1)
     .linebreak();
     ;
  cp5.addButton("newProj")
     .setColorBackground(buttonColor)
     .setSize(100,20)
     .setGroup(g1)
     .linebreak();
     ;
  cp5.addButton("saveProj")
      .setColorBackground(buttonColor)
     .setSize(100,20)
     .setGroup(g1)
     .linebreak();
     ;
  cp5.addButton("renderProj")
      .setColorBackground(buttonColor)
     .setSize(100,20)
     .setGroup(g1)
     .linebreak();
     ;
   cp5.addButton("showOnion")
     .setColorBackground(buttonColor)
     .setSize(100,20)
     .setGroup(g1)
     .linebreak();
     ;
   cp5.addButton("LoadAudio")
      .setColorBackground(buttonColor)
     .setSize(100,20)
     .setGroup(g1)
     .linebreak();
     ;
  cp5.addToggle("showDraft")
     .setColorBackground(buttonColor)
     .setValueLabel("showDraft")
     .setValue(true)
     .setMode(ControlP5.SWITCH)
     .setSize(50,10)
     .setGroup(g1)
     .linebreak();
     ;
  cp5.addToggle("enableFill")
     .setColorBackground(buttonColor)
     .setValueLabel("enableFill")
     .setValue(true)
     .setMode(ControlP5.SWITCH)
     .setSize(50,10)
     .setGroup(g1)
     .linebreak();
     ;
  cp5.addButton("renderFrame")
      .setColorBackground(buttonColor)
     .setSize(100,20)
     .setGroup(g1)
     .linebreak();
     ;
  addSlider("brushSelect", 0, 4, g1);
  addSlider("onionOpacity", 0, 255, g1);
  addSlider("drafOpacity", 0, 255, g1);
  addSlider("imgOpacity", 0, 255, g1);
  addSlider("buffCount", 2, 300, g1);
  addSlider("fpsSpeed", 1, 14, g1);
  addSlider("hisBrushSize", 100, 800, g1);
  addSlider("wcLineWidth", 0.1f, 5, g1);
  addSlider("wcFillOpacity", 0, 1, g1);
  addSlider("wcStrokeOpacity", 0, 1, g1);
  addSlider("wcLifeMin", 0, 300, g1);
  addSlider("wcLifeMax", 0, 300, g1);
  addSlider("wcRandMult", 0.02f, 5, g1);
  addSlider("wcLifeSpeed", 0, 10, g1);
  addSlider("wcOpacitySpeed", 0, 100, g1);
  addSlider("eraseSize", 0, 300, g1);
  
  cp5.end();
}

public void hideUI(){
    if(cpHide){
      cp5.hide();
    }else{
      cp5.show();
    }
    cpHide ^=true; 
}
public void addSlider(String name, float in, float out, Group g){
  Slider s =
  cp5.addSlider(name)
  .setRange(in, out)
  .setHeight(17)
  .setGroup(g)
  .linebreak();  
  
  controlP5.Label label = s.captionLabel();
  label.toUpperCase(false);
}
// -----------------------------------------------------  Info and Frames
public void uiDraw(){
  pushMatrix(); 
  translate(w/2, h); // to center 
    
    uiMove();
    uiPointer();
  popMatrix();
  uiSwatch();
}
public void uiSwatch(){
  int rs = 20;
  int posX = w-rs*9;
  for(int i = 0; i < 8; i++ ){
    noStroke();
    fill(swatches[swrow][i]);
    rect(i*rs+posX,10,rs,rs);
  }
}
public void uiShowFrame(){
  int textsize = 10;
  int leading = 16;
  fill(255, 0, 0);
  textSize(textsize);
  textLeading(leading);
  String info = "frame: "+f+"\n"+"time: "+nf(f/12.0f, 0, 2)+ " seconds";
  text(info, 0, -26);
}
public void uiInfo(){
  int x = 50;
  int y = 600;
  int textsize = 10;
  int leading = 16;
  int border = 10;
  textSize(textsize);
  textLeading(leading);
 
  String info = "f_total  "+f_total+"                                   "+"\n"+
                "f  "+f+"\n"+
                "buffCount  " + buffCount +"\n"+
                "ImgCount  " + ImgCount +"\n"+
                "System fps  " + frameRate +"\n"+
                "Play fps  " + PApplet.parseInt(frameRate/fpsSpeed)
                ;
  String[] lines = info.split("\r\n|\r|\n");
  int lineCount = lines.length;
  float tw = textWidth(info);
  float lineH = textsize +(leading-textsize) ;
  fill(255, 200);
  noStroke();
  int _x = x-border;
  int _y = y-textsize-border;
  float _w = tw+border*2;
  float _h = lineH * lineCount+border*2;
  rect(_x,_y,_w, _h);
  fill(0);
  text(info, x,y);
}
public void uiPointer(){
  pushMatrix(); 
  translate(uiFSize, 0);
    noFill(); 
    stroke(255, 0, 0); 
    strokeWeight(2);
    //line(uiFSize/2,0,uiFSize/2,uiSize); // vertical line
    rect(0,0, uiFSize, uiFSize);
  popMatrix();
}

public void uiMove(){
  int lWidth = layers.get(0).size();
  int x = uiFSize * ( lWidth-f ) - lWidth * uiFSize + uiFSize;
  int y = activeLayer*uiFSize;
  pushMatrix();
  translate(x, y);
    uiFrames();
  popMatrix();
  pushMatrix();
  translate(0, -uiFSize*layers.size()+y);
  uiShowFrame();
  popMatrix();
}
public void uiFrames(){
  int y = 0;
  for(ArrayList<Frame> layer: layers){
    int x = 0;
    pushMatrix(); 
    translate(0,-uiFSize*y);
      for(Frame frame: layer){
        strokeWeight(0.5f);
        stroke(0);
        if(frame.imgExist){
          if(frame.loop){
            fill(frame.col);
          }else{
            fill(210);
          }
        }else if(frame.fileExist){
          fill(200,255,255);
        }else{
          fill(255);
        }
        
        rect(uiFSize*x,0,uiFSize,uiFSize);
        if(x == in){
          stroke(255,0,0);
          line(uiFSize*x, 0, uiFSize*x, 50);
        }
        if(x == out){
          stroke(255,0,0);
          line(uiFSize*x+uiFSize, 0, uiFSize*x+uiFSize, 50);
        }
        //uiFrameInfo(frame, x);
        x++;
      }
    popMatrix();
    y++;
  }
}
public void uiFrameInfo(Frame frame, int x){
  uiFSize = 40;
  String frameId = str(frame.frameId);
  textSize(12);
  fill(0);
  text(frameId,uiFSize*x,10 );
}
public void keyReleased(){
  if (keyCode == 157 ) CMD = false;
  if(!pause && !cp5.get(Textfield.class,"input").isActive()){
      if (key == 'g') brushEraseUp(); 
      if (key == 'c') brushDraftUp();
      if (key == 'k') moveImgXUp();
      if (key == 'l') moveImgYUp();
  }
}
public void keyPressed() {
  
  if( !pause && !cp5.get(Textfield.class,"input").isActive()){
    if (CMD) {
      if (key == '1') {colorChange(4);}
      if (key == '2') {colorChange(5);}
      if (key == '3') {colorChange(6);}
      if (key == '4') {colorChange(7);}
    }else {
      if (key == '1') {colorChange(0);}
      if (key == '2') {colorChange(1);}
      if (key == '3') {colorChange(2);}
      if (key == '4') {colorChange(3);}
    }
    if (key == '5') col = color(255);
    if (key == '6') col = color(0);
    if (key == 'k') moveImgX();
    if (key == 'l') moveImgY();
    if (key == ' ') playSwitch();
    if (key == 'q') playTwo();
    if (key == 'g') brushErase();
    if (key == 'x') draftErase();
    if (key == 'r') layerUp();
    if (key == BACKSPACE) deleteFrame();
    if (keyCode == 157 ) CMD = true; 
    if (key == TAB) hideUI();
    if (key == 'z') muteAudio();
    if (!CMD && key == 'v') layerDown();
    if (!play){
      if (CMD){
        if (key == 'c') copyImage();
        if (key == 'v') pasteImage();
        if (key == 'b') addImage();
      }else{
        if (key == 'c') brushDraft();
        
      }      

    //    if (key == '8') shapePointDown();
    //    if (key == '9') shapePointUp();
    //    if (key == '0') dragShape();
    //    if (key == 'k') shapeAddPoint();
      if (key == 's') addFrame();
      if (key == 'd') prevFrame();
      if (key == 'f') nextFrame();
      if (key == 'a') loopTwo();
      if (key == 'n') newLayer();
      if (key == '.') saveProj();
      if (key == 'i') setInPoint();
      if (key == 'o') setOutPoint();
      if (key == 'p') resetInOut(); 
      if (key == '0') test(); 
      if (key == 'h') brushPlain(); 
      if (key == 'j') brushWC(); 
      if (key == '-') deleteF();
      if (keyCode == RIGHT) fpsSpeedUp(); // right arrow
      if (keyCode == LEFT) fpsSpeedDown(); // left arrow
      if (keyCode == UP) swatchChange(true);
      if (keyCode == DOWN) swatchChange(false);
      if (keyCode == '\n') render();
      
    }
  }else{
   
  }
}
// -----------------------------------------------------  Loop
public void setInPoint(){
  in = f; inoutStore[0] = in;
}
public void setOutPoint(){
  out = f; inoutStore[1] = out;
}
public void resetInOut(){
  in = 0; out = f_total-1;
  inoutStore[0] = in;
  inoutStore[1] = out;
}
public void loopTwo(){
  loopFrames();
}
public void loopFrames(){
  int fCount=0;
  ArrayList<Frame> acLa = layers.get(activeLayer);
  for(int i = f; i<f_total; i++){ // find where frames ending
    Frame frame = acLa.get(i);
    float r = random(150,255);
    float g = random(120,255);
    float b = random(180,255);
    int col = color(r, g, b);
    if(!frame.loop){
      frame.loop = true;
      frame.col = col;
    }
    if(!frame.imgExist)break; 
    fCount++; // count frames to copy
    
  }
  println("fCount = "+fCount);
  int disToBlank = f + fCount; 
  int fToNeed = disToBlank+fCount; 
  int fToAdd = fToNeed - f_total;
  if(fToAdd>0){ // add frames if need
    int l = 0;
    for(ArrayList<Frame> layer: layers){
      addFramesToLayer(layer,fToAdd,l);
      l++;
    }
    f_total += fToAdd;
    out += fToAdd;
    inoutStore[1] = out;
  }
  for(int i=0; i<fCount; i++){ // copy frames
    int idReplace = f+fCount+i;
    int idCopy = f+i;
    Frame fToCopy = layers.get(activeLayer).get(idCopy);
    layers.get(activeLayer).set(idReplace, fToCopy);
  }
}
public void addFramesToLayer(ArrayList<Frame> layer, int fCount, int l){
  for(int i=0; i<fCount; i++){
    Frame frame = new Frame(f_total);
    layer.add(frame);
  }
}
// -----------------------------------------------------  Play
public void playTwo(){
  playSwitcher ^= true;
  
  if(playSwitcher){
    if(f!=f_total-1){ in = f; out = f+1;}
    player.mute();
  }else{
    in = inoutStore[0];
    out = inoutStore[1];
    player.unmute();
  }
  playSwitch();
}
public void playSwitch(){
  play ^= true;
  fpsCount=0;
  if(play){
    thread("playAudioFromIn");
  }else{player.pause();} 
}
public void play(){
  fpsCount++;
  if(fpsCount%fpsSpeed==0){  
    if(f<=out+1){
      if (fillFrames>0) { // start filling shape
        println("fillFrames = "+fillFrames);
        brushFillRelease();
        fillFrames--;
      }
      f++;
    }
    thread("playAudioSync");
  }
  if(f==out+1){
    f=in;
  }
  
}
public void fpsSpeedUp(){
  if(fpsSpeed>1){fpsSpeed--;}
}
public void fpsSpeedDown(){
  fpsSpeed++;
}
class Data {
  String[] rows;
  String[][] data;   
  Data(String filename) {
    rows = loadStrings(filename); 
    data =  new String[rows.length][];
    for (int i = 0; i < rows.length; i++) {
      String[] pieces = split(rows[i], ',');
      data[i] = pieces;
    }
  }
}
public void folderSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
    projPath = selection.getAbsolutePath();
    String m = cp5.get(Textfield.class,"input").getText();
    if(projState.equals("load")) loadProject();
    if(projState.equals("new")){
      String empty = "";
      if(m.equals(empty)){
        dialBox("Type scene name please");
      }else{
        sc = cp5.get(Textfield.class,"input").getText();
        newProject();
      }
    }
    println("User selected " + selection.getAbsolutePath());
  }
}
// -----------------------------------------------------  Load
public void loadProj(){
  projState = "load";
  selectFolder("Select a folder to process:", "folderSelected");
}
public void countFramesInScenes(){  
  String[] m = match(scPath, "sc.*"); // sc23
  String[] m2 = match(m[0], "[0-9]+[0-9]|[0-9]"); // 23
  int scCounts = PApplet.parseInt(m2[0]); 
  framesInScenes = 0;
  if (scCounts>1) {  
    for (int i = 1; i < scCounts; ++i) {
      String[] cutNum = match(scPath, ".*/sc"); // path/sc
      String _scPath = cutNum[0]+str(i);
      Data prData = new Data(_scPath+"/ProjData.csv"); // path/sc/
      int _f_total = PApplet.parseInt(prData.data[1][1]);
      if (i==1) {
        audiopath = prData.data[1][4] ;
      }
      framesInScenes += _f_total;
    }
  }
}
public void loadProject(){
  println("framesInScenes = " + framesInScenes);
  layers.clear(); 
  activeLayer = 0;
  ImgCount = 0;
  scPath = projPath+"/";
  countFramesInScenes();
  Data ld = new Data(scPath+"LayersData.csv");
  Data pd = new Data(scPath+"ProjData.csv");
  // ------------- Setup project var
  f =          PApplet.parseInt(pd.data[1][0]);
  f_total =    PApplet.parseInt(pd.data[1][1]);
  in =         PApplet.parseInt(pd.data[1][2]);
  out =        PApplet.parseInt(pd.data[1][3]);
  audiopath =      pd.data[1][4];
  layerCount = PApplet.parseInt(pd.data[1][5]) ;
  inoutStore[0] = in;
  inoutStore[1] = out;
  // ------------- Setup layers and frames in it
  int c = 1; // first row is a title, start from 1
  for(int i = 0; i < layerCount; i++ ){
    ArrayList<Frame> layer = new ArrayList<Frame>();
    ArrayList<Frame> loopedFrames = new ArrayList<Frame>(); // to search loop marked
    for(int k = 0; k < f_total; k++){
      if(PApplet.parseBoolean(ld.data[c][3])){ // find loop mark 
        putMatchFrameToLayer(loopedFrames, ld, layer, c);
      }else{ // or create new one
        Frame frame = frameWithVar(ld, c);
        layer.add(frame); //<>//
      }                    
      c++;
    }
    layers.add(layer);
  }
  loadAudio();
    for (int i = 0; i < 3; ++i) {
    AudioPlayer pl = minim.loadFile(audiopath);
    players.add(pl);
  }
  buffSelected(); // Selected mean from in point to end point
  pause = false;
}
// Where Search, condition mark data, in which layer put similar frame, counter
public void putMatchFrameToLayer(ArrayList<Frame> loopedFrames, Data ld, ArrayList<Frame> layer, int c){ 
  boolean finded = false;
  for(Frame frame: loopedFrames){ // search in looped frames
    int frameId =  PApplet.parseInt(ld.data[c][0]); // condition for match
    int randomId = PApplet.parseInt(ld.data[c][1]); // condition for match
    if(frame.frameId == frameId && 
       frame.randomId == randomId){
      layer.add(frame);
      println("finded!");
      finded = true;
      break;
    }
  }
  if(!finded){ // if not find create new
    Frame frame = frameWithVar(ld, c);
    loopedFrames.add(frame);
    layer.add(frame);
  }
}
public Frame frameWithVar(Data d, int r){
  int frameId =         PApplet.parseInt(d.data[r][0]);
  Frame frame = new Frame(frameId);
  frame.randomId =      PApplet.parseInt(d.data[r][1]);
  frame.fileExist = PApplet.parseBoolean(d.data[r][2]);
  frame.loop =      PApplet.parseBoolean(d.data[r][3]);
  frame.col = color(  PApplet.parseFloat(d.data[r][4]), 
                      PApplet.parseFloat(d.data[r][5]), 
                      PApplet.parseFloat(d.data[r][6]) );
  return frame;      
}
// -----------------------------------------------------  New
public void newProj(){ // public because it's a button
  pause = true;
  layers.clear(); 
  inoutStore[0] = 0;
  inoutStore[1] = 0;
  activeLayer = 0;
  layerCount = 1;
  f =          0;
  f_total =    1;
  in =         0;
  out =        0;
  ImgCount = 0;
  projState = "new";
  selectFolder("Select a folder to process:", "folderSelected"); 
}
public void newProject(){
  scPath = projPath+"/"+sc+"/";
  countFramesInScenes(); 
  ArrayList<Frame> layer0 = new ArrayList<Frame>();
  layer0.add(new Frame(0));
  layer0.get(0).makeImg();
  layers.add(layer0);
  pause = false;
  foldSelect = false; 
}
// -----------------------------------------------------  Save
public void saveProj(){ // public because it's a button
  output = createWriter(scPath+"LayersData.csv");
  String title = 
        "frameId"+","+
        "randomId"+","+ // Pimage not needed that's why it mised
        "fileExist"+","+
        "loop"+","+
        "colR"+","+
        "colG"+","+
        "colB"+","+
        "layerId";
  output.println(title);
  int layerId = 0;
  String info;
  for(ArrayList<Frame> layer: layers){
    for(Frame frame: layer){
      if(frame.fileExist && frame.change) frame.saveImg();
      if(frame.imgExist && !frame.fileExist) {
        frame.saveImg();
        info = "saving image";
      }
      String m =
      frame.frameId+","+
      frame.randomId+","+ // Pimage not needed that's why it mised
      frame.fileExist+","+// imgExist mised // change missed
      frame.loop+","+
      red(frame.col)+","+
      green(frame.col)+","+
      blue(frame.col)+","+
      layerId;
      output.println(m);
      
    }
    layerId++;
  }
  output.flush();
  output.close();  
    
  output = createWriter(scPath+"ProjData.csv");
  output.println("f"+","+
                 "f_total"+","+
                 "in"+","+
                 "out"+","+
                 "audiopath"+","+
                 "layerCount");
  output.println(f+","+
                 f_total+","+
                 in+","+
                 out+","+
                 audiopath+","+
                 layers.size());
  output.flush();
  output.close();  
  println("Saved!"); 
}
public void renderProj(){
  render();
}
// -----------------------------------------------------  Render
public void render(){
  render = true;
  f = 0;
  renderCounter = 0;
  showOnion = false;
}
public void renderFrames(){
  renderImage = createGraphics(w, h,JAVA2D);
  renderImage.beginDraw();
  renderImage.clear();
  renderImage.endDraw();
  
  for(ArrayList<Frame> layer: layers){
    Frame frame = layer.get(f);
    if(frame.fileExist){
      String loadPath = scPath+
                      frame.frameId+"_" +
                      frame.randomId +
                      ".png";
      PImage tempImg = loadImage(loadPath);
      renderImage.beginDraw();
      renderImage.image(tempImg, 0, 0);
      renderImage.endDraw();
    } 
  }
  renderImage.save(scPath+"render/img"+f+".png");
  nextFrame();
  if(renderCounter == f_total) {
    render = false;
  }
  renderCounter++;
}
public void renderFrame(){
  renderImage = createGraphics(w, h,JAVA2D);
  renderImage.beginDraw();
  renderImage.clear();
  renderImage.endDraw();
  
  for(ArrayList<Frame> layer: layers){
    Frame frame = layer.get(f);
    if(frame.fileExist){
      String loadPath = scPath+
                      frame.frameId+"_" +
                      frame.randomId +
                      ".png";
      PImage tempImg = loadImage(loadPath);
      renderImage.beginDraw();
      renderImage.image(tempImg, 0, 0);
      renderImage.endDraw();
    } 
  }
  String path[] = split(scPath,"/");
  String scName = path[path.length-2];
  renderImage.save("/Users/gordey/Projects/Tolik_Fallacy/background/"+scName+"_"+f+".png");
}
// -----------------------------------------------------  Shape
public void dragShape(){
  dragShape ^=true;
}
public void brushShape() {
  shapeSystem.setPos(currPoint, f, mouseX, mouseY);
}
public void shapeDisplay(){
  shapeDraw();
  if(pShape!=null)shape(pShape);
}
public void shapeAddFrames(){
  for(Point p :shapeSystem.points){
    PVector v = p.pos.get(f-1);
    p.pos.add(v);
  }
}
public void shapeAddPoint() {
  shapeSystem.addPoint(mouseX, mouseY);
}
public void shapePointUp() {
  if (currPoint<=shapeSystem.points.size()-1) {
    if (currPoint==shapeSystem.points.size()-1) {
      currPoint=0;
    } else {
      currPoint++;
    }
  }
}
public void shapePointDown() {
  if (currPoint>=0) {
    if (currPoint==0) {
      currPoint=shapeSystem.points.size()-1;
    } else {
      currPoint--;
    }
  }
}
public void shapeDraw() {
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
  public void addPoint(int x, int y) {
    Point p = new Point(x, y);
    points.add(p);
  }
  public void setPos(int pNum, int frame, int x, int y) {
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
  public void setPos(int index, int x, int y) {
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
// ----------------------------------------------------- Utilities 
public void dialBox(String m){
  javax.swing.JOptionPane.showMessageDialog(null, m);
}
public ArrayList filesToArrayList(String folderPath) {
  ArrayList<File> filesList = new ArrayList<File>();
  if (folderPath != null) {
    File file = new File(folderPath);
    File[] files = file.listFiles();
    for (int i = 0; i < files.length; i++) {
      filesList.add(files[i]);
    }
  }
  return(filesList);
}
int[][] swatches = {
    {
        color(248,229,209),
        color(219,254,137),
        color(211,236,255),
        color(142,203,213),
        color(243,255,172),
        color(156,255,185),
        color(227,150,233),
        color(188,178,255)
    },
    {
        color(110,255,2),
        color(235,255,19),
        color(255,9,79),
        color(8,10,10),
        color(211,253,253),
        color(197,255,48),
        color(231,83,49),
        color(214,9,44)
    },
    {
        color(254,5,12),
        color(138,4,109),
        color(254,129,253),
        color(8,10,11),
        color(244,255,143),
        color(173,9,70),
        color(242,250,1),
        color(189,199,219)
    },
    {
        color(57,150,254),
        color(147,83,255),
        color(254,26,14),
        color(243,255,133),
        color(250,23,30),
        color(29,136,82),
        color(243,250,39),
        color(235,145,24)
    },
    {
        color(85,193,144),
        color(240,255,92),
        color(187,89,68),
        color(193,188,140),
        color(234,255,10),
        color(254,3,15),
        color(254,184,224),
        color(253,178,94)
    },
    {
        color(240,255,11),
        color(167,255,2),
        color(193,147,254),
        color(246,1,11),
        color(236,0,22),
        color(227,61,241),
        color(219,128,190),
        color(208,120,254)
    },
    {
        color(206,206,194),
        color(254,3,66),
        color(119,62,255),
        color(168,80,57),
        color(236,255,24),
        color(173,236,148),
        color(87,202,140),
        color(194,61,176)
    },
    
};
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "Anima_1_10" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
