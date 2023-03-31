package it.unitn.control;

import it.unitn.view.CentralConsole;
import lombok.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HashOccurrence {
    private String hash;
    private List<Section> sections;

    private Boolean isPreprocessed = false;
    private Integer hashCoveragePercentage = null;

    /**
     * sort the sections and assign {@link SplitErrorType#TOO_LATE} error in case of necessity.
     * this method must be called after any change in sections and before any further process such as validation
     */
    public void preprocess() {
        if (isPreprocessed) {
            return;
        }

        /**
         * Sort sections based on their hash index
         */
        Collections.sort(sections);

        /**
         * find first not null section
         */
        int i = 0;
        while (i < sections.size() && sections.get(i).getErrorType() != null) {
            i++;
        }

        Integer lastLine = sections.get(i).getLineNumber();
        i++;
        for (; i < sections.size(); i++) {
            if (sections.get(i).getErrorType() == null) {
                if (sections.get(i).getLineNumber() - lastLine
                        > AlgorithmConstants.MAX_SPLIT_LINE_WINDOW) {
                    sections.get(i).setErrorType(SplitErrorType.TOO_LATE);
                }
                lastLine = sections.get(i).getLineNumber();
            } else {
                continue;// without moving forward the lastLine
            }
        }
        isPreprocessed = true;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
        isPreprocessed = false;
    }

    /**
     * calculate the hash coverage percentage for the hash occurrence.
     *
     * @return
     */
    public Boolean isValid() {
        if (sections == null || sections.size() == 0) {
            return false;
        } else if (sections.size() == 1) {
            return (sections.get(0).getErrorType() == null);
        } else {
            calculateHashCoveragePercentage();
            Boolean isValid = false;
            if (hashCoveragePercentage > AlgorithmConstants.MIN_HASH_COVERAGE) {
                isValid = true;
            }
            return isValid;
        }
    }

    private void calculateHashCoveragePercentage() {
        preprocess();
        if (hashCoveragePercentage == null) {
            Integer hashCoverage = 0;
            for (Section section : sections) {
                if (section.getErrorType() == null) {
                    hashCoverage += section.getSplitContent().length();
                }
            }
            hashCoveragePercentage = 100 * hashCoverage / hash.length();
            CentralConsole.log("hash coverage percentage is: " + hashCoveragePercentage + " for hash value: " + hash);
        }
    }

    public Integer getStartLineNumber() {
        preprocess();
        for (Section section : sections) {
            if (section.getErrorType() != null) {
                continue;
            } else {
                return section.getLineNumber();
            }
        }
        throw new RuntimeException("no valid section in hash occurrence");
    }

    public Integer getEndLineNumber() {
        preprocess();
        for (int i = sections.size() - 1; i >= 0; i--) {
            if (sections.get(i).getErrorType() != null) {
                continue;
            } else {
                return sections.get(i).getLineNumber();
            }
        }
        throw new RuntimeException("no valid section in hash occurrence");
    }

    public String getInfo() {
        String errors = "";
        Map<SplitErrorType, Integer> errors2Count = new HashMap<>();
        Integer sum = 0;
        Integer count = 0;
        Integer maxSize = 0;
        for (Section section : sections) {
            if (section.getErrorType() == null) {
                sum += section.getSplitContent().length();
                count++;
                if (section.getSplitContent().length() > maxSize) {
                    maxSize = section.getSplitContent().length();
                }
            } else {
                errors += section.getErrorType() + ", ";
                Integer errorCount = errors2Count.get(section.getErrorType());
                if (errorCount == null) {
                    errors2Count.put(section.getErrorType(), 1);
                } else {
                    errors2Count.put(section.getErrorType(), errorCount + 1);
                }
            }
        }
        String errorMsg = "";
        for (SplitErrorType errorType : SplitErrorType.values()) {
            Integer errorCount = errors2Count.get(errorType);
            errorMsg += "\t" + errorType.toString() + "=\t" + (errorCount == null ? "0" : errorCount + "");
        }

        String info =
                "HashCoveragePercentage:\t" + getHashCoveragePercentage() + "\t, Sections size: \t" + sections.size() +
                        "\t, maxSplitSize:\t" + maxSize + "\t, AvgSize:\t" + ((float) (sum / count)) + "\t, " +
                        "SectionErrorTypesCount" +
                        ":\t" + errorMsg + "\t SectionErrorTypes:\t" + errors;
        return info;
    }
}
