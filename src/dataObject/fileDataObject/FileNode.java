package dataObject.fileDataObject;

import dataObject.AbstractNode;

import java.util.Arrays;

import static util.FileUtil.DATA_SIZE;

public class FileNode extends AbstractNode {

  private char[] data;


  public FileNode(){
    data = new char[DATA_SIZE];
  }

  //TODO finish file node util method

  @Override
  public String toString() {
    return super.toString().split("\\.")[2];
  }

  @Override
  public String display() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < data.length && data[i] != '\u0000'; i++){
      sb.append(data[i]);
    }
    return sb.length() == 0 ? "The file is empty!" : sb.toString();
  }
}
