package dataObject;


public class SectorInfo {
  private boolean isFree;
  private AbstractNode node;

  public SectorInfo(AbstractNode node){
    isFree = false;
    this.node = node;
  }

  public SectorInfo(){
    isFree = true;
    node = null;
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


  public String display() {
    return String.valueOf(isFree) + "\t" + node;
  }

  @Override
  public String toString() {
    return super.toString().split("\\.")[1];
  }
}
