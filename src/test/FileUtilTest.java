package test;

import org.junit.jupiter.api.Test;
import util.FileUtil;


class FileUtilTest {

  @Test
  void testSimpleCreate(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create d a");
    fileUtil.parseCommand("create d a/b");
    fileUtil.parseCommand("create u a/a");
  }


  @Test
  void testMaxCreate() {
    FileUtil fileUtil = new FileUtil();
    for (int i = 0; i < 26; i++){
      fileUtil.parseCommand("create d " + (char)('a' + i));
    }
    for (int i = 0; i < 26; i++){
      fileUtil.parseCommand("create d a" + (char)('a' + i));
    }
    for (int i = 0; i < 26; i++){
      fileUtil.parseCommand("create d aa" + (char)('a' + i));
    }
    for (int i = 0; i < 26; i++){
      fileUtil.parseCommand("create d aaa" + (char)('a' + i));
    }
  }


  @Test
  void testSimpleDelete(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create d a/b");
    fileUtil.parseCommand("create u a/a");
    fileUtil.parseCommand("delete a/a");
  }
}