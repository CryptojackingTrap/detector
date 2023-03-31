package it.unitn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class ListenerSettings {
    private List<ListenerSetting> listenerSettingList;
}
