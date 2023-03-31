package it.unitn.view;

import java.io.File;

public class DetectorExecutor {

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            File file = new File(args[0]);
            if (file.isDirectory()) {
                openSearch(file);
            } else {
                openSearch(file.getParentFile());
            }
        } else {
            openSearch(new File("c:\\"));
        }
    }

    private static void openSearch(File searchDir) {
        DetectorGUI gui = DetectorGUI.getInstance();
        //gui.setSearchDir(searchDir);
        gui.show();
    }
}
