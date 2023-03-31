package it.unitn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class MonitorSetting {
    /**
     * This path can be a directory or one file.
     * For the former case the whole files in the directory are scanned and considered.
     */
    private FileSetting monitoringFileSetting;
}
