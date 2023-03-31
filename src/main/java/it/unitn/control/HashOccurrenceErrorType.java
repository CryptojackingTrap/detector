package it.unitn.control;

/**
 * todo {you can organize none valid HashOccurrences into these later but now just make log of them instantly and
 * remove them and don't keep them till the end of process}
 * <p>
 * determine the error types that a {@link HashOccurrence} is ignored in further calculations
 */
public enum HashOccurrenceErrorType {
    /**
     * no split occurrence could be found
     */
    NO_SPLIT_OCCURRENCE,
    /**
     * it has one split occurrence but this one has error
     */
    ONE_BAD_SPLIT_OCCURRENCE,

    /**
     * the hash coverage for this hash occurrence is less than  AlgorithmConstants.MIN_HASH_TOO_SMALL_HASH_COVERAGE
     */
    TOO_SMALL_HASH_COVERAGE,

}
