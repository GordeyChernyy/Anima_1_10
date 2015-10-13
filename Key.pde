void keyReleased(){
  if (keyCode == 157 ) CMD = false;
  if(!pause && !cp5.get(Textfield.class,"input").isActive()){
      if (key == 'g') brushEraseUp(); 
      if (key == 'c') brushDraftUp();
      if (key == 'k') moveImgXUp();
      if (key == 'l') moveImgYUp();
  }
}
void keyPressed() {
  if (keyCode == '`') renderFrame();
  if( !pause && !cp5.get(Textfield.class,"input").isActive()){
    if (key == '1') {colorChange(4);}
    if (key == '2') {colorChange(5);}
    if (key == '3') {colorChange(6);}
    if (key == '4') {colorChange(7);}
    if (key == '5') {colorChange(0);}
    if (key == '6') {colorChange(1);}
    if (key == '7') {colorChange(2);}
    if (key == '8') {colorChange(3);}
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
        if (key == 'd') brushSelect = 1;
        if (key == 'f') brushSelect = 3;
      }else{
        if (key == 'c') brushDraft();
        if (key == 'd') prevFrame();
        if (key == 'f') nextFrame();
      }      

    //    if (key == '8') shapePointDown();
    //    if (key == '9') shapePointUp();
    //    if (key == '0') dragShape();
    //    if (key == 'k') shapeAddPoint();
      if (key == 's') addFrame();
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
