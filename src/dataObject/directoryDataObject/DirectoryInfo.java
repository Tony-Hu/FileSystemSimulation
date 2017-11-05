package dataObject.directoryDataObject;

import dataObject.AbstractNode;
import dataObject.SectorInfo;

import java.util.Arrays;

import static util.FileUtil.MAX_NAME_LENGTH;

public class DirectoryInfo {

  private char type;
  private char[] name;
  private SectorInfo link;
  private byte size;

  public DirectoryInfo(){
    type = 'f';
    name = new char[MAX_NAME_LENGTH];
  }

  public void setInfo(char type, String name, SectorInfo link){
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
    this.name = name;//TODO rewrite this part
  }

  public SectorInfo getLink() {
    return link;
  }

  public void setLink(SectorInfo link) {
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
    return i >= anotherName.length() && name[i] == '\u0000';
  }

  @Override
  public String toString() {
    return String.valueOf(type) + "\t\t" + String.valueOf(name) + "\t\t" +
        link + "\t\t" + size;
  }
}
