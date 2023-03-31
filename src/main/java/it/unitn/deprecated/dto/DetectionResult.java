package it.unitn.deprecated.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DetectionResult {
    //todo integrate into DetectorResult
    /**
     * if the detector detects Cryptojacking activity then detected must be true and otherwise it must be false
     */
    private Boolean detected;
    /**
     * if detected is true, then the list of detectedCryptocurrencyTypes must have at least one item
     */
    private List<String> detectedCryptocurrencyNames;

    public void addDetectedCryptocurrencyName(String cryptoName) {
        if (detectedCryptocurrencyNames == null) {
            detectedCryptocurrencyNames = new ArrayList<>();
        }
        detectedCryptocurrencyNames.add(cryptoName);
    }
}
