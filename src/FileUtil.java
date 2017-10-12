import dataObject.FreeBlockInfo;
import dataObject.directoryDataObject.DirectoryNode;


public class FileUtil {

  private DirectoryNode[] directoryNodes;
  private FreeBlockInfo[] freeBlockList;

  public static final int SECTOR_SIZE = 100;


  public FileUtil(){
    directoryNodes = new DirectoryNode[SECTOR_SIZE];
    directoryNodes[0] = new DirectoryNode();
    freeBlockList = new FreeBlockInfo[SECTOR_SIZE];
  }

  public void parseCommand(String command){
    String lowerCaseCommand = command.toLowerCase();
    String[] splits = lowerCaseCommand.split("\\s+");
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
    if ("u".equals(type)){
      
    } else if ("d".equals(type)){

    } else {
      System.out.println("Type \"" + type + "\" is invalid. Only \"u\" or \"d\" is allowed");
    }
  }
}
