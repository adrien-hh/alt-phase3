package org.alt.bo.dto;

import lombok.*;
import org.alt.bo.output.MetricsIntermediate;
import org.alt.bo.output.ScheduleEntryIntermediate;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class LabOutputIntermediate {
    private List<ScheduleEntryIntermediate> schedule;
    private MetricsIntermediate metrics;
}
