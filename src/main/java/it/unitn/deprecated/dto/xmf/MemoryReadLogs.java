package it.unitn.deprecated.dto.xmf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class MemoryReadLogs {
    private List<MemoryReadLog> memoryReadLogList;
}
