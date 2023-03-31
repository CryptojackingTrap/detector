package it.unitn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinersInVirusShare {

    public static void main(String[] args) {
        String md5Path = "C:\\Users\\LENOVO\\Desktop\\Final2\\md5";
        String minersHashes = "C:\\Users\\LENOVO\\Desktop\\Final2\\bitcoin_malware_hashes.txt";
        List<String> l = new ArrayList<String>();

        Map<String, Integer> map = new HashMap<String, Integer>();//hashcontent to line number

        Map<String, Integer> count = new HashMap<String, Integer>();//fileName to hit count


        // ________________________________________________________________________________

        /*
         * read each minersHashes line
         */
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(
                    minersHashes)));
            String targetLine = null;
            int lineNumber = 0;
            while ((targetLine = reader.readLine()) != null) {
                lineNumber++;
                map.put(targetLine, lineNumber);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }

        }
        // ________________________________________________________________________________


        // ********************************************



        /*
         * read each md5 file
         */
        File md5File = new File(md5Path);
        for (File fileToSearch : md5File.listFiles()) {
            if (!fileToSearch.isDirectory()) {
                /*
                 * read each fileToSearch line
                 */
                BufferedReader reader2 = null;
                try {
                    reader2 = new BufferedReader(new FileReader(
                            fileToSearch));
                    String targetLine2 = null;
                    int lineNumber2 = 0;
                    while ((targetLine2 = reader2.readLine()) != null) {
                        lineNumber2++;
                        if (map.containsKey(targetLine2)) {
                            System.out
                                    .println("lineNumber,targetLine,fileToSearch, lineNumber2, targetLine2 = "
                                            + "?" + ", "
                                            + targetLine2 + ", "
                                            + fileToSearch.getName() + ", "
                                            + lineNumber2 + ", " + targetLine2);

                            if (count.containsKey(fileToSearch.getName())) {
                                count.put(fileToSearch.getName(), count.get(fileToSearch.getName()) + 1);
                            } else {
                                count.put(fileToSearch.getName(), 1);
                            }
                        }

                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } finally {
                    try {
                        reader2.close();
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                    }
                }

                // ********************************************


            }
        }

        System.out.println(count.toString());

    }
}
