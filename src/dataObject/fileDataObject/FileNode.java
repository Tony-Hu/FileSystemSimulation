package dataObject.fileDataObject;

public class FileNode {
  private FileNode back;
  private FileNode forward;
  private char[] data;

  public static final int DATA_SIZE = 504;
  public FileNode(){
    data = new char[DATA_SIZE];
  }
}
