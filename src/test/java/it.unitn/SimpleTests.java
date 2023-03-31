package it.unitn;

public class SimpleTests {
    public static void main(String[] args) {
        String memoryReadLine = "aa     bb    ";
        String[] parts = memoryReadLine.split("\\s++");
        for (String s : parts) {
            System.out.println(s);
        }
        System.out.println("End");
    }
}
