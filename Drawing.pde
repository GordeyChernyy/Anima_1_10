// -----------------------------------------------------  Swatches, drawing, brushes
void brushEraseUp(){
  brushSelect = storeLastBrush;
}
void brushErase(){
  storeLastBrush = brushSelect;
  brushSelect = 2;
}
void brushDraft(){
  storeLastBrush = brushSelect;
  brushSelect = 4;
}
void brushDraftUp(){
  brushSelect = storeLastBrush;
}
void draftErase(){
  draft.beginDraw();
  draft.clear();
  draft.endDraw();
}
void brushPlain(){
  brushSelect = 0;
}
void brushWC(){
  brushSelect = 1;
}
void colorChange(int num){
  col = swatches[swrow][num];
}
void swatchChange(boolean up){
  if(up){
    if(swrow<swatches.length-1)swrow++;
  }else{
    if(swrow>0)swrow--; 
  }
}
