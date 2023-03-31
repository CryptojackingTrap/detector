package it.unitn.evaluate;

import it.unitn.control.DetectorTextBase;
import it.unitn.control.Split;

import java.io.File;
import java.util.List;

public class SearchTextInFileTest {
    private final static String BASE_PATH = "src/test/resources/sample-data/monero-real-data/";

    public static void main(String[] args) {
        File file = new File(BASE_PATH + "monitorLog-MinerGate-Monero.out");

        String hash =
                "edb1ff80a168cf72a7" +
                        "c89760d60" +
                        "f60ed931d1937" +
                        "0" +
                        "fbe3836c8" +
                        "0c618f5ccae0ad";
        DetectorTextBase searchText = new DetectorTextBase();
//        List<Split2> list = searchText.splitHash(hash, file);
        List<Split> occurrenceList = searchText.findAllSplitOccurrences(hash, file);
//        List<Split2> occurrenceList = searchText.findAllHashOccurrences(hash, file);
        System.out.println(occurrenceList);
    }
}
