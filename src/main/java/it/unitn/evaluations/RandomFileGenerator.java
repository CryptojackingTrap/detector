package it.unitn.evaluations;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class RandomFileGenerator {
    private static final String PATH = "C:\\D\\Workspace\\Cryptojackingtrap-Main-Workspace\\cryptojackingtrap-detector\\src\\test\\resources\\sample-data\\random-generated-data\\";
    private static String fileName = "random-monitor-data-2.out";
    private static Integer FILE_LINE_NUMBER = 2000000;
    //todo fixed time, because it is not processed in detector
    private static String FILE_TIME_STAMP = "2022/05/18 16:57:05";
    private static Integer MIN_HEX_CHAR_COUNT = 1;
    private static Integer MAX_HEX_CHAR_COUNT = 31;


    public static void main(String[] args) throws Exception {
        Files.createFile(Paths.get(PATH + fileName));
        for (int i = 0; i < FILE_LINE_NUMBER; i++) {
            Integer randomSize = RandomUtil.getRandomSize(MIN_HEX_CHAR_COUNT, MAX_HEX_CHAR_COUNT);
            String randomHexStr = RandomUtil.getRandomHexString(randomSize);
            String messageToWrite = FILE_TIME_STAMP + " 0x" + randomHexStr + "\n";

            Files.write(
                    Paths.get(PATH + fileName),
                    messageToWrite.getBytes(),
                    StandardOpenOption.APPEND);
        }
    }
}
