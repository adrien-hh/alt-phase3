package org.alt.bo.output;

import lombok.*;
import org.alt.bo.input.Priority;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Schedule {

    private List<ScheduleEntry> entries = new ArrayList<>();

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    private static class ScheduleEntry {
        private String sampleId;
        private String technicianId;
        private String equipmentId;
        private String startTime;
        private String endTime;
        private Priority priority;
    }
}
