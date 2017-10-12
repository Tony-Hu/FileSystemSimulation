package dataObject.fileDataObject;

import dataObject.AbstractNode;

public class FileNode extends AbstractNode {
  private char[] data;

  public static final int DATA_SIZE = 504;
  public FileNode(){
    data = new char[DATA_SIZE];
  }
}
