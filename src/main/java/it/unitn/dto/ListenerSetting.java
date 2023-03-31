package it.unitn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * the settings related to a cryptocurrency network listener
 */
@Setter
@Getter
@AllArgsConstructor
@ToString
public class ListenerSetting {
    /**
     * standard name of cryptocurrencies such as Bitcoin, Ethereum and so on.
     */
    private String cryptocurrencyName;
    /**
     * this path can be a directory or one file.
     * For the former case the whole files in the directory are scanned and considered.
     */
    private FileSetting listenerLogsFileSetting;
}