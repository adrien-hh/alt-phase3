package org.alt.bo.output;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Output {
    private Schedule schedule;
    private Metrics metrics;
}
