package dataObject.directoryDataObject;

import dataObject.AbstractNode;
import dataObject.SectorInfo;

import java.util.Arrays;

import static util.FileUtil.MAX_INFO_SIZE;

public class DirectoryNode extends AbstractNode{

  private DirectoryInfo[] directoryInfo;

  public SectorInfo getNextFreeNode() {
    return nextFreeNode;
  }

  public void setNextFreeNode(SectorInfo nextFreeNode) {
    this.nextFreeNode = nextFreeNode;
  }

  private SectorInfo nextFreeNode;
  private SectorInfo filler;//Unused for this project.

  public DirectoryNode(){
    initializeDirInfo();
  }


  public DirectoryInfo[] getDirectoryInfo() {
    return directoryInfo;
  }

  public void setDirectoryInfo(DirectoryInfo[] directoryInfo) {
    this.directoryInfo = directoryInfo;
  }

  //Return null if not found.
  public DirectoryInfo seekName(String name){
    DirectoryNode directory = this;
    do {
      for (DirectoryInfo info : directory.getDirectoryInfo()) {
        if (info.haveSameName(name) && info.getType() != 'f') {
          return info;
        }
      }
      if (directory.getForward() == null){
        break;
      }
      directory = (DirectoryNode) directory.getForward().getNode();
    } while (directory != null);

    return null;
  }

  private void initializeDirInfo(){
    directoryInfo = new DirectoryInfo[MAX_INFO_SIZE];
    for (int i = 0; i < MAX_INFO_SIZE; i++){
      directoryInfo[i] = new DirectoryInfo();
    }
  }

  public boolean addInfo(char type, String name, SectorInfo link){
    for (int i = 0; i < MAX_INFO_SIZE; i++){
      if (directoryInfo[i].getType() == 'f'){
        directoryInfo[i].setInfo(type, name, link);
        return true;
      }
    }
    return false;
  }

  @Override
  public String display() {
    StringBuilder sb = new StringBuilder();
    sb.append("Forward: ").append(getForward()).append("\n");
    sb.append("Backward: ").append(getBack()).append("\n");
    sb.append("Free: ").append(getNextFreeNode()).append("\n");
    sb.append("\n");
    sb.append("#").append("\t").append("type").append("\t").append("name").append("\t")
        .append("link").append("\t\t\t\t\t").append("size").append("\n");
    for (int i = 0; i < MAX_INFO_SIZE; i++){
      if (directoryInfo[i].getType() != 'f') {
        sb.append(i).append("\t");
        sb.append(directoryInfo[i]);
        sb.append("\n");
      }
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return super.toString().split("\\.")[2];
  }
}
