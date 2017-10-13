package dataObject.directoryDataObject;

import dataObject.AbstractNode;

import static util.FileUtil.MAX_NAME_LENGTH;

public class DirectoryInfo {

  private char type;
  private char[] name;
  private AbstractNode link;
  private byte size;

  public DirectoryInfo(){
    type = 'f';
    name = new char[MAX_NAME_LENGTH];
  }

  public void setInfo(char type, String name, AbstractNode link){
    this.type = type;
    this.name = new char[MAX_NAME_LENGTH];
    System.arraycopy(name.toCharArray(), 0, this.name, 0, Math.min(name.length(), MAX_NAME_LENGTH));
    this.link = link;
  }

  public char getType() {
    return type;
  }

  public void setType(char type) {
    this.type = type;
  }

  public char[] getName() {
    return name;
  }

  public void setName(char[] name) {
    this.name = name;
  }

  public AbstractNode getLink() {
    return link;
  }

  public void setLink(AbstractNode link) {
    this.link = link;
  }

  public byte getSize() {
    return size;
  }

  public void setSize(byte size) {
    this.size = size;
  }

  public boolean haveSameName(String anotherName){
    int i = 0;
    for (; i < anotherName.length() && name[i] != '\u0000';i++){
      if (anotherName.charAt(i) != name[i]){
        return false;
      }
    }
    if (i < anotherName.length() || name[i] != '\u0000'){
      return false;
    }


    return true;
  }
}
