package util;

import dataObject.AbstractNode;
import dataObject.SectorInfo;
import dataObject.directoryDataObject.DirectoryInfo;
import dataObject.directoryDataObject.DirectoryNode;
import dataObject.fileDataObject.FileNode;

enum OpenType{input, output, update, closed}
public class FileUtil {


  private SectorsUtil sectors;
  private DirectoryInfo currentOpeningFileInfo;
  private FileNode currentOpeningFile;
  private OpenType openType;
  private int currentOffset;

  public static final int SECTOR_SIZE = 100;
  public static final int MAX_INFO_SIZE = 31;
  public static final int MAX_NAME_LENGTH = 9;
  public static final int DATA_SIZE = 504;

  public FileUtil() {
    sectors = new SectorsUtil();
    openType = OpenType.closed;
    currentOffset = 0;
  }

  public void parseCommand(String command) {
    String lowerCaseCommand = command.toLowerCase();
    String[] splits = lowerCaseCommand.split("\\s+");//Split out all white spaces.
    if (splits.length == 0) {
      System.out.println(("Invalid command: " + command));
      return;
    }

    switch (splits[0]) {
      case "create":
        if (splits.length < 3) {
          System.out.println("\"Create\" command too short.\n Syntax: create type name.");
        } else {
          try {
            create(splits[1], splits[2]);
            displaySectors(command);
          } catch (DiskFullException e){
            System.out.println("Disk is full!");
          }
        }
        break;
      case "open":
        if (splits.length < 3) {
          System.out.println("\"Open\" command too short.\n Syntax: open mode name.");
        } else {
          open(splits[1], splits[2]);
        }
        break;
      case "close":
        close();
        break;
      case "delete":
        if (splits.length < 2) {
          System.out.println("\"Delete\" command too short.\n Syntax: delete name.");
        } else {
          delete(splits[1]);
          displaySectors(command);
        }
        break;
      case "read":
        if (splits.length < 2) {
          System.out.println("\"Read\" command too short.\n Syntax: read n.");
        } else {
          read(splits[1]);
        }
        break;
      case "write":
        if (splits.length < 3) {
          System.out.println("\"Write\" command too short.\n Syntax: write n 'data'.");
        } else {
          try {
            write(splits[1], splits[2]);
            displaySectors(command);
          } catch (DiskFullException e){
            System.out.println("Disk is full!");
            currentOffset = 0;
          }
        }
        break;
      case "seek":
        if (splits.length < 3) {
          System.out.println("\"Seek\" command too short.\n Syntax: seek base offset.");
        } else {
          seek(splits[1], splits[2]);
          //displaySectors(command);
        }
        break;
      default:
        System.out.println(("Invalid argument: " + splits[0]));
        break;
    }

  }

  private void create(String type, String name) throws DiskFullException{
    if ("u".equals(type)) {
      createFile(name);
    } else if ("d".equals(type)) {
      createDir(name);
    } else {
      System.out.println("Invalid type: " + type);
    }
  }

  private void createDir(String name) throws DiskFullException{
    String[] paths = name.split("/");
    SectorInfo tempPtr = sectors.getSectorZero();
    for (int i = 0; i < paths.length && tempPtr != null; i++) {
      DirectoryInfo info = ((DirectoryNode) tempPtr.getNode()).seekName(paths[i]);
      if (info == null) {//If the dir is currently not exist.
        tempPtr = createNewInfo('d', paths[i], tempPtr).getLink();
      } else if (info.getType() == 'u') {//If already exists a file with same name.
        System.out.println(paths[i] + " already exists as a file. Try another path name again!");
        return;
      } else {//The dir exists.
        tempPtr = info.getLink();
      }
      if (tempPtr == null){
        break;
      }
    }
  }

