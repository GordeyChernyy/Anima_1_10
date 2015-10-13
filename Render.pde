// -----------------------------------------------------  Render
void render(){
  render = true;
  f = 0;
  renderCounter = 0;
  showOnion = false;
}
void renderFrames(){
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
  renderImage.save("/Users/gordey/Projects/Tolik_Fallacy_II/Animation/unicFrames/"+scName+"_"+numSeqNaming(f)+".png");
}

String numSeqNaming(int num){
  String s = "";
  if(num<1000) s = str(num);
  if(num<100) s = "0"+str(num);
  if(num<10) s = "00"+str(num);
  return s; 
}
