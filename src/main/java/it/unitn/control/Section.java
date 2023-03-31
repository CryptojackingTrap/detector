package it.unitn.control;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
/**
 * Specify the
 */
public class Section implements Comparable<Section> {
    private String splitContent;
    private Integer splitIdx;

    private Integer lineNumber;
    private Integer lineIdx;
    /**
     * if ErrorType is {@link SplitErrorType#TOO_SMALL} or {@link SplitErrorType#TOO_LATE}, then
     * ({@link Section}#lineNumber and {@link Section}{@link #lineIdx}) would be not empty but if the error is
     * {@link SplitErrorType#MISSED}, then they are null.
     */
    private SplitErrorType errorType;

    @Override
    public int compareTo(Section o) {
        return this.splitIdx.compareTo(o.getSplitIdx());
    }
}
