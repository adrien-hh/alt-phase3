package org.alt.bo.output;

import lombok.*;
import org.alt.bo.input.Priority;

import java.time.LocalTime;

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
    private LocalTime startTime;
    private LocalTime endTime;
    private Priority priority;
}
