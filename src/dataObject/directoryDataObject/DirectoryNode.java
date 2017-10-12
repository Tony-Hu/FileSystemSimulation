package dataObject.directoryDataObject;

public class DirectoryNode {

  private DirectoryInfo[] directoryInfo;
  private DirectoryNode forward;
  private DirectoryNode backward;
  private DirectoryNode free;
  private DirectoryNode filler;

  public static final int MAX_INFO_SIZE = 31;
  public DirectoryNode(){
    directoryInfo = new DirectoryInfo[MAX_INFO_SIZE];
  }
}
