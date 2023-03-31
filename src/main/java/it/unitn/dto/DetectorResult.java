package it.unitn.dto;

import it.unitn.control.MiningOccurrence;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class DetectorResult {
    private List<MiningOccurrence> miningOccurrences;

    public void add(MiningOccurrence miningOccurrence) {
        if (miningOccurrence != null) {
            if (miningOccurrences == null) {
                miningOccurrences = new ArrayList<>();
            }
            miningOccurrences.add(miningOccurrence);
        }
    }

    public void addAll(List<MiningOccurrence> miningOccurrences) {
        if (miningOccurrences != null) {
            if (this.miningOccurrences == null) {
                this.miningOccurrences = new ArrayList<>();
            }
            this.miningOccurrences.addAll(miningOccurrences);
        }
    }

    public Boolean getDetectorAlert() {
        if (miningOccurrences != null && miningOccurrences.size() >= 1) {
            return true;
        } else {
            return false;
        }
    }
}
