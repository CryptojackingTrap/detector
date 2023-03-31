package it.unitn.deprecated.control.search;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Split1 {
    private String longestSubString;
    private Integer firstStartIdx;
    private Integer secondStartIdx;
    private Integer firstAbsoluteIdx;
    private Integer secondAbsoluteIdx;
    private Integer secondLineNumber;

    public void setAbsoluteIdx(Integer firstAbsoluteIdx, Integer secondAbsoluteIdx) {
        this.firstAbsoluteIdx = firstAbsoluteIdx + firstStartIdx;
        this.secondAbsoluteIdx = secondAbsoluteIdx + secondStartIdx;
    }
}
