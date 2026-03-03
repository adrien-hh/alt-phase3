package org.alt.bo.output;

import lombok.*;
import org.alt.bo.input.Priority;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ScheduleEntry {
    private String sampleId;
    private String technicianId;
    private String equipmentId;
    private String startTime;
    private String endTime;
    private Priority priority;
}
