// -----------------------------------------------------  Play
void playTwo(){
  playSwitcher ^= true;
  
  if(playSwitcher){
    if(f!=f_total-1){ in = f; out = f+1;}
    if(soundEnable)player.mute();
  }else{
    in = inoutStore[0];
    out = inoutStore[1];
    if(soundEnable)player.unmute();
  }
  playSwitch();
}
void playSwitch(){
  play ^= true;
  fpsCount=0;
  if(play){
    if(soundEnable) thread("playAudioFromIn");
  }else{if(soundEnable) player.pause();} 
}
void play(){
  fpsCount++;
  if(fpsCount%fpsSpeed==0){  
    if(f<=out+1){
      if (fillFrames>0) { // start filling shape
        println("fillFrames = "+fillFrames);
        brushFillRelease();
        fillFrames--;
      }
      f++;
    }
    if(soundEnable) thread("playAudioSync");
  }
  if(f==out+1){
    f=in;
  }
  
}
void fpsSpeedUp(){
  if(fpsSpeed>1){fpsSpeed--;}
}
void fpsSpeedDown(){
  fpsSpeed++;
}
