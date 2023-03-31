package it.unitn;

import it.unitn.dto.FileSetting;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public class TestSubOperations {
    public static void main(String[] args) {
        String fileName = "C:\\D\\Workspace\\Cryptojackingtrap\\cryptojackingtrap-detector\\src" +
                "\\main\\resources\\sample-data\\latestBlockHash-Bitcoin.txt";

        String patternStr = ".*\\.txt";
        Pattern pattern = Pattern.compile(patternStr);
       /*Boolean result =  pattern.matcher("latestBlockHash-Bitcoin.txt").find();
       result = fileName.matches(patternStr);
        System.out.println(result);*/

        fileName = "C:\\D\\Workspace\\Cryptojackingtrap\\cryptojackingtrap-detector\\src" +
                "\\main\\resources\\sample-data\\";


        File file = new File(fileName);
        FileSetting fileSetting = new FileSetting(
                fileName,
                patternStr,
                true);
        List<File> files = fileSetting.getFiles();
        if (files == null) {
            System.out.println("Null");
        } else {
            for (File file1 : files) {
                System.out.println("file: " + file1.getName());
            }
        }
        /*File file = new File("C:\\D\\Workspace\\Cryptojackingtrap\\cryptojackingtrap-detector\\src\\main\\resources\\sample-data\\latestBlockHash-Bitcoin.txt");
        if(file.listFiles() == null){
            System.out.println("ppp");
        }
        else {
            for (File file1 : file.listFiles()) {
                System.out.println("file: " + file1);
            }
        }*/
    }
}