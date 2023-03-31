package it.unitn.control;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Split implements Comparable<Split> {
    private String longestSubString;
    private Integer hashStartIdx;
    private Integer lineStartIdx;
    private Integer hashAbsoluteIdx;
    private Integer lineStartAbsoluteIdx;
    private Integer fileLineNumber;
    private List<SplitOccurrence> splitOccurrences;
    private SplitErrorType splitErrorType;

    /**
     * This method is <b>always</b> called after finding the longest split
     *
     * @param hashAbsoluteIdx
     */
    public void adjustHashAbsoluteIdx(Integer hashAbsoluteIdx) {
        this.hashAbsoluteIdx = this.hashStartIdx + hashAbsoluteIdx;
    }

    public void setLineStartIdx(Integer lineStartIdx) {
        this.lineStartIdx = lineStartIdx;
        this.lineStartAbsoluteIdx = lineStartIdx;
    }

    /**
     * This method is called <b>only</b> when the longest substring is fined in a line that we don't search it from the
     * index 0
     * so by calling this method absolute index of the line can be corrected.
     *
     * @param fileLineIdxFrom
     */
    public void adjustLineStartAbsoluteIdx(Integer fileLineIdxFrom) {
        this.lineStartAbsoluteIdx = this.lineStartIdx + fileLineIdxFrom;
    }

    @Override
    public int compareTo(Split o) {
        return this.hashAbsoluteIdx.compareTo(o.getHashAbsoluteIdx());
    }

    public void addSplitOccurrence(SplitOccurrence splitOccurrence) {
        if (splitOccurrences == null) {
            splitOccurrences = new ArrayList<>();
        }
        splitOccurrences.add(splitOccurrence);
    }

    public Boolean hasError() {
        return splitErrorType != null;
    }
}
