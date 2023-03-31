package it.unitn.deprecated.control;

import it.unitn.control.FileParser;
import it.unitn.deprecated.dto.DetectionResult;
import it.unitn.deprecated.dto.xmf.MemoryReadLog;
import it.unitn.deprecated.dto.xmf.MemoryReadLogs;
import it.unitn.dto.DetectorSetting;
import it.unitn.dto.imf.Block;
import it.unitn.dto.xmf.BlockLog;
import it.unitn.dto.xmf.BlockLogs;
import it.unitn.view.CentralConsole;

import java.io.IOException;
import java.text.ParseException;

public class Detector {
    public DetectionResult detect(DetectorSetting detectorSettings) throws IOException, ParseException {
        FileParser fileParser = new FileParser();
        BlockLogs blockLogs = fileParser.getBlockLogs(detectorSettings.getListenerSettings());
        MemoryReadLogs memoryReadLogs = fileParser.getMemoryReadLogs(detectorSettings.getMonitorSetting());
        DetectionResult detectionResult = detect(blockLogs, memoryReadLogs);
        return detectionResult;
    }

    private DetectionResult detect(BlockLogs blockLogs, MemoryReadLogs memoryReadLogs) {
        DetectionResult detectionResult = new DetectionResult();
        detectionResult.setDetected(false);
        Boolean hashDetection = false;
        for (BlockLog blockLog : blockLogs.getBlockLogList()) {
            Boolean goToNextCrypto = false;
            String cryptoName = blockLog.getCryptocurrencyName();
            for (Block block : blockLog.getBlockList()) {
                for (MemoryReadLog memoryReadLog : memoryReadLogs.getMemoryReadLogList()) {
                    if (memoryReadLog.getReadContent().equals(block.getHeaderHash())) {
                        if (hashDetection == false) {
                            hashDetection = true;
                            CentralConsole.log("hash detection!");
                        } else {
                            CentralConsole.log("mining detection!");
                            detectionResult.setDetected(true);
                            detectionResult.addDetectedCryptocurrencyName(cryptoName);
                            goToNextCrypto = true;
                        }
                    }
                    if (goToNextCrypto) {
                        break;
                    }
                }
                if (goToNextCrypto) {
                    break;
                }
            }
        }
        return detectionResult;
    }
}
