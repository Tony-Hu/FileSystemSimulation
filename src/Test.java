import util.FileUtil;

import java.util.Arrays;

public class Test {

  public static void main(String[] args) {
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create d a");
    fileUtil.parseCommand("create d a/b");
    System.out.println(1);
  }
}
