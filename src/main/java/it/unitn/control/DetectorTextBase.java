package it.unitn.control;

import com.google.common.base.Stopwatch;
import it.unitn.deprecated.control.search.SearchResponse;
import it.unitn.dto.DetectorResult;
import it.unitn.dto.DetectorSetting;
import it.unitn.view.CentralConsole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class DetectorTextBase implements Runnable {
    private FileParser fileParser = new FileParser();

    /**
     * ------------------------
     * Timer
     * ------------------------
     */
    private static void logStart(String message, Stopwatch timer) {
        timer.reset();
        timer.start();
        CentralConsole.log("start " + message);

    }

    private static void logEnd(String message, Stopwatch timer) {
        timer.stop();
        CentralConsole.log("end " + message, timer);
    }


    /**
     * ------------------------
     * Thread
     * ------------------------
     */
    private DetectorSetting detectorSetting;
    private List<DetectorListener> detectorListeners = new ArrayList<>();

    public void registerDetectorListener(DetectorListener listener) {
        detectorListeners.add(listener);
    }

    private void notifyListeners(DetectorResult detectorResult) {
        for (DetectorListener detectorListener : detectorListeners) {
            detectorListener.detectionResultCallback(detectorResult);
        }
    }

    public void setDetectorSetting(DetectorSetting detectorSetting) {
        this.detectorSetting = detectorSetting;
    }

    /**
     * assume detectorSetting is assigned
     */
    @Override
    public void run() {
        if (detectorSetting != null) {
            try {
                DetectorResult detectorResult = startDetection(detectorSetting);
                notifyListeners(detectorResult);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public DetectorResult startDetection(DetectorSetting detectorSetting) throws IOException, ParseException {
        Stopwatch timer = Stopwatch.createUnstarted();
        logStart("startDetection (" + detectorSetting + ")", timer);
        DetectorResult detectorResult = new DetectorResult();
        validateDetectorSetting(detectorSetting);
        List<BlockReadLog> blockReadLogs = fileParser.getBlockReadLogs(detectorSetting);
        for (BlockReadLog blockReadLog : blockReadLogs) {
            List<MiningOccurrence> miningOccurrences = this.findAllMiningOccurrences(blockReadLog.getHashValue(),
                    blockReadLog.getMonitorFile());
            detectorResult.addAll(miningOccurrences);
        }
        logEnd("startDetection, DetectorResult: " + detectorResult, timer);
        return detectorResult;
    }

    /**
     * validate the parameters and throws exception if there is any problem with the settings, and we cannot start
     * detection
     *
     * @param detectorSetting
     * @throws RuntimeException
     */
    private void validateDetectorSetting(DetectorSetting detectorSetting) throws RuntimeException {
        //todo
    }

    /**
     * if a configurable number of hash occurrences are in a window, it is interpreted as a mining occurrence.
     * the window is calculated by calculating the line
     *
     * @param hash
     * @param file
     * @return
     */
    public List<MiningOccurrence> findAllMiningOccurrences(String hash, File file) {
        Stopwatch timer = Stopwatch.createUnstarted();
        logStart("findAllMiningOccurrences(hash:" + hash + ", File: " + file.getName() + ")", timer);
        List<HashOccurrence> hashOccurrences = findAllHashOccurrences(hash, file);
        List<MiningOccurrence> miningOccurrences = new ArrayList<>();

        if (hashOccurrences == null || hashOccurrences.size() == 0) {
            return null;
        }

        if (AlgorithmConstants.MIN_HASH_OCCURRENCE_COUNT == 1) {
            /**
             * no need for calculating the distance between hashOccurrences
             */
            MiningOccurrence miningOccurrence = new MiningOccurrence();
            miningOccurrence.setHash(hash);
            miningOccurrence.setAcceptedHashOccurrences(hashOccurrences);
            miningOccurrences.add(miningOccurrence);
            return miningOccurrences;
        } else {
            MiningOccurrence miningOccurrence = new MiningOccurrence();
            miningOccurrence.setHash(hash);
            miningOccurrence.setAcceptedHashOccurrences(new ArrayList<>());
            Boolean isFirstValidHashOcc = true;
            Integer lastValidLineNumber = null;
            for (HashOccurrence hashOccurrence : hashOccurrences) {
                if (hashOccurrence.isValid()) {
                    if (isFirstValidHashOcc) {
                        miningOccurrence.getAcceptedHashOccurrences().add(hashOccurrence);
                        isFirstValidHashOcc = false;
                        lastValidLineNumber = hashOccurrence.getEndLineNumber();
                    } else {
                        /**
                         * calculate the distance:
                         */
                        Integer currentStartLineNumber = hashOccurrence.getStartLineNumber();
                        if (currentStartLineNumber - lastValidLineNumber < AlgorithmConstants.MAX_HASH_LINE_WINDOW) {
                            miningOccurrence.getAcceptedHashOccurrences().add(hashOccurrence);
                            lastValidLineNumber = hashOccurrence.getEndLineNumber();
                        } else {
                            CentralConsole.log("hash occurrence is deleted because of being too late. its distance is" +
                                    " : " + (currentStartLineNumber - lastValidLineNumber) +
                                    " while is more than MAX_HASH_LINE_WINDOW value: " +
                                    AlgorithmConstants.MAX_HASH_LINE_WINDOW);
                            CentralConsole.info("hash occurrence too late:" + (currentStartLineNumber - lastValidLineNumber));
                        }
                    }
                }
            }
            miningOccurrences.add(miningOccurrence);
            logEnd("findAllMiningOccurrences, miningOccurrences:" + miningOccurrences, timer);
            return miningOccurrences;
        }
    }

    /**
     * split the hash into the <b>biggest parts</b> that can be found in the file. The splits must be bigger than a
     * threshold ({@link AlgorithmConstants#MIN_SIZE_OF_HASH_SPLIT}). Also the splits must be significantly close to
     * each other. Their occurrence line number must be less that a threshold
     * ({@link AlgorithmConstants#MAX_SPLIT_LINE_WINDOW})
     * <p>
     * If <b>acceptable percentage</b> of the given hash content (specified by
     * {@link AlgorithmConstants#MIN_HASH_COVERAGE} can be found in the file in a way that splits are in order, and
     * above-mentioned criteria are valid, it is interpreted as a hash occurrence.
     *
     * @param hash
     * @param file
     * @return
     */
    public List<HashOccurrence> findAllHashOccurrences(String hash, File file) {
        Stopwatch timer = Stopwatch.createUnstarted();
        logStart("findAllHashOccurrences(hash:" + hash + ", File: " + file.getName() + ")", timer);
        List<Split> splitList = findAllSplitOccurrences(hash, file);
        if (splitList == null || splitList.size() == 0) {
            return null;
        }
        /**
         * if for any occurrence for first split, we can find the second one with limited line number differences and
         * so on so forth for further splits, we interpret this as a hash occurrence
         */
        List<HashOccurrence> hashOccurrences = new ArrayList<>();

        /**
         * assumedHashOcc: we consider the "minimum split occurrence count" in all split occurrences that their size
         * are not less than {@link AlgorithmConstants#MIN_SIZE_OF_HASH_SPLIT}.
         */
        Integer assumedHashOccCount = getAssumedHashOccCount(splitList);
        /**
         * each split occurrence of the first split is the beginning of an any possible hash
         */
        for (int splitOccIdx = 0; splitOccIdx < assumedHashOccCount; splitOccIdx++) {
            HashOccurrence hashOccurrence = new HashOccurrence();
            hashOccurrence.setHash(hash);
            List<Section> sections = new ArrayList<>();
            for (Split split : splitList) {
                Section section = new Section();
                if (split.hasError()) {
                    section.setErrorType(split.getSplitErrorType());
                } else {
                    SplitOccurrence splitOccurrence = null;
                    try {
                        splitOccurrence = split.getSplitOccurrences().get(splitOccIdx);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        throw t;
                    }
                    section.setLineNumber(splitOccurrence.getLineNumber());
                    section.setLineIdx(splitOccurrence.getLineIdx());
                }
                section.setSplitContent(split.getLongestSubString());
                section.setSplitIdx(split.getHashAbsoluteIdx());
                sections.add(section);
            }
            hashOccurrence.setSections(sections);
            if (hashOccurrence.isValid()) {
                hashOccurrences.add(hashOccurrence);
            } else {
                CentralConsole.log("WARN - invalid hash occurrence: " + hashOccurrence);
                CentralConsole.info(hashOccurrence.getInfo());
            }
        }
        logEnd("findAllHashOccurrences, hashOccurrences:" + hashOccurrences, timer);
        return hashOccurrences;
    }

    private Integer getAssumedHashOccCount(List<Split> splitList) {
        Integer count = Integer.MAX_VALUE;
        for (Split split : splitList) {
            if (!split.hasError()) {
                if (split.getSplitOccurrences().size() < count) {
                    count = split.getSplitOccurrences().size();
                }
            }
        }
        return count;
    }

    /**
     * @param hash
     * @param file
     * @return
     */
    public List<Split> findAllSplitOccurrences(String hash, File file) {
        Stopwatch timer = Stopwatch.createUnstarted();
        logStart("findAllSplitOccurrences(hash:" + hash + ", File: " + file.getName() + ")", timer);
        List<Split> splitList = splitHash(hash, file);
        for (Split split : splitList) {
            /**
             * if the split is not missed, check for its size:
             */
            if (!split.hasError() && split.getLongestSubString().length() < AlgorithmConstants.MIN_SIZE_OF_HASH_SPLIT) {
                split.setSplitErrorType(SplitErrorType.TOO_SMALL);
            }
        }
        List<Split> list = findOccurrencesForGivenSplits(splitList, file);
        logEnd("findAllSplitOccurrences, List<Split>:" + list, timer);
        return list;
    }

    /**
     * for each split in the splitList finds all
     *
     * @param splitList
     * @param file
     * @return
     */
    public List<Split> findOccurrencesForGivenSplits(List<Split> splitList, File file) {
        Stopwatch timer = Stopwatch.createUnstarted();
        logStart("findOccurrencesForGivenSplits(List<Split> :" + splitList + ", File: " + file.getName() + ")", timer);
        if (splitList == null || splitList.size() == 0) {
            return null;
        }

        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                for (Split split : splitList) {
                    if (!split.hasError()) {
                        Integer lineIdx = line.indexOf(split.getLongestSubString());
                        if (lineIdx != -1) {
                            SplitOccurrence splitOccurrence = new SplitOccurrence(lineNumber, lineIdx);
                            split.addSplitOccurrence(splitOccurrence);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        logEnd("findOccurrencesForGivenSplits, List<Split>:" + splitList, timer);
        return splitList;
    }

    /**
     * split the hash into the biggest splits such that all splits can be found in the file
     *
     * @param hash
     * @param file
     * @return
     */
    public List<Split> splitHash(String hash, File file) {
        Stopwatch timer = Stopwatch.createUnstarted();
        logStart("splitHash(hash :" + hash + ", File: " + file.getName() + ")", timer);
        List<Split> accumulativeResult = new ArrayList<>();
        splitHash(hash, file, 0,
                1, 0, null, null,
                accumulativeResult);
        Collections.sort(accumulativeResult);
        logEnd("splitHash, List<Split>:" + accumulativeResult, timer);
        return accumulativeResult;
    }

    /**
     * for index and line number, it includes that number for both from amd to
     * line number starts from 1
     * all indexed starts from zero
     * null values for line number or index values means no checking and limitation for that one (infinity)
     * From value for line number or index can be bigger than its possible and just skip it without exception
     *
     * @param hashSubStr
     * @param file
     * @param hashSubStrAbsoluteIdx
     * @param fileLineNumberFrom
     * @param fileLineIdxFrom
     * @param fileLineNumberTo
     * @param fileLineIdxTo
     * @param accumulativeResult
     */
    private void splitHash(String hashSubStr, File file, Integer hashSubStrAbsoluteIdx,
                           Integer fileLineNumberFrom, Integer fileLineIdxFrom, Integer fileLineNumberTo,
                           Integer fileLineIdxTo,
                           List<Split> accumulativeResult) {
        Stopwatch innerTimer = Stopwatch.createUnstarted();
        logStart("splitHash(hashSubStr:" + hashSubStr + ", file:" + file.getName() +
                ", hashSubStrAbsoluteIdx:" + hashSubStrAbsoluteIdx +
                ", fileLineNumberFrom:" + fileLineNumberFrom + ", fileLineIdxFrom:" + fileLineIdxFrom +
                ", fileLineNumberTo:" + fileLineNumberTo + ", fileLineIdxTo:" + fileLineIdxTo +
                ", accumulativeResult:" + accumulativeResult + ")", innerTimer);
        /**
         * Base Case of recursion
         */
        if (hashSubStr == null) {
            return;
        }
        if (fileLineIdxTo != null && fileLineIdxTo == 1 &&
                fileLineIdxTo != null && fileLineIdxTo == 0) {
            return;
        }

        if ((fileLineNumberFrom != null && fileLineNumberTo != null && fileLineNumberFrom > fileLineNumberTo) ||
                (fileLineNumberFrom != null && fileLineNumberTo != null && fileLineNumberFrom == fileLineNumberTo &&
                        fileLineIdxFrom != null && fileLineIdxTo != null && fileLineIdxFrom > fileLineIdxTo
                )) {
            /**
             * It is expected that this part detects some part of hash sub string that are missed in the file between
             * two splits.
             * for example: hash: ...abc...  file ...ac... and b is missed in the file
             */
            noResultWarn(hashSubStr, file, hashSubStrAbsoluteIdx, fileLineNumberFrom, fileLineIdxFrom,
                    fileLineNumberTo, fileLineIdxTo, accumulativeResult);
            return;
        }


        /**
         * Internal Exception handling
         */
        if (hashSubStrAbsoluteIdx < 0 || fileLineNumberFrom < 1 || fileLineIdxFrom < 0 ||
                (fileLineNumberTo != null && fileLineNumberTo < 1) || (fileLineIdxTo != null && fileLineIdxTo < 0)) {
            throw new RuntimeException("unsupported internal value");
        }

        SearchResponse response = new SearchResponse();
        response.setFile(file);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            int lineNumber = 0;

            Split mainSplit = new Split();
            Split split = null;
            /**
             * iterate the whole file and find the biggest share substring with hash
             */
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                /**
                 * skip lines out of line number boundaries
                 */
                if (lineNumber < fileLineNumberFrom) {
                    continue;
                } else if (fileLineNumberTo != null && lineNumber > fileLineNumberTo) {
                    break;
                }

                /**
                 * skip characters in the first and last lines that are out of index boundaries
                 */
                line = getLineSubString(fileLineNumberFrom, fileLineNumberTo, fileLineIdxFrom,
                        fileLineIdxTo, lineNumber, line);

                split = lcs(hashSubStr, line);
                if (split == null)
                    continue;
                split.setFileLineNumber(lineNumber);
                if (mainSplit == null || mainSplit.getLongestSubString() == null ||
                        mainSplit.getLongestSubString().length() < split.getLongestSubString().length()) {
                    mainSplit = split;
                }
            }
            if (mainSplit.getLongestSubString() == null) {
                //search is done but there is no result for method call.
                noResultWarn(hashSubStr, file, hashSubStrAbsoluteIdx, fileLineNumberFrom, fileLineIdxFrom,
                        fileLineNumberTo, fileLineIdxTo, accumulativeResult);
                return;
            }
            mainSplit.adjustHashAbsoluteIdx(hashSubStrAbsoluteIdx);
            if (mainSplit.getFileLineNumber() == fileLineNumberFrom) {
                mainSplit.adjustLineStartAbsoluteIdx(fileLineIdxFrom);
            }
            accumulativeResult.add(mainSplit);
            CentralConsole.log("Main Split: " + mainSplit);

            //left
            if (mainSplit.getHashStartIdx() != 0) {
                String hashLeftSubStr = hashSubStr.substring(0, mainSplit.getHashStartIdx());
                Integer fileLeftSideLineFrom = fileLineNumberFrom;
                Integer fileLeftSideLineIdxFrom = fileLineIdxFrom;

                /**
                 * initialize with non supported value. both must be initialized before recursive call
                 */
                Integer fileLeftSideLineTo = -1;
                Integer fileLeftSideLineIdxTo = -1;

                if (mainSplit.getLineStartIdx() != 0) {//use the left side of the main split in the same line
                    fileLeftSideLineTo = mainSplit.getFileLineNumber();
                    fileLeftSideLineIdxTo = mainSplit.getLineStartIdx() - 1;
                } else if (mainSplit.getLineStartIdx() == 0) {//there is nothing in the left of main split in that line
                    if (mainSplit.getFileLineNumber() == 1) {
                        //nothing in left side remained, do nothing in recursive call
                        fileLeftSideLineTo = 1;
                        fileLeftSideLineIdxTo = 0;
                    } else if (mainSplit.getFileLineNumber() > 1) {
                        fileLeftSideLineTo = mainSplit.getFileLineNumber();
                        fileLeftSideLineIdxTo = null;//until the of that line
                    }
                }

                splitHash(hashLeftSubStr, file, hashSubStrAbsoluteIdx, fileLeftSideLineFrom, fileLeftSideLineIdxFrom,
                        fileLeftSideLineTo, fileLeftSideLineIdxTo,
                        accumulativeResult);
            }

            //right
            Integer hashSplitEndIdx = mainSplit.getHashStartIdx() + mainSplit.getLongestSubString().length();

            if (hashSplitEndIdx < hashSubStr.length()) {
                String hashRightSubStr = hashSubStr.substring(hashSplitEndIdx);
                Integer fileRightSideLineTo = fileLineNumberTo;
                Integer fileRightSideLineIdxTo = fileLineIdxTo;

                /**
                 *  We don't have perception of the size of current line or number of lines in the file.
                 *  So the values can exceed, and it doesn't matter
                 */
                Integer fileRightSideLineFrom = mainSplit.getFileLineNumber();
                Integer fileRightSideLineIdxFrom = mainSplit.getLineStartIdx() + mainSplit.getLongestSubString().length();

                Integer hashRightSubStrAbsoluteIdx =
                        hashSubStrAbsoluteIdx + mainSplit.getHashStartIdx() + mainSplit.getLongestSubString().length();
                splitHash(hashRightSubStr, file, hashRightSubStrAbsoluteIdx, fileRightSideLineFrom,
                        fileRightSideLineIdxFrom, fileRightSideLineTo, fileRightSideLineIdxTo, accumulativeResult);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (response.getHits().size() > 0) {
                handleSearchResponse(response);
                //found++;
            }
            try {
                reader.close();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
        logEnd("splitHash", innerTimer);
    }

    public static String getLineSubString(Integer fileLineNumberFrom, Integer fileLineNumberTo,
                                          Integer fileLineIdxFrom,
                                          Integer fileLineIdxTo, Integer lineNumber, String line) {
        if (fileLineNumberFrom != null && fileLineNumberFrom == lineNumber
                && (fileLineNumberTo == null || fileLineNumberTo != null)) {
            //do nothing
        } else if (fileLineNumberFrom != null && fileLineNumberTo != null) {
            if (lineNumber == fileLineNumberFrom && lineNumber == fileLineNumberTo) {
                if (fileLineIdxFrom != null && fileLineIdxTo != null) {
                    line = line.substring(fileLineIdxFrom, fileLineIdxTo);
                } else if (fileLineIdxFrom != null && fileLineIdxTo == null) {
                    line = line.substring(fileLineIdxFrom);
                } else if (fileLineIdxFrom == null && fileLineIdxTo != null) {
                    line = line.substring(0, fileLineIdxTo);
                }
            }
        } else if (lineNumber == fileLineNumberFrom && fileLineNumberFrom < fileLineNumberTo) {
            line = line.substring(fileLineIdxFrom);
        } else if (fileLineNumberTo != null && lineNumber == fileLineNumberTo) {
            line = line.substring(0, fileLineIdxTo + 1);
        }
        //-------------------
        /**
         * 1) i to end
         */
        if (fileLineNumberFrom != null && fileLineNumberFrom == lineNumber && fileLineIdxFrom != null &&
                (fileLineNumberTo == null ||
                        (fileLineNumberTo != null && fileLineIdxTo == null) ||
                        (fileLineNumberTo != null && fileLineIdxTo != null && fileLineNumberFrom != fileLineNumberTo))) {
            line = line.substring(fileLineIdxFrom);
        }
        /**
         * 2) i to j
         */
        else if (fileLineNumberFrom != null && fileLineNumberTo != null &&
                fileLineIdxFrom != null && fileLineIdxTo != null &&
                lineNumber == fileLineNumberFrom && lineNumber == fileLineNumberTo &&
                fileLineIdxFrom <= fileLineIdxTo) {
            line = line.substring(fileLineIdxFrom, fileLineIdxTo + 1);
        }
        /**
         * 3) 0 to j
         */
        else if (fileLineNumberTo != null && fileLineIdxTo != null &&
                lineNumber == fileLineNumberFrom &&
                !(fileLineNumberFrom != null && fileLineIdxFrom != null && lineNumber == fileLineNumberFrom)) {
            line = line.substring(0, fileLineIdxTo + 1);
        }
        return line;
    }

    private void noResultWarn(String hashSubStr, File file, Integer hashSubStrAbsoluteIdx,
                              Integer fileLineNumberFrom, Integer fileLineIdxFrom, Integer fileLineNumberTo,
                              Integer fileLineIdxTo, List<Split> accumulativeResult) {
        CentralConsole.log("WARN - no result for: " + "hashSubStr: " + hashSubStr + ", file: " +
                file.getName() + ", hashSubStrAbsoluteIdx: " + hashSubStrAbsoluteIdx +
                ", fileLineNumberFrom: " + fileLineNumberFrom + ", fileLineIdxFrom: " + fileLineIdxFrom +
                ", fileLineNumberTo: " + fileLineNumberTo + ", fileLineIdxTo: " + fileLineIdxTo);

        Split splitWithError = new Split();
        splitWithError.setLongestSubString(hashSubStr);
        splitWithError.setHashAbsoluteIdx(hashSubStrAbsoluteIdx);
        splitWithError.setSplitErrorType(SplitErrorType.MISSED);
        accumulativeResult.add(splitWithError);
    }

    public static Split lcs(String hash, String line) {
        Split lcsResult = null;
        if (null == hash || null == line) return null;
        String longestShare = "";
        for (int hashIdx = 0; hashIdx < hash.length(); ++hashIdx) {
            for (int lineIdx = 0; lineIdx < line.length(); ++lineIdx) {
                int subLength;
                for (subLength = 0;
                     hashIdx + subLength < hash.length() && lineIdx + subLength < line.length();
                     ++subLength) {
                    if (hash.charAt(hashIdx + subLength) != line.charAt(lineIdx + subLength)) {
                        break;
                    }
                }
                if (subLength > longestShare.length()) {
                    if (lcsResult == null) {
                        lcsResult = new Split();
                    }
                    longestShare = hash.substring(hashIdx, hashIdx + subLength);
                    lcsResult.setHashStartIdx(hashIdx);
                    lcsResult.setLineStartIdx(lineIdx);
                    lcsResult.setLongestSubString(longestShare);
                }
            }
        }
        return lcsResult;
    }

    private void handleSearchResponse(SearchResponse response) {
        CentralConsole.log("SearchResponse: " + response.toString());
    }
}
