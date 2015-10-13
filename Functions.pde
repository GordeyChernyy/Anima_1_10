// -----------------------------------------------------  Layers
void newLayer(){
  ArrayList<Frame> layer = new ArrayList<Frame>();
  for(int i=0; i<f_total; i++){
    layer.add(new Frame(i));
  }
  layers.add(layer);
}
void layerUp(){
  if(activeLayer<layers.size()-1 && activeLayer>=0){activeLayer++;}
}
void layerDown(){
  if(activeLayer<layers.size() && activeLayer>0){activeLayer--;}
}
