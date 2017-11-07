package util;

import dataObject.AbstractNode;
import dataObject.SectorInfo;
import dataObject.directoryDataObject.DirectoryNode;
import dataObject.fileDataObject.FileNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.FileUtil.SECTOR_SIZE;

public class SectorsUtil {

  private SectorInfo[] sectors;
  private int nextFreePos;

  public SectorsUtil(){
    sectors = new SectorInfo[SECTOR_SIZE];
    sectors[0] = new SectorInfo(new DirectoryNode());//Sector 0 always being a dir.
    for (int i = 1; i < SECTOR_SIZE; i++){
      sectors[i] = new SectorInfo();
    }
    nextFreePos = 1;
  }


  public SectorInfo getNextAvailableSector(char type) throws DiskFullException {
    SectorInfo newInfo = sectors[findNextAvailableSector()];
    newInfo.setFree(false);
    AbstractNode newNode;
    if (type == 'd') {
      newNode = new DirectoryNode();
    } else {
      newNode = new FileNode();
    }
    newInfo.setNode(newNode);
    ((DirectoryNode)sectors[0].getNode()).setNextFreeNode(newInfo);
    return ((DirectoryNode)sectors[0].getNode()).getNextFreeNode();
  }

  private int findNextAvailableSector() throws DiskFullException {
    int counter = 0;
    for (; counter < SECTOR_SIZE && !sectors[nextFreePos].isFree(); counter++){
      nextFreePos++;
      nextFreePos %= SECTOR_SIZE;
    }
    if (counter >= SECTOR_SIZE){//If there is no space available
      System.out.println("The entire disk sectors are full!");
      ((DirectoryNode)sectors[0].getNode()).setNextFreeNode(null);
      throw new DiskFullException();
    }

    return nextFreePos;
//
  }
  public SectorInfo getSectorZero(){
    return sectors[0];
  }



  public String display() {
    List<AbstractNode> results = new ArrayList<>();
    Map<String, AbstractNode> map = new HashMap<>();
    StringBuilder sb = new StringBuilder();
    sb.append("#").append("\t").append("Address").append("\t\t\t\t\t").append("isFree").append("\t")
        .append("Location").append("\n");
    for (int i = 0; i < SECTOR_SIZE; i++){
      if (!sectors[i].isFree()) {
        sb.append(i).append("\t");
        sb.append(sectors[i]).append("\t\t");
        sb.append(sectors[i].display());
        sb.append("\n");
        results.add(sectors[i].getNode());
      }
    }
    sb.append("\n\n\n");
    for (AbstractNode node : results){
      if (!map.containsKey(node.toString())) {
        do {
          sb.append(node).append("\n");
          sb.append(node.display());
          sb.append("\n\n");
          map.put(node.toString(),node);
          if (node.getForward() == null){
            break;
          }
          node = node.getForward().getNode();
        } while (node != null);
      }
    }

    sb.append("--------------------------------------------");
    return sb.toString();
  }
}
