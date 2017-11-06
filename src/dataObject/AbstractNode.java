package dataObject;

public class AbstractNode {
  private SectorInfo back;
  private SectorInfo forward;

  public SectorInfo getBack() {
    return back;
  }

  public void setBack(SectorInfo back) {
    this.back = back;
  }

  public SectorInfo getForward() {
    return forward;
  }

  public void setForward(SectorInfo forward) {
    this.forward = forward;
  }

  public String display(){
    StringBuilder sb = new StringBuilder();
    sb.append("Forward: ").append(forward).append("\n");
    sb.append("Backward: ").append(back).append("\n");
    return sb.toString();
  }
}
