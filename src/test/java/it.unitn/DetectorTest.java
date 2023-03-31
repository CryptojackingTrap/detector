package it.unitn;

import it.unitn.deprecated.control.Detector;
import it.unitn.deprecated.dto.DetectionResult;
import it.unitn.dto.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DetectorTest {
    private static String BASE = "C:\\D\\Workspace\\Cryptojackingtrap\\cryptojackingtrap-detector\\src\\test" +
            "\\resources\\sample-data";

    public static void main(String[] args) throws IOException, ParseException {
        Detector detector = new Detector();
        ListenerSetting bitcoinListenerSetting = new ListenerSetting("Bitcoin",
                new FileSetting(BASE + "\\latestBlockHash-Bitcoin.txt", null, null));

        ListenerSetting ethereumListenerSetting = new ListenerSetting("Ethereum",
                new FileSetting(BASE + "\\latestBlockHash-Ethereum.txt", null, null));

        ListenerSetting moneroListenerSetting = new ListenerSetting("Monero",
                new FileSetting(BASE + "\\monero-real-data\\latestBlockHash-Monero.txt", null, null));

        List<ListenerSetting> listenerSettingList = new ArrayList<ListenerSetting>();
//        listenerSettingList.add(bitcoinListenerSetting);
//        listenerSettingList.add(ethereumListenerSetting);
        listenerSettingList.add(moneroListenerSetting);
        ListenerSettings listenerSettings = new ListenerSettings(listenerSettingList);
        MonitorSetting monitorSetting = new MonitorSetting(new FileSetting(
                BASE + "\\monero-real-data\\monitorLog-MinerGate-Monero.out", null, null));

        DetectorSetting detectorSetting = new DetectorSetting(listenerSettings, monitorSetting, true);
        DetectionResult detectionResult = detector.detect(detectorSetting);
        System.out.println("Detection result: " + detectionResult);
    }
}
