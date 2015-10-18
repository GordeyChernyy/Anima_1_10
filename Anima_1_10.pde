int w = 1280;
int h = 720;
import java.util.*;
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
import controlP5.*;
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
color col = color(0);
import codeanticode.tablet.*;
Tablet tablet;
color colorBg = color(245);
int brushSelect = 0;
ArrayList <PVector> wcHisUp = new ArrayList<PVector>();
ArrayList <PVector> wcHisDn = new ArrayList<PVector>();
int hisBrushSize = 300;
ParticleSystem ps; // watercolor
float wcLineWidth = 1.2;
float wcFillOpacity = 0.067;
float wcStrokeOpacity = 1.0;
int wcLifeMin = 20;
int wcLifeMax = 50;
int wcRandMult = 2;
float wcLifeSpeed = 2.0;
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
import ddf.minim.*;
Minim minim;
boolean mute = false;
AudioPlayer player;
int playersCount = 0;
ArrayList players = new ArrayList();

boolean audioExist = false;
// -----------------------------------------------------  Key
boolean CMD = false;
// -----------------------------------------------------  Translate
boolean moveImgX = true;
boolean moveImgY = false;
int imgPosX = 0;
int imgPosY = 0;
int moveStep = 10;
// -----------------------------------------------------  BackgroundMode
boolean backgroundMode = false;
boolean soundEnable = false;
ArrayList<PImage> topImg = new ArrayList<PImage>();
boolean showTopImg = true;
void setup(){
  size(w,h+uiSize, OPENGL);
  noCursor();  
  cp5Setup();
  if(backgroundMode) topImg = loadFilesToArray("/Users/gordey/Projects/Glue/mask/");
//  shapeSystem = new ShapeSys();
  draft = createGraphics(w,h,JAVA2D);
  minim = new Minim(this);
  tablet = new Tablet(this);   
  ps = new ParticleSystem(new PVector(0,0));
  frameRate(60);
}

void draw(){
  background(colorBg);
  if(!pause){
    brushDraw();
    if(play)play();
    
//    if(dragShape)brushShape();
//    shapeDisplay();
    if(render){
      renderFrames();
    }else{
      display();
      if(frameCount%2000==0) thread("saveProj");
    }
    if(cpHide) uiInfo();
    uiDraw(); 
  }
}
void mousePressed() {
}
void mouseReleased( ){
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
void mouseDragged(){
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
void mouseWheel(MouseEvent event) {
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
