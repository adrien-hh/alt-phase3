package org.alt.bo.output;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Metrics {
    private Integer totalTime; // total duration in minutes
    private Double efficiency; // % = (sum of analysis durations) / (total planning time) * 100
    private Integer conflicts;
}
