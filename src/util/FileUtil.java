package util;

import dataObject.AbstractNode;
import dataObject.SectorInfo;
import dataObject.directoryDataObject.DirectoryInfo;
import dataObject.directoryDataObject.DirectoryNode;
import dataObject.fileDataObject.FileNode;

enum OpenType{input, output, update, closed}
public class FileUtil {

  private SectorInfo[] sectors;
  private int nextFreePos;
  private FileNode currentOpeningFile;
  private OpenType openType;
  private short currentPos;

  public static final int SECTOR_SIZE = 100;
  public static final int MAX_INFO_SIZE = 31;
  public static final int MAX_NAME_LENGTH = 9;
  public static final int DATA_SIZE = 504;

  public FileUtil(){
    sectors = new SectorInfo[SECTOR_SIZE];
    sectors[0] = new SectorInfo(new DirectoryNode());//Sector 0 always being a dir.
    nextFreePos = 1;
    openType = OpenType.closed;
    currentPos = 0;
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
        if (splits.length < 3){
          System.out.println("\"Create\" command too short.\n Syntax: create type name." );
        } else {
          open(splits[1], splits[2]);
        }
        break;
      case "close":
        close();
        break;
      case "delete":
        if (splits.length < 2){
          System.out.println("\"delete\" command too short.\n Syntax: delete name." );
        } else {
          delete(splits[1]);
        }
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
    if ("u".equals(type)){
      createFile(name);
    } else if ("d".equals(type)){
      createDir(name);
    } else {
      System.out.println("Invalid type: "+ type);
    }
  }

  private void createDir(String name){
    String[] paths = name.split("/");
    AbstractNode tempPtr = sectors[0].getNode();
    for (int i = 0; i < paths.length && tempPtr != null; i++){
      DirectoryInfo info =  ((DirectoryNode) tempPtr).seekName(paths[i]);
      if (info == null){//If the dir is currently not exist.
        tempPtr = createNewInfo('d', paths[i], tempPtr);
      } else if (info.getType() == 'u'){//If already exists a file with same name.
        System.out.println(paths[i] + " already exists as a file. Try another path name again!");
        return;
      } else {//The dir exists.
        tempPtr = info.getLink();
      }
    }
  }

  private void createFile(String name){
    String[] paths = name.split("/");
    AbstractNode tempPtr = sectors[0].getNode();
    for (int i = 0; i < (paths.length - 1) && tempPtr != null; i++){
      DirectoryInfo info =  ((DirectoryNode) tempPtr).seekName(paths[i]);
      if (info == null){//If the dir is currently not exist.
        tempPtr = createNewInfo('d', paths[i], tempPtr);
      } else if (info.getType() == 'u'){//If already exists a file with same name.
        System.out.println(paths[i] + " already exists as a file. Try another path name again!");
        return;
      } else {//The dir exists.
        tempPtr = info.getLink();
      }
    }
    //TODO - implements delete and recreate if file already exists
    currentOpeningFile = (FileNode) createNewInfo('u', paths[paths.length - 1], tempPtr);
    openType = OpenType.output;
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
    for (; counter < SECTOR_SIZE && sectors[nextFreePos] != null && !sectors[nextFreePos].isFree(); counter++){
      nextFreePos++;
      nextFreePos %= SECTOR_SIZE;
    }
    if (counter >= SECTOR_SIZE){//If there is no space available
      System.out.println("The entire disk sector is full!");
      return null;
    }

    if (sectors[nextFreePos] == null){
      AbstractNode newNode;
      if (type == 'd') {
        newNode = new DirectoryNode();
      } else {
        newNode = new FileNode();
      }
      sectors[nextFreePos] = new SectorInfo(newNode);
      nextFreePos++;
      nextFreePos %= SECTOR_SIZE;
      return newNode;
    } else {
      sectors[nextFreePos].setFree(false);
      AbstractNode node = sectors[nextFreePos].getNode();
      nextFreePos++;
      nextFreePos %= SECTOR_SIZE;
      return node;
    }
  }

  private void open(String mode, String name){
    switch(mode){
      case "i":
        openType = OpenType.input;
        break;
      case "o":
        openType = OpenType.output;
        break;
      case "u":
        openType = OpenType.update;
        break;
      default:
        System.out.println("Mode \"" + mode + "\" is invalid. Only \"u\" or \"o\" or \"i\" is allowed");
        return;
    }

    String[] paths = name.split("/");
    AbstractNode tempPtr = sectors[0].getNode();
    for (int i = 0; i < (paths.length - 1) && tempPtr != null; i++){
      DirectoryInfo info =  ((DirectoryNode) tempPtr).seekName(paths[i]);
      if (info == null || info.getType() != 'd'){
        System.out.println("Dir " + paths[i] + " does not exist!");
        openType = OpenType.closed;
        return;
      }
      tempPtr = info.getLink();
    }
    DirectoryInfo info =  ((DirectoryNode) tempPtr).seekName(paths[paths.length - 1]);
    if (info == null || info.getType() !='u'){
      System.out.println("File " + paths[paths.length - 1] + " does not exist!");
      openType = OpenType.closed;
      return;
    }
    currentOpeningFile = (FileNode) info.getLink();
    byte size = info.getSize();
    placePointer(size);
  }

  private void placePointer(byte size){
    switch(openType){
      case input:
      case update:
        currentPos = 0;
        break;
      case output:
        while (currentOpeningFile.getForward() != null){
          currentOpeningFile = (FileNode) currentOpeningFile.getForward();
        }
        currentPos = size;
      break;
      default:
        //Usually can't happen
        System.out.println("File already closed! Meet an error!");
        return;
    }
  }
  private void close(){
    if (openType != OpenType.closed){
      currentOpeningFile = null;
      openType = OpenType.closed;
    }
  }

  private void delete(String fileName){
    String[] paths = fileName.split("/");
    AbstractNode tempPtr = sectors[0].getNode();
    for (int i = 0; i < (paths.length - 1) && tempPtr != null; i++){
      DirectoryInfo info =  ((DirectoryNode) tempPtr).seekName(paths[i]);
      if (info == null || info.getType() != 'd'){
        System.out.println("Dir " + paths[i] + " does not exist! File deletion failed!");
        return;
      }
    }
    
    //TODO - finish this part
  }
}
