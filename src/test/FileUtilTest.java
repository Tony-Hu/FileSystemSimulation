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
    fileUtil.parseCommand("write 3 \'cdc\'");
    fileUtil.parseCommand("write 4 \'aba\'");
  }

  @Test
  void testMaxWrite(){
    FileUtil fileUtil = new FileUtil();
    for (int i = 0; i < 23; i++){//Create 92 file. Total 94 sectors are used.
      char a = (char)('a' + i);
      for (int j = 0; j < 4; j++){
        char b = (char)('a' + j);
        fileUtil.parseCommand("create u " + a + b);
      }
    }
    fileUtil.parseCommand("write 9999 " + generateSequenceFileData(9999));
    fileUtil.displaySectors("");
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

  @Test
  void testDeleteFileWithMultipleSectors(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create u a");
    fileUtil.parseCommand("create d b");
    fileUtil.parseCommand("write 1024 " + generateSequenceFileData(1024));
    fileUtil.parseCommand("delete a");
  }

  @Test
  void testSimpleReadFile(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create u a");
    fileUtil.parseCommand("create d b");
    fileUtil.parseCommand("write 1024 " + generateSequenceFileData(1024));
    fileUtil.parseCommand("open i a");
    fileUtil.parseCommand("read 1024");
  }

  @Test
  void testOverReadFile(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create u a");
    fileUtil.parseCommand("create d b");
    fileUtil.parseCommand("write 3 " + generateSequenceFileData(3));
    fileUtil.parseCommand("open i a");
    fileUtil.parseCommand("read 6");
  }


  @Test
  void testSimpleSeekReWind(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create u a");
    fileUtil.parseCommand("write 3 " + generateSequenceFileData(3));
    fileUtil.parseCommand("seek -1 0");
    fileUtil.parseCommand("write 4 " + generateRandomFileData(4));
  }

  @Test
  void testSeekReWindOverSectors(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create u a");
    fileUtil.parseCommand("write 1024 " + generateSequenceFileData(1024));
    fileUtil.parseCommand("seek -1 0");
    fileUtil.parseCommand("write 3 " + generateRandomFileData(3));
  }

  @Test
  void testSimpleMoveToEOF(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create u a");
    fileUtil.parseCommand("write 3 " + generateSequenceFileData(3));
    fileUtil.parseCommand("seek -1 0");
    fileUtil.parseCommand("seek 1 0");
    fileUtil.parseCommand("write 3 " + generateSequenceFileData(3));
  }

  @Test
  void testMoveToEOFOverSectors(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create u a");
    fileUtil.parseCommand("write 1008 " + generateSequenceFileData(1008));
    fileUtil.parseCommand("seek -1 0");
    fileUtil.parseCommand("seek 1 0");
    fileUtil.parseCommand("write 3 " + generateRandomFileData(3));
  }


  @Test
  void testSimpleMoveToOffset(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create u a");
    fileUtil.parseCommand("write 3 " + generateSequenceFileData(3));
    fileUtil.parseCommand("seek 0 -1");
    fileUtil.parseCommand("write 3 " + generateSequenceFileData(3));
    fileUtil.parseCommand("open u a");
    fileUtil.parseCommand("seek 0 3");
    fileUtil.parseCommand("write 3 " + generateRandomFileData(3));
  }


  @Test
  void testMoveNegativeOffsetOverSectors(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create u a");
    fileUtil.parseCommand("write 1024 " + generateSequenceFileData(1024));
    fileUtil.parseCommand("seek 0 -17");
    fileUtil.parseCommand("write 3 " + generateRandomFileData(3));
  }

  @Test
  void testMovePositiveOffsetOverSectors(){
    FileUtil fileUtil = new FileUtil();
    fileUtil.parseCommand("create u a");
    fileUtil.parseCommand("write 1024 " + generateSequenceFileData(1024));
    fileUtil.parseCommand("open u a");
    fileUtil.parseCommand("seek 0 503");
    fileUtil.parseCommand("write 3 " + generateRandomFileData(3));
  }
}