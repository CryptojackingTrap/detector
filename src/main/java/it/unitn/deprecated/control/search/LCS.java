package it.unitn.deprecated.control.search;

/**
 * https://gist.github.com/weidagang/8182284
 */
public class LCS {
    public static Split1 lcs(String first, String second) {
        Split1 lcsResult = null;
        if (null == first || null == second) return null;
        String longest = "";
        for (int firstIdx = 0; firstIdx < first.length(); ++firstIdx) {
            for (int secondIdx = 0; secondIdx < second.length(); ++secondIdx) {
                int subLength;
                for (subLength = 0;
                     firstIdx + subLength < first.length() && secondIdx + subLength < second.length();
                     ++subLength) {
                    if (first.charAt(firstIdx + subLength) != second.charAt(secondIdx + subLength)) {
                        break;
                    }
                }
                if (subLength > longest.length()) {
                    if (lcsResult == null) {
                        lcsResult = new Split1();
                    }
                    longest = first.substring(firstIdx, firstIdx + subLength);
                    lcsResult.setFirstStartIdx(firstIdx);
                    lcsResult.setSecondStartIdx(secondIdx);
                    lcsResult.setLongestSubString(longest);
                }
            }
        }
        return lcsResult;
    }
}