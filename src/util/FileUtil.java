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
  private short currentPos;

  public static final int SECTOR_SIZE = 100;
  public static final int MAX_INFO_SIZE = 31;
  public static final int MAX_NAME_LENGTH = 9;
  public static final int DATA_SIZE = 504;

  public FileUtil() {
    sectors = new SectorsUtil();
    openType = OpenType.closed;
    currentPos = 0;
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
          create(splits[1], splits[2]);
          displaySectors(command);
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
          write(splits[1], splits[2]);
          displaySectors(command);
        }
        break;
      case "seek":

        break;
      default:
        System.out.println(("Invalid argument: " + splits[0]));
        break;
    }

  }

  private void create(String type, String name) {
    if ("u".equals(type)) {
      createFile(name);
    } else if ("d".equals(type)) {
      createDir(name);
    } else {
      System.out.println("Invalid type: " + type);
    }
  }

  private void createDir(String name) {
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
    }
  }

  private void createFile(String name) {
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

  private DirectoryInfo createNewInfo(char type, String name, SectorInfo currentDir) {
    if (!(currentDir.getNode() instanceof DirectoryNode)) {//Can't happen. just in case.
      System.out.println("Current node is not a directory node!");
      return null;
    }

    try {
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
    } catch (DiskFullException e){
      System.out.println("Disk full!");
      return null;
    }
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
    currentOpeningFile = (FileNode) info.getLink().getNode();
    placeFilePointer(info.getSize());
  }

  private void placeFilePointer(byte size) {
    switch (openType) {
      case input:
      case update:
        currentPos = 0;
        break;
      case output:
        while (currentOpeningFile.getForward() != null && currentOpeningFile.getForward().getNode() != null) {
          currentOpeningFile = (FileNode) currentOpeningFile.getForward().getNode();
        }
        currentPos = size;
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
    String fileName = info.getName().toString();
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

  }

  private void write(String bytes, String data) {
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
      if (DATA_SIZE -currentPos >= bytesInInt){
        int length = Math.min(data.length(), bytesInInt);
        currentOpeningFile.writeData(currentPos, data.substring(i, i + length));
        currentPos += length;
        bytesInInt = 0;
        currentOpeningFileInfo.setSize((byte)currentPos);
      } else {
        int bytesToBeWritten = DATA_SIZE - currentPos;
        currentOpeningFile.writeData(currentPos, data.substring(i, i + bytesToBeWritten));
        try {
          SectorInfo newFileSector = sectors.getNextAvailableSector('u');
          currentOpeningFile.setForward(newFileSector);
          newFileSector.getNode().setBack(currentFileSector);
          currentFileSector = newFileSector;
          currentOpeningFile = (FileNode) newFileSector.getNode();
          bytesInInt -= bytesToBeWritten;
          i += bytesToBeWritten;
        } catch(DiskFullException e){
          System.out.println("Disk is full!");
          return;
        } finally {
          currentPos = 0;
        }
      }
    } while (bytesInInt > 0);
  }
}