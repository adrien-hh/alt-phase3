package org.alt.bo.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.alt.bo.input.Priority;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class ScheduleEntry {
    private String sampleId;
    private String technicianId;
    private String equipmentId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;
    private Priority priority;
}
