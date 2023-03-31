package it.unitn.evaluate;

import it.unitn.control.DetectorTextBase;
import it.unitn.control.Split;

public class ClsTest {
    public static void main(String[] args) {
        Split split2 = DetectorTextBase.lcs("aabcd", "0123456789abcd0123456789");
        System.out.println(split2);
    }
}
