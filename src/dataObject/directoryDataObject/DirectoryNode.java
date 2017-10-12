package dataObject.directoryDataObject;

import dataObject.AbstractNode;

public class DirectoryNode extends AbstractNode{

  private DirectoryInfo[] directoryInfo;
  private DirectoryNode free;
  private DirectoryNode filler;

  public static final int MAX_INFO_SIZE = 31;
  public DirectoryNode(){
    directoryInfo = new DirectoryInfo[MAX_INFO_SIZE];
  }
}
