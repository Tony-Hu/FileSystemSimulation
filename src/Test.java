
public class Test {

  public static void main(String[] args) {
    String a = "a   b  c";
    for (String b : a.split("\\s+")){
      System.out.println(b);
    }
  }
}
