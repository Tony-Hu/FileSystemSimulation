package dataObject.directoryDataObject;

public class DirectoryInfo {

  private char type;
  private char[] name;
  private DirectoryNode link;
  private byte size;

  public static final int MAX_NAME_LENGTH = 9;
  public DirectoryInfo(){
    type = 'F';
    name = new char[MAX_NAME_LENGTH];
  }
}
