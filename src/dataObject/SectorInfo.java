package dataObject;

import dataObject.directoryDataObject.DirectoryNode;

public class SectorInfo {
  private boolean isFree;
  private AbstractNode node;

  public SectorInfo(AbstractNode node){
    isFree = false;
    this.node = node;
  }

  public boolean isFree() {
    return isFree;
  }

  public void setFree(boolean free) {
    isFree = free;
  }

  public AbstractNode getNode() {
    return node;
  }

  public void setNode(AbstractNode node) {
    this.node = node;
  }
}
