package org.alt.bo.dto;

import lombok.*;
import org.alt.bo.output.Metrics;
import org.alt.bo.output.ScheduleEntry;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class LabOutput {
    private List<ScheduleEntry> schedule;
    private Metrics metrics;
}
