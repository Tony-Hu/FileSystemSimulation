package dataObject.fileDataObject;

import dataObject.AbstractNode;

import java.util.Arrays;

import static util.FileUtil.DATA_SIZE;

public class FileNode extends AbstractNode {

  private char[] data;


  public FileNode(){
    data = new char[DATA_SIZE];
  }

  public String read(int beginIndex, int endIndex){
    if (beginIndex < 0 || beginIndex >= endIndex || endIndex > DATA_SIZE){
      return "Read index out of bound!";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = beginIndex; i < endIndex; i++){
      if (data[i] == '\u0000'){
        sb.append("\n").append("Request to read ").append(endIndex - beginIndex).append(" bytes.")
            .append(" But only ").append(i).append(" bytes are available").append("\n");
        break;
      }
      sb.append(data[i]);
    }
    return sb.toString();
  }


  public void writeData(int offset, String data){
    System.arraycopy(data.toCharArray(), 0, this.data, offset, data.length());
  }
  @Override
  public String toString() {
    return super.toString().split("\\.")[2];
  }

  @Override
  public String display() {
    StringBuilder sb = new StringBuilder();
    sb.append(super.display());
    for (int i = 0; i < data.length && data[i] != '\u0000'; i++){
      sb.append(data[i]);
    }
    return sb.length() == 0 ? "The file is empty!" : sb.toString();
  }
}
