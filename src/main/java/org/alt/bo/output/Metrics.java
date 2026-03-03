package org.alt.bo.output;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Metrics {
    private long totalTime; // total duration in minutes
    private double efficiency; // % = (sum of analysis durations) / (total planning time) * 100
    private int conflicts;
}
