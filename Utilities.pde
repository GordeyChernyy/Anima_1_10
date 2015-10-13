// ----------------------------------------------------- Utilities 
void dialBox(String m){
  javax.swing.JOptionPane.showMessageDialog(null, m);
}
ArrayList filesToArrayList(String folderPath) {
  ArrayList<File> filesList = new ArrayList<File>();
  if (folderPath != null) {
    File file = new File(folderPath);
    File[] files = file.listFiles();
    for (int i = 0; i < files.length; i++) {
      filesList.add(files[i]);
    }
  }
  return(filesList);
}
ArrayList loadFilesToArray( String folderPath){
  ArrayList<PImage> images =  new ArrayList<PImage>();
  File folder = new File(folderPath);
  File[] files = folder.listFiles();
  for (int i = 0; i < files.length; i++) {
    PImage img = loadImage(files[i].getPath()); 
    images.add(img);
    println(files[i].getPath());
  }
  return images;
}
