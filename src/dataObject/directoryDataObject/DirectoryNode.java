package dataObject.directoryDataObject;

import dataObject.AbstractNode;

public class DirectoryNode extends AbstractNode{

  private DirectoryInfo[] directoryInfo;
  private DirectoryNode nextFreeNode;
  private DirectoryNode filler;//Unused for this project.

  public static final int MAX_INFO_SIZE = 31;
  public DirectoryNode(){
    initializeDirInfo();
  }


  public DirectoryInfo[] getDirectoryInfo() {
    return directoryInfo;
  }

  public void setDirectoryInfo(DirectoryInfo[] directoryInfo) {
    this.directoryInfo = directoryInfo;
  }


  public DirectoryInfo seekDir(String name){
    DirectoryNode directory = this;
    do {
      for (DirectoryInfo info : directory.getDirectoryInfo()) {
        if (name.equals(info.getName()) && info.getType() != 'f') {
          return info;
        }
      }
      directory = (DirectoryNode) directory.getForward();
    } while (directory != null);

    return null;
  }

  private void initializeDirInfo(){
    directoryInfo = new DirectoryInfo[MAX_INFO_SIZE];
    for (int i = 0; i < MAX_INFO_SIZE; i++){
      directoryInfo[i] = new DirectoryInfo();
    }
  }

  public boolean addInfo(char type, String name, AbstractNode link){
    for (int i = 0; i < MAX_INFO_SIZE; i++){
      if (directoryInfo[i].getType() == 'f'){
        directoryInfo[i].setInfo(type, name, link);
        return true;
      }
    }
    return false;
  }
}
