// -----------------------------------------------------  Loop
void setInPoint(){
  in = f; inoutStore[0] = in;
}
void setOutPoint(){
  out = f; inoutStore[1] = out;
}
void resetInOut(){
  in = 0; out = f_total-1;
  inoutStore[0] = in;
  inoutStore[1] = out;
}
void loopTwo(){
  loopFrames();
}
void loopFrames(){
  int fCount=0;
  ArrayList<Frame> acLa = layers.get(activeLayer);
  for(int i = f; i<f_total; i++){ // find where frames ending
    Frame frame = acLa.get(i);
    float r = random(150,255);
    float g = random(120,255);
    float b = random(180,255);
    color col = color(r, g, b);
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
void addFramesToLayer(ArrayList<Frame> layer, int fCount, int l){
  for(int i=0; i<fCount; i++){
    Frame frame = new Frame(f_total);
    layer.add(frame);
  }
}
