  class Data {
  String[] rows;
  String[][] data;   
  Data(String filename) {
    rows = loadStrings(filename); 
    data =  new String[rows.length][];
    for (int i = 0; i < rows.length; i++) {
      String[] pieces = split(rows[i], ',');
      data[i] = pieces;
    }
  }
}
void folderSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
    projPath = selection.getAbsolutePath();
    String m = cp5.get(Textfield.class,"input").getText();
    if(projState.equals("load")) loadProject();
    if(projState.equals("new")){
      String empty = "";
      if(m.equals(empty)){
        dialBox("Type scene name please");
      }else{
        sc = cp5.get(Textfield.class,"input").getText();
        newProject();
      }
    }
    println("User selected " + selection.getAbsolutePath());
  }
}
// -----------------------------------------------------  Load
public void loadProj(){
  projState = "load";
  selectFolder("Select a folder to process:", "folderSelected");
}
void countFramesInScenes(){  
  String[] m = match(scPath, "sc.*"); // sc23
  String[] m2 = match(m[0], "[0-9]+[0-9]|[0-9]"); // 23
  int scCounts = int(m2[0]); 
  framesInScenes = 0;
  if (scCounts>1) {  
    for (int i = 1; i < scCounts; ++i) {
      String[] cutNum = match(scPath, ".*/sc"); // path/sc
      String _scPath = cutNum[0]+str(i);
      Data prData = new Data(_scPath+"/ProjData.csv"); // path/sc/
      int _f_total = int(prData.data[1][1]);
      if (i==1) {
        audiopath = prData.data[1][4] ;
      }
      framesInScenes += _f_total;
    }
  }
}
void loadProject(){
  println("framesInScenes = " + framesInScenes);
  layers.clear(); 
  activeLayer = 0;
  ImgCount = 0;
  scPath = projPath+"/";
  // uncomment this to use audio:
//  countFramesInScenes();
  Data ld = new Data(scPath+"LayersData.csv");
  Data pd = new Data(scPath+"ProjData.csv");
  // ------------- Setup project var
  f =          int(pd.data[1][0]);
  f_total =    int(pd.data[1][1]);
  in =         int(pd.data[1][2]);
  out =        int(pd.data[1][3]);
  audiopath =      pd.data[1][4];
  layerCount = int(pd.data[1][5]) ;
  inoutStore[0] = in;
  inoutStore[1] = out;
  // ------------- Setup layers and frames in it
  int c = 1; // first row is a title, start from 1
  for(int i = 0; i < layerCount; i++ ){
    ArrayList<Frame> layer = new ArrayList<Frame>();
    ArrayList<Frame> loopedFrames = new ArrayList<Frame>(); // to search loop marked
    for(int k = 0; k < f_total; k++){
      if(boolean(ld.data[c][3])){ // find loop mark 
        putMatchFrameToLayer(loopedFrames, ld, layer, c);
      }else{ // or create new one
        Frame frame = frameWithVar(ld, c);
        layer.add(frame); //<>//
      }                    
      c++;
    }
    layers.add(layer);
  }
  loadAudio();
  buffSelected(); // Selected mean from in point to end point
  pause = false;
}
// Where Search, condition mark data, in which layer put similar frame, counter
void putMatchFrameToLayer(ArrayList<Frame> loopedFrames, Data ld, ArrayList<Frame> layer, int c){ 
  boolean finded = false;
  for(Frame frame: loopedFrames){ // search in looped frames
    int frameId =  int(ld.data[c][0]); // condition for match
    int randomId = int(ld.data[c][1]); // condition for match
    if(frame.frameId == frameId && 
       frame.randomId == randomId){
      layer.add(frame);
      println("finded!");
      finded = true;
      break;
    }
  }
  if(!finded){ // if not find create new
    Frame frame = frameWithVar(ld, c);
    loopedFrames.add(frame);
    layer.add(frame);
  }
}
Frame frameWithVar(Data d, int r){
  int frameId =         int(d.data[r][0]);
  Frame frame = new Frame(frameId);
  frame.randomId =      int(d.data[r][1]);
  frame.fileExist = boolean(d.data[r][2]);
  frame.loop =      boolean(d.data[r][3]);
  frame.col = color(  float(d.data[r][4]), 
                      float(d.data[r][5]), 
                      float(d.data[r][6]) );
  return frame;      
}
// -----------------------------------------------------  New
public void newProj(){ // public because it's a button
  pause = true;
  layers.clear(); 
  inoutStore[0] = 0;
  inoutStore[1] = 0;
  activeLayer = 0;
  layerCount = 1;
  f =          0;
  f_total =    1;
  in =         0;
  out =        0;
  ImgCount = 0;
  projState = "new";
  selectFolder("Select a folder to process:", "folderSelected"); 
}
void newProject(){
  scPath = projPath+"/"+sc+"/";
  // uncomment this to use audio in all scenes:
  // countFramesInScenes(); 
  ArrayList<Frame> layer0 = new ArrayList<Frame>();
  layer0.add(new Frame(0));
  layer0.get(0).makeImg();
  layers.add(layer0);
  pause = false;
  foldSelect = false; 
}
// -----------------------------------------------------  Save
public void saveProj(){ // public because it's a button
  output = createWriter(scPath+"LayersData.csv");
  String title = 
        "frameId"+","+
        "randomId"+","+ // Pimage not needed that's why it mised
        "fileExist"+","+
        "loop"+","+
        "colR"+","+
        "colG"+","+
        "colB"+","+
        "layerId";
  output.println(title);
  int layerId = 0;
  String info;
  for(ArrayList<Frame> layer: layers){
    for(Frame frame: layer){
      if(frame.fileExist && frame.change) frame.saveImg();
      if(frame.imgExist && !frame.fileExist) {
        frame.saveImg();
        info = "saving image";
      }
      String m =
      frame.frameId+","+
      frame.randomId+","+ // Pimage not needed that's why it mised
      frame.fileExist+","+// imgExist mised // change missed
      frame.loop+","+
      red(frame.col)+","+
      green(frame.col)+","+
      blue(frame.col)+","+
      layerId;
      output.println(m);
      
    }
    layerId++;
  }
  output.flush();
  output.close();  
    
  output = createWriter(scPath+"ProjData.csv");
  output.println("f"+","+
                 "f_total"+","+
                 "in"+","+
                 "out"+","+
                 "audiopath"+","+
                 "layerCount");
  output.println(f+","+
                 f_total+","+
                 in+","+
                 out+","+
                 audiopath+","+
                 layers.size());
  output.flush();
  output.close();  
  println("Saved!"); 
}
public void renderProj(){
  render();
}
