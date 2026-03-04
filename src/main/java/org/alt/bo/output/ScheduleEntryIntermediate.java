package org.alt.bo.output;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@SuperBuilder
public class ScheduleEntryIntermediate extends ScheduleEntry {
    private int duration;
    private String analysisType;
    private double efficiency;
    private int cleaningDelay;
}

