package test;

import util.FileUtil;

import java.util.Scanner;

public class ManualTest {
  public static void main(String[] args) {
    FileUtil fileUtil = new FileUtil();
    Scanner scanner = new Scanner(System.in);
    while (true){
      System.out.println("Enter a command: ");
      String command = scanner.nextLine();
      if ("quit".equals(command)){
        break;
      }
      fileUtil.parseCommand(command);
    }
  }
}
