package it.unitn.dto.xmf;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BlockLogs {
    private List<BlockLog> blockLogList;

    public void add(BlockLog blockLog) {
        if (blockLog != null) {
            if (blockLogList == null) {
                blockLogList = new ArrayList<>();
            }
            blockLogList.add(blockLog);
        }
    }

    public void addAll(BlockLogs blockLogs) {
        if (blockLogs != null) {
            if (blockLogList == null) {
                blockLogList = new ArrayList<>();
            }
            blockLogList.addAll(blockLogs.getBlockLogList());
        }
    }
}
