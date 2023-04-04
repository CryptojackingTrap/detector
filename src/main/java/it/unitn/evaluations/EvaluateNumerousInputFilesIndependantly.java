package it.unitn.evaluations;

import com.google.common.base.Stopwatch;
import it.unitn.control.BlockReadLog;
import it.unitn.control.DetectorTextBase;
import it.unitn.control.MiningOccurrence;
import it.unitn.dto.FileSetting;
import it.unitn.util.NaturalOrderComparator;
import it.unitn.view.CentralConsole;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EvaluateNumerousInputFilesIndependantly {
    private static final String PATH = "C:\\D\\University of Trento\\Our Future " +
            "Papers\\Cryptojackingtrap\\Experimental results od " +
            "Cryptojackingtrap\\data\\generated-dataset\\randomized benign miner\\";

    public static void main(String[] args) {
        DetectorTextBase detectorTextBase = new DetectorTextBase();
        BlockReadLog blockReadLog = new BlockReadLog();
        blockReadLog.setCryptocurrencyName("Monero");
        FileSetting fileSetting = new FileSetting(PATH, ".*out", true);
        List<File> files = fileSetting.getFiles();
        NaturalOrderComparator naturalOrderComparator = new NaturalOrderComparator();
        Collections.sort(files, naturalOrderComparator);
        List<List<MiningOccurrence>> results = new ArrayList<>();
        String hash = "edb1ff80a168cf72a7c89760d60f60ed931d19370fbe3836c80c618f5ccae0ad";
        for (File file : files) {
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
}
