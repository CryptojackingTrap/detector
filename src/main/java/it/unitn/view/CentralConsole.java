package it.unitn.view;

import com.google.common.base.Stopwatch;

import java.sql.Timestamp;

/**
 * singleton class to log and use on UI
 */
public class CentralConsole {

    private CentralConsole() {

    }

    public static void log(String message, Stopwatch timer) {
        String level = "debug";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String outMsg = timestamp + ", " + level + ", duration: " + timer + ":" + message;
        DetectorGUI.getInstance().appendStatus(outMsg);
        System.out.println(outMsg);
    }

    public static void log(String message) {
        String level = "debug";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String outMsg = timestamp + ", " + level + ": " + message;
        DetectorGUI.getInstance().appendStatus(outMsg);
        System.out.println(outMsg);
    }

    /**
     * For the purpose of gathering statistics for CryptojackingTrap paper
     *
     * @param message
     */
    private static String infoMsg = "";

    public static void info(String message) {
        System.out.println(">>>>" + message);
        infoMsg += "\n" + message;
    }

    public static void printInfo() {
        System.out.println("---------------------------------------------");
        System.out.println(infoMsg);
        System.out.println("---------------------------------------------");
    }
}
