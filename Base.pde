// -----------------------------------------------------  Base controls
public void showOnion(){ // button
  showOni();
}
void showOni(){
  showOnion ^= true; 
}
void test(){
  Frame frame = new Frame(1);
  boolean b = layers.get(0).contains(frame);
  println(b);
}
void addFrame(){
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
void deleteF(){
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
void nextFrame(){
  if(f<out)f++;
  thread("buffNextFrame"); 
  if(soundEnable && !mute && audioThread)thread("playAudio");
}
void prevFrame(){
  if(f>in)f--;
  thread("buffPrevFrame");
  if(soundEnable && !mute && audioThread)thread("playAudio");
}
void deleteFrame(){
  Frame frame = layers.get(activeLayer).get(f);
  if(frame.loop){
    Frame frameN = new Frame(f);
    layers.get(activeLayer).set(f,frameN);  // set blank 
  }else{
    frame.removeImg();
    frame.removeFile();
  }
}
