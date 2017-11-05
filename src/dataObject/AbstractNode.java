package dataObject;

public abstract class AbstractNode {
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

  public abstract String display();
}
