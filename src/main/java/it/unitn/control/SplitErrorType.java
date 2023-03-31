package it.unitn.control;

/**
 * determine the error types that a hash split is ignored in the further calculations
 */
public enum SplitErrorType {
    /**
     * the split is not occurred in the expected part of the file
     */
    MISSED,

    /**
     * the split is found in the expected part of the file, but it is smaller than
     * {@link AlgorithmConstants#MIN_SIZE_OF_HASH_SPLIT}
     */
    TOO_SMALL,

    /**
     * the split is found in the file but its distance to its preceding split is more that
     * {@link AlgorithmConstants#MAX_SPLIT_LINE_WINDOW}
     */
    TOO_LATE;
}
