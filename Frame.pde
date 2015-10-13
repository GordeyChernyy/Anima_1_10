class Frame{
  int posX = 0;
  int posY = 0;
  int frameId;
  int randomId;
  PGraphics img;
  boolean fileExist = false;
  boolean imgExist = false;
  boolean change = false;
  boolean loop = false;
  color col;
  Frame(int f_ID){
    frameId = f_ID;
  }
  
  void makeImg(){
    img = createGraphics(w,h, JAVA2D);
    ImgCount++;
    imgExist = true;
  }
  void saveImg(){
    if(!fileExist) randomId = int(random(1000));
    String savePath = scPath+
                      frameId+"_"+
                      randomId+
                      ".png";  
    if(img!=null)img.save(savePath);
    fileExist = true;
    change = false;
  }
  void loadImg(){
    img = createGraphics(w,h, JAVA2D);
    String loadPath = scPath+
                      frameId+"_"+
                      randomId+
                      ".png";
    PImage tempImg = loadImage(loadPath);
    img.beginDraw();
    img.image(tempImg,0,0);
    ImgCount++;
    imgExist = true;
  }
  void removeImg(){
    g.removeCache(img);
    System.gc();
    img = null;   
    imgExist = false;
    ImgCount--;
  }
  void removeFile(){
    fileExist = false;
  }
  void setChange(boolean b){
    change = b;
  }
  PGraphics getImg(){
    return img;
  }
  boolean fileExist(){
    return fileExist;
  }
  boolean imgExist(){
    return imgExist;
  }
}
