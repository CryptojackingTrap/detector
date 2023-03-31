package it.unitn.dto.xmf;

import it.unitn.dto.imf.Block;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class BlockLog {
    private String cryptocurrencyName;
    private List<Block> blockList;
}
