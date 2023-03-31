package it.unitn.control;

import it.unitn.dto.DetectorResult;

public interface DetectorListener {
    /**
     * would be call after detection is completed
     */
    void detectionResultCallback(DetectorResult detectorResult);
}
