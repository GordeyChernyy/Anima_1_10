// -----------------------------------------------------  Controls
void cp5Setup(){
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
  cp5.addToggle("showTopImg")
     .setColorBackground(buttonColor)
     .setValueLabel("showTopImg")
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
  addSlider("wcLineWidth", 0.1, 5, g1);
  addSlider("wcFillOpacity", 0, 1, g1);
  addSlider("wcStrokeOpacity", 0, 1, g1);
  addSlider("wcLifeMin", 0, 300, g1);
  addSlider("wcLifeMax", 0, 300, g1);
  addSlider("wcRandMult", 0.02, 5, g1);
  addSlider("wcLifeSpeed", 0, 10, g1);
  addSlider("wcOpacitySpeed", 0, 100, g1);
  addSlider("eraseSize", 0, 300, g1);
  
  cp5.end();
}

void hideUI(){
    if(cpHide){
      cp5.hide();
    }else{
      cp5.show();
    }
    cpHide ^=true; 
}
void addSlider(String name, float in, float out, Group g){
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
void uiDraw(){
  pushMatrix(); 
  translate(w/2, h); // to center 
    
    uiMove();
    uiPointer();
  popMatrix();
  uiSwatch();
}
void uiSwatch(){
  int rs = 20;
  int posX = w-rs*9;
  for(int i = 0; i < 8; i++ ){
    noStroke();
    fill(swatches[swrow][i]);
    rect(i*rs+posX,10,rs,rs);
  }
}
void uiShowFrame(){
  int textsize = 10;
  int leading = 16;
  fill(255, 0, 0);
  textSize(textsize);
  textLeading(leading);
  String info = "frame: "+f+"\n"+"time: "+nf(f/12.0, 0, 2)+ " seconds";
  text(info, 0, -26);
}
void uiInfo(){
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
                "Play fps  " + int(frameRate/fpsSpeed)
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
void uiPointer(){
  pushMatrix(); 
  translate(uiFSize, 0);
    noFill(); 
    stroke(255, 0, 0); 
    strokeWeight(2);
    //line(uiFSize/2,0,uiFSize/2,uiSize); // vertical line
    rect(0,0, uiFSize, uiFSize);
  popMatrix();
}

void uiMove(){
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
void uiFrames(){
  int y = 0;
  for(ArrayList<Frame> layer: layers){
    int x = 0;
    pushMatrix(); 
    translate(0,-uiFSize*y);
      for(Frame frame: layer){
        strokeWeight(0.5);
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
void uiFrameInfo(Frame frame, int x){
  uiFSize = 40;
  String frameId = str(frame.frameId);
  textSize(12);
  fill(0);
  text(frameId,uiFSize*x,10 );
}
