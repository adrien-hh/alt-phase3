package org.alt.bo.dto;

import org.alt.bo.output.ScheduleEntry;

import java.util.List;

public record BuiltSchedule(
        List<ScheduleEntry> scheduleEntries,
        int conflicts
) {
}
