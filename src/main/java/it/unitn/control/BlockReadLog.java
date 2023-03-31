package it.unitn.control;

import lombok.*;

import java.io.File;

@Setter
@Getter
@AllArgsConstructor
@ToString
@NoArgsConstructor
/**
 * This class is the corresponding BRL on the paper indicating all listener and monitoring data just related to a
 * limited time slot that the hash for one cryptocurrency is not changed.
 */
public class BlockReadLog {
    /**
     * standard name of cryptocurrencies such as Bitcoin, Ethereum and so on.
     */
    private String cryptocurrencyName;

    private String hashValue;

    private File monitorFile;
}
