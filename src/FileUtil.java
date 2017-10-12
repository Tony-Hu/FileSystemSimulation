import dataObject.AbstractNode;
import dataObject.FreeBlockInfo;
import dataObject.directoryDataObject.DirectoryInfo;
import dataObject.directoryDataObject.DirectoryNode;
import dataObject.fileDataObject.FileNode;


public class FileUtil {

  private DirectoryNode[] directoryNodes;
  private FreeBlockInfo[] freeBlockList;
  private int nextFreePos;

  public static final int SECTOR_SIZE = 100;


  public FileUtil(){
    directoryNodes = new DirectoryNode[SECTOR_SIZE];
    directoryNodes[0] = new DirectoryNode();
    freeBlockList = new FreeBlockInfo[SECTOR_SIZE];
    freeBlockList[0] = new FreeBlockInfo(directoryNodes[0]);
    nextFreePos = 1;
  }

  public void parseCommand(String command){
    String lowerCaseCommand = command.toLowerCase();
    String[] splits = lowerCaseCommand.split("\\s+");//Split out all white spaces.
    if (splits.length == 0){
      System.out.println(("Invalid command: " + command));
      return;
    }

    switch(splits[0]){
      case "create":
        if (splits.length < 3){
          System.out.println("\"Create\" command too short.\n Syntax: create type name." );
        } else {
          create(splits[1], splits[2]);
        }
        break;
      case "open":

        break;
      case "close":

        break;
      case "read":

        break;
      case "write":

        break;
      case "seek":

        break;
      default:
        System.out.println(("Invalid argument: " + splits[0]));
        break;
    }
  }

  private void create(String type, String name){
    if (!"u".equals(type) || !"d".equals(type)){
      System.out.println("Type \"" + type + "\" is invalid. Only \"u\" or \"d\" is allowed");
      return;
    }

    String[] paths = name.split("/");
    DirectoryNode tempPtr = directoryNodes[0];
    for (int i = 0; i < (paths.length - 1) && tempPtr != null; i++){
      DirectoryInfo info = tempPtr.seekDir(paths[i]);
      if (info == null){
        AbstractNode node = getNextAvailableSector(type.charAt(0));
        if (!tempPtr.addInfo(type.charAt(0), name, node)){

        }

      } else if (info.getType() == 'u'){
        System.out.println(paths[i] + " already exists as a file. Try another path name again!");
        return;
      } else {

      }
    }
  }






  public AbstractNode getNextAvailableSector(char type){
    int counter = 0;
    for (; counter < SECTOR_SIZE && freeBlockList[nextFreePos] != null && !freeBlockList[nextFreePos].isFree(); counter++){
      nextFreePos++;
      nextFreePos %= SECTOR_SIZE;
    }
    if (counter >= SECTOR_SIZE){//If there is no space available
      System.out.println("The entire disk sector is full!");
      return null;
    }

    if (freeBlockList[nextFreePos] == null){
      AbstractNode newNode;
      if (type == 'd') {
        newNode = new DirectoryNode();
      } else {
        newNode = new FileNode();
      }
      freeBlockList[nextFreePos] = new FreeBlockInfo(newNode);
      nextFreePos++;
      nextFreePos %= SECTOR_SIZE;
      return newNode;
    } else {
      freeBlockList[nextFreePos].setFree(false);
      AbstractNode node = freeBlockList[nextFreePos].getNode();
      nextFreePos++;
      nextFreePos %= SECTOR_SIZE;
      return node;
    }
  }
}
