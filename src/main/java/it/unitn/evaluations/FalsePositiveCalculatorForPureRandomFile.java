package it.unitn.evaluations;

import com.google.common.base.Stopwatch;
import it.unitn.control.BlockReadLog;
import it.unitn.control.DetectorTextBase;
import it.unitn.control.MiningOccurrence;
import it.unitn.view.CentralConsole;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * In this class we calculate the false positive of the approach using random data
 * <p>
 * We use the file created by:
 *
 * @see RandomFileGenerator in Cryptojackingtrap-dataset repository to create random data for monitor read memory
 * access data
 */
public class FalsePositiveCalculatorForPureRandomFile {
    private static final String PATH = "C:\\D\\Workspace\\Cryptojackingtrap-Main-Workspace\\cryptojackingtrap-detector\\src\\test\\resources\\sample-data\\random-generated-data\\";
    private static String fileName = "random-monitor-data.out";
    private static Integer EXPERIMENT_COUNT = 100;

    public static void main(String[] args) {
        DetectorTextBase detectorTextBase = new DetectorTextBase();
        BlockReadLog blockReadLog = new BlockReadLog();
        blockReadLog.setCryptocurrencyName("Random");
        File file = new File(PATH + fileName);

        Stopwatch timerAll = Stopwatch.createStarted();
        List<List<MiningOccurrence>> results = new ArrayList<>();
        for (int i = 0; i < EXPERIMENT_COUNT; i++) {
            System.out.println(">>>>> round:" + i);
            String randomHash = RandomUtil.getRandomHexString(64);
            Stopwatch timer = Stopwatch.createStarted();
            List<MiningOccurrence> miningOccurrences = detectorTextBase.findAllMiningOccurrences(randomHash, file);
            timer.stop();
            CentralConsole.info("\t hash:\t" + randomHash + "\ttime:\t" + timer + "\tminingOccurrences:\t" + miningOccurrences);
            results.add(miningOccurrences);
        }
        timerAll.stop();
        CentralConsole.info("total time:\t" + timerAll);
        CentralConsole.printInfo();
    }

}
