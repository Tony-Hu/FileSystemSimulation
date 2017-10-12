import dataObject.FreeBlockInfo;
import dataObject.directoryDataObject.DirectoryNode;

import java.util.ArrayList;
import java.util.List;

public class FileUtil {

  private DirectoryNode[] directoryNodes;
  private List<FreeBlockInfo> freeBlockList;

  public static final int SECTOR_SIZE = 100;


  public FileUtil(String[] args){
    directoryNodes = new DirectoryNode[SECTOR_SIZE];
    freeBlockList = new ArrayList<>(SECTOR_SIZE);
  }


}
