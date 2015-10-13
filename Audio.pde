// ----------------------------------------------------- Audio
void muteAudio(){
  if(player.isMuted()){
    player.unmute();
    mute = false;
  }else{
    player.mute();
    mute = true;
  }
}
void playAudio(){
  audioThread = false;
  if(audioExist && !mute && (f+framesInScenes)*1000/12< player.length()){
      player.setLoopPoints((f+framesInScenes)*1000/12, ((f+framesInScenes)+1)*1000/12);
      player.loop(0);
   }
  audioThread = true;
}
void playAudioFromIn(){
  audioThread = false;
  if(audioExist && !mute && (f+framesInScenes)*1000/12< player.length()){
    player.setLoopPoints((in+framesInScenes)*1000/12, (out+framesInScenes)*1000/12);
    player.loop();
    player.cue((f+framesInScenes)*1000/12);
  }
  audioThread = true;
}
void playAudioSync(){  
audioThread = false;
 if(audioExist && !mute && (f+framesInScenes)*1000/12< player.length()){
      player.cue((f+framesInScenes)*1000/12);
      player.play();
   }
  audioThread = true;
}
public void LoadAudio(){
  selectFolder("Select Audio:", "audioSelected");
}
void audioSelected(File selection){
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
    audiopath = selection.getAbsolutePath();
    loadAudio();
  }
}
void loadAudio(){
  File audiofile = new File(audiopath);
  if(audiofile.exists()){
    player = minim.loadFile(audiopath);
    audioExist = true;
    println("Audio loaded!");
  }
}
