package dataObject.fileDataObject;

import dataObject.AbstractNode;

import static util.FileUtil.DATA_SIZE;

public class FileNode extends AbstractNode {

  private char[] data;


  public FileNode(){
    data = new char[DATA_SIZE];
  }

}
