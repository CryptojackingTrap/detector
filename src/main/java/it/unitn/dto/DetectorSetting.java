package it.unitn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class DetectorSetting {
    private ListenerSettings listenerSettings;
    private MonitorSetting monitorSetting;
    private Boolean searchGreedily;
}
