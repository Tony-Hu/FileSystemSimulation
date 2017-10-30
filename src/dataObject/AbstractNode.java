package dataObject;

public class AbstractNode {
  private AbstractNode back;
  private AbstractNode forward;

  public AbstractNode getBack() {
    return back;
  }

  public void setBack(AbstractNode back) {
    this.back = back;
  }

  public AbstractNode getForward() {
    return forward;
  }

  public void setForward(AbstractNode forward) {
    this.forward = forward;
  }
}
