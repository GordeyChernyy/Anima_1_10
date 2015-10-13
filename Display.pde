// -----------------------------------------------------  Display frames, onion, draft
void display(){
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
  if(backgroundMode && showTopImg && f<topImg.size()) image(topImg.get(f), 0, 0);
  tint(255, 255);
}
