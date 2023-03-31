package it.unitn.control;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MiningOccurrence {
    private String hash;
    private List<HashOccurrence> acceptedHashOccurrences;
}
