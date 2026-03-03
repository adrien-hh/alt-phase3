package org.alt.service;

import org.alt.bo.dto.LabInput;
import org.alt.bo.dto.LabOutput;
import org.alt.bo.input.Equipment;
import org.alt.bo.input.Sample;
import org.alt.bo.input.Technician;
import org.alt.bo.output.Metrics;
import org.alt.bo.output.ScheduleEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LabPlanner {
    public LabOutput planifyLab(LabInput input) {
        List<Sample> samples = sortByPriorityThenArrival(input.getSamples());

        List<ScheduleEntry> schedule = new ArrayList<>();
        int conflicts = 0;

        Technician technician = input.getTechnicians().getFirst();
        Equipment equipment = input.getEquipments().getFirst();

        if (technician == null || equipment == null) {
            return new LabOutput(List.of(), new Metrics(0, 0.0, 1));
        }

        LocalTime techFreeAt = technician.getStartTime();
        LocalTime equipFreeAt = LocalTime.MIN;

        for (Sample sample : samples) {

            LocalTime start = maxStartTime(
                    sample.getArrivalTime(),
                    technician.getStartTime(),
                    techFreeAt,
                    equipFreeAt
            );

            LocalTime end = start.plusMinutes(sample.getAnalysisTime());

            if (end.isAfter(technician.getEndTime())) {
                conflicts++;
                continue;
            }

            schedule.add(ScheduleEntry.builder()
                    .sampleId(sample.getId())
                    .technicianId(technician.getId())
                    .equipmentId(equipment.getId())
                    .startTime(start)
                    .endTime(end)
                    .priority(sample.getPriority())
                    .build()
            );

            techFreeAt = end;
            equipFreeAt = end;
        }

        LocalTime earliestArrival = input.getSamples().stream()
                .map(Sample::getArrivalTime)
                .min(LocalTime::compareTo)
                .orElse(null);

        LocalTime latestEnd = schedule.stream()
                .map(ScheduleEntry::getEndTime)
                .max(LocalTime::compareTo)
                .orElse(null);

        long totalTime = 0;
        if (earliestArrival != null && latestEnd != null) {
            totalTime = minutesBetween(earliestArrival, latestEnd);
        }

        int totalAnalysis = input.getSamples().stream().mapToInt(Sample::getAnalysisTime).sum();

        double efficiency = totalTime == 0
                ? 0.0
                : BigDecimal.valueOf(100.0 * totalAnalysis / totalTime)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        return new LabOutput(schedule, new Metrics(totalTime, efficiency, conflicts));
    }

    private long minutesBetween(LocalTime startTime, LocalTime endTime) {
        return Duration.between(startTime, endTime).toMinutes();
    }

    private List<Sample> sortByPriorityThenArrival(List<Sample> samples) {
        samples.sort(
                Comparator.comparingInt((Sample s) -> s.getPriority().ordinal()) // STAT → URGENT → ROUTINE
                .thenComparing(Sample::getArrivalTime) // then chronological order
        );
        return samples;
    }

    private LocalTime maxStartTime(LocalTime... times) {
        LocalTime max = times[0];
        for (int i = 1; i < times.length; i++) {
            if (times[i].isAfter(max)) max = times[i];
        }
        return max;
    }
}
