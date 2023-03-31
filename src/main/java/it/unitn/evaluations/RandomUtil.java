package it.unitn.evaluations;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * redundant with dataset project
 */
public class RandomUtil {
    private static Random r = new Random();

    public static String getRandomHexStringFaster(int numchars) {
        String hex = IntStream.range(0, numchars).mapToObj(i -> "F").collect(Collectors.joining(""));
        Long i = r.nextLong(Long.parseLong(hex, 16));
        return Long.toHexString(i);
    }

    public static String getRandomHexString(int numchars) {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, numchars);
    }

    public static Integer getRandomSize(Integer min, Integer max) {
        Random random = new Random();
        int randomNumber = random.nextInt(max + 1 - min) + min;
        return randomNumber;
    }
}
