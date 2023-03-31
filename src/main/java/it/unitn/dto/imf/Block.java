package it.unitn.dto.imf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class Block {
    private String headerHash;
    private Date receiveDate;
    private Date blockDate;
}
