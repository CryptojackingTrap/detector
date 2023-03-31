package it.unitn.deprecated.control.search;

import it.unitn.view.CentralConsole;

import java.util.ArrayList;
import java.util.List;

public class LongestSplitter {
    /**
     * split first string into the largest strings that they are in the second string
     * usually the first string is small and the second one is large
     *
     * @param first
     * @param second
     */
    public static List<Split1> split(String first, String second) {
        List<Split1> accumulativeResult = new ArrayList<>();
        split(first, 0, second, 0, accumulativeResult);
        return accumulativeResult;
    }

    private static void split(String first, Integer firstAbsoluteIdx, String second,
                              Integer secondAbsoluteIdx, List<Split1> accumulativeResult) {
        Split1 mainSplit = LCS.lcs(first, second);
        if (mainSplit == null)
            return;
        mainSplit.setAbsoluteIdx(firstAbsoluteIdx, secondAbsoluteIdx);
        accumulativeResult.add(mainSplit);
        CentralConsole.log("Main Split: " + mainSplit);
        if (mainSplit.getFirstStartIdx() != 0) {
            String firstLeft = first.substring(0, mainSplit.getFirstStartIdx());
            String secondLeft = second.substring(0, mainSplit.getSecondStartIdx());
            split(firstLeft, firstAbsoluteIdx, secondLeft, secondAbsoluteIdx, accumulativeResult);
        }

        Integer firstSplitEndIdx = mainSplit.getFirstStartIdx() + mainSplit.getLongestSubString().length();
        Integer secondSplitEndIdx = mainSplit.getSecondStartIdx() + mainSplit.getLongestSubString().length();
        if (firstSplitEndIdx < first.length()) {
            String firstRight = first.substring(firstSplitEndIdx);
            String secondRight = second.substring(secondSplitEndIdx);
            split(firstRight, firstAbsoluteIdx + mainSplit.getLongestSubString().length(), secondRight,
                    secondAbsoluteIdx + mainSplit.getLongestSubString().length(),
                    accumulativeResult);
        }
    }
}