  private void createFile(String name) throws DiskFullException {
    String[] paths = name.split("/");
    SectorInfo tempPtr = sectors.getSectorZero();
    for (int i = 0; i < (paths.length - 1) && tempPtr != null; i++) {
      DirectoryInfo info = ((DirectoryNode) tempPtr.getNode()).seekName(paths[i]);
      if (info == null) {//If the dir is currently not exist.
        tempPtr = createNewInfo('d', paths[i], tempPtr).getLink();
      } else if (info.getType() == 'u') {//If already exists a file with same name.
        System.out.println(paths[i] + " already exists as a file. Try another path name again!");
        return;
      } else {//The dir exists.
        tempPtr = info.getLink();
      }
    }
    if (tempPtr == null) {
      System.out.println("No such dir " + paths[paths.length - 2]);
      return;
    }
    if (tempPtr.getNode() == null) {
      System.out.println("No node bind with the sector " + tempPtr);
      System.out.println("Create file failed!");
      return;
    }
    DirectoryInfo info = ((DirectoryNode) tempPtr.getNode()).seekName(paths[paths.length - 1]);
    if (info == null) {//File not exists before
      currentOpeningFileInfo = createNewInfo('u', paths[paths.length - 1], tempPtr);
      currentOpeningFile = (FileNode) currentOpeningFileInfo.getLink().getNode();
    } else if (info.getType() == 'u') {//File already exists, recreate it.
      delete(info);
      currentOpeningFileInfo = createNewInfo('u', paths[paths.length - 1], tempPtr);
      currentOpeningFile = (FileNode) currentOpeningFileInfo.getLink().getNode();
    } else if (info.getType() == 'd') {
      System.out.println("File already exists as a dir. Fail to create file!");
      return;
    }
    openType = OpenType.output;
  }

  private DirectoryInfo createNewInfo(char type, String name, SectorInfo currentDir) throws DiskFullException {
    if (!(currentDir.getNode() instanceof DirectoryNode)) {//Can't happen. just in case.
      System.out.println("Current node is not a directory node!");
      return null;
    }

    SectorInfo node = sectors.getNextAvailableSector(type);
    SectorInfo previous;
    SectorInfo current = currentDir;
    DirectoryInfo result;
    do {
      if ((result = ((DirectoryNode) current.getNode()).addInfo(type, name, node)) != null) {
        return result;
      }
      previous = current;
      current = current.getNode().getForward();
    } while (current != null);

    SectorInfo newDirNode = sectors.getNextAvailableSector('d');
    newDirNode.getNode().setBack(previous);
    result = ((DirectoryNode) newDirNode.getNode()).addInfo(type, name, node);
    previous.getNode().setForward(newDirNode);
    return result;
  }


