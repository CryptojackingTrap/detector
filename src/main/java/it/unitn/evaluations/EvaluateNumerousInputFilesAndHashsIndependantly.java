package it.unitn.evaluations;

import com.google.common.base.Stopwatch;
import it.unitn.control.BlockReadLog;
import it.unitn.control.DetectorTextBase;
import it.unitn.control.MiningOccurrence;
import it.unitn.dto.FileSetting;
import it.unitn.util.NaturalOrderComparator;
import it.unitn.view.CentralConsole;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This evaluation expects to have X.txt for any X.out file in the specified path. X.out for ReadLog files and X.txt
 * specifies one hash to search.
 */
public class EvaluateNumerousInputFilesAndHashsIndependantly {
    private static final String PATH = "C:\\D\\University of Trento\\Our Future " +
            "Papers\\Cryptojackingtrap\\Experimental results od " +
            "Cryptojackingtrap\\data\\benign-non-miners\\";
//            "randomized benign miner\\";
//            "expanded of benign miner\\10";

    public static void main(String[] args) throws IOException {
        DetectorTextBase detectorTextBase = new DetectorTextBase();
        BlockReadLog blockReadLog = new BlockReadLog();
        blockReadLog.setCryptocurrencyName("Monero");
        FileSetting fileSetting = new FileSetting(PATH, ".*out", true);
        List<File> files = fileSetting.getFiles();
        NaturalOrderComparator naturalOrderComparator = new NaturalOrderComparator();
        Collections.sort(files, naturalOrderComparator);
        List<List<MiningOccurrence>> results = new ArrayList<>();
//        String hash = "edb1ff80a168cf72a7c89760d60f60ed931d19370fbe3836c80c618f5ccae0ad";
        for (File file : files) {
            String hash = getHash(file);
            Stopwatch timerAll = Stopwatch.createStarted();
            System.out.println(">>>>> file:" + file.getAbsolutePath());
            Stopwatch timer = Stopwatch.createStarted();
            List<MiningOccurrence> miningOccurrences = detectorTextBase.findAllMiningOccurrences(hash, file);
            timer.stop();
            CentralConsole.info("\ttime:\t" + timer + "\tminingOccurrences:\t" + miningOccurrences);
            results.add(miningOccurrences);
            timerAll.stop();

            CentralConsole.info("process time:\t" + timerAll);
        }
        System.out.println("----------------------------------------");
        CentralConsole.printInfo();
    }

    private static String getHash(File readLogFile) throws IOException {
        String txtFilePath = readLogFile.getAbsolutePath().substring(0, readLogFile.getAbsolutePath().length() - 3) +
                "txt";
        BufferedReader reader = new BufferedReader(new FileReader(txtFilePath));
        String hash = reader.readLine();
        return hash;
    }
}
