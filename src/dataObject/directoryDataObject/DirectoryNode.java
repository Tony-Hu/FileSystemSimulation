package dataObject.directoryDataObject;

import dataObject.AbstractNode;
import util.FileUtil;

import java.util.Arrays;

import static util.FileUtil.MAX_INFO_SIZE;

public class DirectoryNode extends AbstractNode{

  private DirectoryInfo[] directoryInfo;
  private DirectoryNode nextFreeNode;
  private DirectoryNode filler;//Unused for this project.

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
        if (info.haveSameName(name) && info.getType() != 'f') {
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
