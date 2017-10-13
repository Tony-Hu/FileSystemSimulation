package util;

import dataObject.AbstractNode;
import dataObject.FreeBlockInfo;
import dataObject.directoryDataObject.DirectoryInfo;
import dataObject.directoryDataObject.DirectoryNode;
import dataObject.fileDataObject.FileNode;


public class FileUtil {

  private AbstractNode[] sectors;//TODO have bug to register sectors.(Unused)
  private FreeBlockInfo[] freeBlockList;
  private int nextFreePos;

  public static final int SECTOR_SIZE = 100;
  public static final int MAX_INFO_SIZE = 31;
  public static final int MAX_NAME_LENGTH = 9;
  public static final int DATA_SIZE = 504;

  public FileUtil(){
    sectors = new DirectoryNode[SECTOR_SIZE];
    sectors[0] = new DirectoryNode();//Sector 0 always being a dir.
    freeBlockList = new FreeBlockInfo[SECTOR_SIZE];
    freeBlockList[0] = new FreeBlockInfo(sectors[0]);
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
    if (!"u".equals(type) && !"d".equals(type)){
      System.out.println("Type \"" + type + "\" is invalid. Only \"u\" or \"d\" is allowed");
      return;
    }

    String[] paths = name.split("/");
    AbstractNode tempPtr = sectors[0];
    for (int i = 0; i < (paths.length - 1) && tempPtr != null; i++){
      DirectoryInfo info =  ((DirectoryNode) tempPtr).seekDir(paths[i]);
      if (info == null){//If the dir is currently not exist.
        tempPtr = createNewInfo('d', paths[i], tempPtr);
      } else if (info.getType() == 'u'){//If already exists a file with same name.
        System.out.println(paths[i] + " already exists as a file. Try another path name again!");
        return;
      } else {//The dir exists.
        tempPtr = info.getLink();
      }
    }
    createNewInfo(type.charAt(0), paths[paths.length - 1], tempPtr);
  }


  private AbstractNode createNewInfo(char type, String name, AbstractNode currentDir){
    if (!(currentDir instanceof DirectoryNode)){//Can't happen. just in case.
      System.out.println("Current node is not a directory node!");
      return null;
    }

    AbstractNode node = getNextAvailableSector(type);
    DirectoryNode previous;
    DirectoryNode current = (DirectoryNode) currentDir;
    do{
      if (current.addInfo(type, name, node)) {
        return node;
      }
      previous = current;
      current = (DirectoryNode) currentDir.getForward();
    } while (current != null);

    DirectoryNode newDirNode = (DirectoryNode) getNextAvailableSector('d');
    newDirNode.addInfo(type, name, node);
    previous.setForward(newDirNode);

    return node;
  }



  private AbstractNode getNextAvailableSector(char type){
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
