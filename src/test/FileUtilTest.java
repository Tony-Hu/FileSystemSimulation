package test;

import org.junit.jupiter.api.Test;
import util.FileUtil;

import java.util.Random;


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

  @Test
  void testSimpleWrite(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create u a");
    fileUtil.parseCommand("write 512 \'cdc\'");
  }

  @Test
  void testMaxWrite(){
    FileUtil fileUtil = new FileUtil();
    for (int i = 0; i < 24; i++){//Create 96 file. Total 97 sectors are used.
      char a = (char)('a' + i);
      for (int j = 0; j < 4; j++){
        char b = (char)('a' + j);
        fileUtil.parseCommand("create u " + a + b);
      }
    }
    fileUtil.parseCommand("write 2048 " + generateRandomFileData(2048));
  }

  String generateRandomFileData(int length){
    StringBuilder sb = new StringBuilder();
    sb.append('\'');
    for (int i = 0; i < length; i++){
      Random random = new Random();
      int rand = random.nextInt(26);
      sb.append((char)('a' + rand));
    }
    sb.append('\'');
    return sb.toString();
  }

  String generateSequenceFileData(int length){
    StringBuilder sb = new StringBuilder();
    sb.append('\'');
    for (int i = 0; i < length; i++){
      sb.append(i % 10);
    }
    sb.append('\'');
    return sb.toString();
  }
  @Test
  void testLongWrite(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create u a");
    fileUtil.parseCommand("write 1024 " + generateSequenceFileData(1024));
  }
}