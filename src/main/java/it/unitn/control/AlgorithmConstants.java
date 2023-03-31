package it.unitn.control;

public class AlgorithmConstants {
    /**
     * ignore hash substrings that are shorter than this size in further process and mark them as
     * {@link SplitErrorType#TOO_SMALL}
     */
    public static Integer MIN_SIZE_OF_HASH_SPLIT = 3;
    /**
     * in hash occurrence process, if two consecutive hash splits occur in different lines that are further than
     * the following threshold window, then mark that split as {@link SplitErrorType#TOO_LATE}
     */
    public static Integer MAX_SPLIT_LINE_WINDOW = 20;

    /**
     * the minimum percentage of hash coverage in the file. in calculating hash coverage percentage, we just count
     * the size of hash splits that are not missed in the file, not too small and do not occur too late (after
     * {@link AlgorithmConstants#MAX_SPLIT_LINE_WINDOW} lines).
     */
    public static Integer MIN_HASH_COVERAGE = 80;

    /**
     * in mining occurrence process, if two consecutive hash occurrences are not further than
     * the following threshold window, then we count that hashOccurrence in miningOccurrence algorithm. We measure
     * the difference line number between last split of one hash occurrence and the line number of the first split of
     * the next hash occurrence and if this value is not greater than the following value we include them as
     * acceptable hash occurrence.
     */
    public static Integer MAX_HASH_LINE_WINDOW = 700000;

    /**
     * A positive number (expected to be equal or greater than one). This number is used for detecting the mining
     * occurrence and is the count of observing hash occurrence in a configurable line window to interpret the mining.
     */
    public static Integer MIN_HASH_OCCURRENCE_COUNT = 2;
}