  private void open(String mode, String name) {
    switch (mode) {
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
    SectorInfo tempPtr = sectors.getSectorZero();
    for (int i = 0; i < (paths.length - 1) && tempPtr != null; i++) {
      DirectoryInfo info = ((DirectoryNode) tempPtr.getNode()).seekName(paths[i]);
      if (info == null || info.getType() != 'd') {
        System.out.println("Dir " + paths[i] + " does not exist!");
        openType = OpenType.closed;
        return;
      }
      tempPtr = info.getLink();
    }

    if (tempPtr == null) {
      System.out.println("No such dir " + paths[paths.length - 2]);
      return;
    }
    if (tempPtr.getNode() == null) {
      System.out.println("No node bind with the sector " + tempPtr);
      System.out.println("Open file failed!");
      return;
    }
    DirectoryInfo info = ((DirectoryNode) tempPtr.getNode()).seekName(paths[paths.length - 1]);
    if (info == null || info.getType() != 'u') {
      System.out.println("File " + paths[paths.length - 1] + " does not exist!");
      openType = OpenType.closed;
      return;
    }
    currentOpeningFileInfo = info;

    placeFilePointer(info.getSize());
  }

  private void placeFilePointer(short size) {
    switch (openType) {
      case input:
      case update:
        reWind();
        break;
      case output:
        locAtEndOfFile();
        break;
      default:
        //Usually can't happen
        System.out.println("File already closed! Meet an error!");
    }
  }

  private void close() {
    if (openType != OpenType.closed) {
      currentOpeningFileInfo = null;
      currentOpeningFile = null;
      openType = OpenType.closed;
    }
    System.out.println("File " + currentOpeningFileInfo.getName() + " close successful!");
  }

  private void delete(String fileName) {
    String[] paths = fileName.split("/");
    SectorInfo tempPtr = sectors.getSectorZero();
    for (int i = 0; i < (paths.length - 1) && tempPtr != null; i++) {
      DirectoryInfo info = ((DirectoryNode) tempPtr.getNode()).seekName(paths[i]);
      if (info == null || info.getType() != 'd') {
        System.out.println("Dir " + paths[i] + " does not exist! File deletion failed!");
        return;
      } else {
        tempPtr = info.getLink();
      }
    }
    if (tempPtr == null) {
      System.out.println("No such dir " + paths[paths.length - 2]);
      return;
    }
    if (tempPtr.getNode() == null) {
      System.out.println("No node bind with the sector " + tempPtr);
      System.out.println("Delete file failed!");
      return;
    }
    DirectoryInfo infoToBeDelete = ((DirectoryNode) tempPtr.getNode()).seekName(paths[paths.length - 1]);
    delete(infoToBeDelete);
  }


  private void delete(DirectoryInfo info) {
    if (info.getType() != 'u') {
      System.out.println("Only file can be deleted!");
      return;
    }
    String fileName = info.getName();
    SectorInfo sector = info.getLink();
    info.setType('f');
    do {
      AbstractNode tempNode = sector.getNode();
      sector.setNode(null);
      sector.setFree(true);

      if (tempNode == null) {
        break;
      }
      sector = tempNode.getForward();
    } while (sector != null);

    System.out.println("File " + fileName + " delete successfully!");
  }

  public void displaySectors(String command) {
    System.out.println("The command is:" + command);
    System.out.println("\nThe Sector looks like this: ");
    System.out.println(sectors.display());
  }


  private void read(String bytes) {
    if (openType == OpenType.closed){
      System.out.println("Fail to read file! Already closed!");
      return;
    }
    if (openType == OpenType.output){
      System.out.println("Fail to read file! Can't do read operation in output mode!");
      return;
    }

    int bytesInInt = Integer.parseInt(bytes);

    StringBuilder sb = new StringBuilder();
    sb.append("Read ").append(bytesInInt).append(" bytes from file ")
        .append(currentOpeningFileInfo.getName()).append("\n");
    do {
      if (DATA_SIZE - currentOffset >= bytesInInt){
        int newPos = currentOffset + bytesInInt;
        sb.append(currentOpeningFile.read(currentOffset, newPos));
        currentOffset = newPos;
        bytesInInt = 0;
      } else {
        sb.append(currentOpeningFile.read(currentOffset, DATA_SIZE));
        int bytesRead = DATA_SIZE - currentOffset;
        bytesInInt -= bytesRead;
        if (currentOpeningFile.getForward() != null && currentOpeningFile.getForward().getNode() != null) {
          currentOpeningFile = (FileNode) currentOpeningFile.getForward().getNode();
          currentOffset = 0;
        } else {
          System.out.println(bytesInInt + " bytes left. No further file sectors for reading!");
          currentOffset = DATA_SIZE;
          break;
        }
      }
    } while (bytesInInt > 0);
    System.out.println(sb.toString());
  }

  private void write(String bytes, String data) throws DiskFullException{
    if (openType == OpenType.closed) {
      System.out.println("No file currently opened! You can't write data.");
      return;
    }
    if (openType == OpenType.input) {
      System.out.println("File is opened for open mode! You can't write data.");
      return;
    }

    if (data.length() < 3 || data.charAt(0) != '\'' || data.charAt(data.length() - 1) != '\'') {
      System.out.println("Invalid data format. Shall wrap with single quote in both end.");
      return;
    }

    int bytesInInt = Integer.parseInt(bytes);
    data = data.substring(1, data.length() - 1);//Get rid of single quote.
    if (data.length() < bytesInInt) {//If the data is fewer than the given bytes, fill with spaces
      data = String.format("%-" + bytesInInt + "s", data);
    }
    int i = 0;
    SectorInfo currentFileSector = currentOpeningFileInfo.getLink();
    do {
      if (DATA_SIZE -currentOffset >= bytesInInt){
        int length = Math.min(data.length(), bytesInInt);
        currentOpeningFile.writeData(currentOffset, data.substring(i, i + length));
        currentOffset += length;
        bytesInInt = 0;
        currentOpeningFileInfo.setSize((short)currentOffset);
      } else {
        int bytesToBeWritten = DATA_SIZE - currentOffset;
        currentOpeningFile.writeData(currentOffset, data.substring(i, i + bytesToBeWritten));
        SectorInfo newFileSector;
        if (currentOpeningFile.getForward() != null){
          newFileSector = currentOpeningFile.getForward();
        } else {
          newFileSector = sectors.getNextAvailableSector('u');
        }
        currentOpeningFile.setForward(newFileSector);
        newFileSector.getNode().setBack(currentFileSector);
        currentFileSector = newFileSector;
        currentOpeningFile = (FileNode) newFileSector.getNode();
        bytesInInt -= bytesToBeWritten;
        i += bytesToBeWritten;
        currentOffset = 0;
      }
    } while (bytesInInt > 0);
  }


  private void seek(String base, String offset){
    if (openType == OpenType.closed){
      System.out.println("File closed! Nothing to seek.");
      return;
    }
    switch(base){
      case "-1":
        reWind();
        break;
      case "0":
        int offsetInInt = Integer.parseInt(offset);
        moveCurrentOffset(offsetInInt);
        break;
      case "1":
        locAtEndOfFile();
        break;
      default:
        System.out.println("Invalid base set: " + base);
    }
  }
  
  private void reWind(){
    currentOpeningFile = (FileNode) currentOpeningFileInfo.getLink().getNode();
    currentOffset = 0;
  }
  
  private void locAtEndOfFile(){
    while (currentOpeningFile.getForward() != null && currentOpeningFile.getForward().getNode() != null) {
      currentOpeningFile = (FileNode) currentOpeningFile.getForward().getNode();
    }
    currentOffset = currentOpeningFileInfo.getSize();
  }
  
  private void moveCurrentOffset(int offset){
    int newOffset = currentOffset + offset;
    if (newOffset >=0 && newOffset < DATA_SIZE){
      currentOffset = newOffset;
    } else if (newOffset < 0){//Move backward several sector(s)
      SectorInfo currentInfo = currentOpeningFile.getBack();
      while (newOffset < -DATA_SIZE){
        if (currentInfo == null || currentInfo.getNode() == null || currentInfo.getNode().getBack() == null){
          System.out.println("Move more than the sector has! Move to the head of file.");
          newOffset = 1;
          break;
        }
        currentInfo = currentInfo.getNode().getBack();
        newOffset += DATA_SIZE;
      }
      currentOffset = newOffset > 0 ? 0 : DATA_SIZE + newOffset;
      currentOpeningFile = currentInfo == null ? currentOpeningFile : (FileNode) currentInfo.getNode();
    } else {//Move forward several sector(s)
      SectorInfo currentInfo = currentOpeningFileInfo.getLink();
      while (newOffset > DATA_SIZE){
        if (currentInfo.getNode() == null || currentInfo.getNode().getForward() == null){
          System.out.println("Move more than the sector has! Move to the end of file.");
          newOffset = -1;
        }
        currentInfo = currentInfo.getNode().getBack();
        newOffset -= DATA_SIZE;
      }
      currentOffset = newOffset < 0 ? currentOpeningFileInfo.getSize() : newOffset;
      currentOpeningFile = (FileNode) currentInfo.getNode();
    }
  }
}