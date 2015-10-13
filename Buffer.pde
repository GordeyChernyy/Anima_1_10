// -----------------------------------------------------  Buffering
void translateImg(){
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
void moveImgX(){
  if(buffImage!=null) moveImgX = true;
}
void moveImgY(){
  if(buffImage!=null) moveImgY = true;
}
void moveImgXUp(){
  moveImgX = false;
}
void moveImgYUp(){
  moveImgY = false;
}
void copyImage(){
  imgPosX = 0 ; imgPosY = 0;
  buffImage = layers.get(activeLayer).get(f).img;
} 
void pasteImage(){
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
void addImage(){
  Frame frame = layers.get(activeLayer).get(f);
  if(frame.img==null){
    frame.makeImg();
  }
  PGraphics pg = frame.img;
  pg.beginDraw();
  pg.image(buffImage,0,0);
  pg.endDraw();
}
void buffSelected(){
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
void buffNewFrame(){
  if(ImgCount>buffCount){buffClearFromStart();}    
}
void buffPrevFrame(){
  for(ArrayList<Frame> layer: layers){
    Frame frame = layer.get(f);
    if(!frame.imgExist && frame.fileExist ){frame.loadImg();}
  }
  while(ImgCount>buffCount){buffClearFromEnd();}
}
void buffNextFrame(){
  for(ArrayList<Frame> layer: layers){
    Frame frame = layer.get(f);
    if(!frame.imgExist && frame.fileExist ){frame.loadImg();}
  }
  while(ImgCount>buffCount){buffClearFromStart();}   
}
void buffClearFromStart(){
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
void buffClearFromEnd(){
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
