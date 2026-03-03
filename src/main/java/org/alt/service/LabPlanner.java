package org.alt.service;

import org.alt.bo.dto.LabInput;
import org.alt.bo.dto.LabOutput;
import org.alt.bo.dto.BuiltSchedule;
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

        if (input.getTechnicians().isEmpty() || input.getEquipments().isEmpty()) {
            return new LabOutput(List.of(), new Metrics(0, 0.0, 1));
        }

        Technician technician = input.getTechnicians().getFirst();
        Equipment equipment = input.getEquipments().getFirst();

        BuiltSchedule builtSchedule = buildSchedule(samples, technician, equipment);

        Metrics metrics = computeMetrics(input, builtSchedule.scheduleEntries(), builtSchedule.conflicts());

        return new LabOutput(builtSchedule.scheduleEntries(), metrics);
    }

    private BuiltSchedule buildSchedule(List<Sample> samples, Technician technician, Equipment equipment) {

        List<ScheduleEntry> schedule = new ArrayList<>();
        int conflicts = 0;

        LocalTime technicianFreeAt = technician.getStartTime();
        LocalTime equipmentFreeAt = LocalTime.MIN;

        for (Sample sample : samples) {

            LocalTime start = maxStartTime(
                    sample.getArrivalTime(),
                    technician.getStartTime(),
                    technicianFreeAt,
                    equipmentFreeAt
            );

            LocalTime end = start.plusMinutes(sample.getAnalysisTime());

            if (end.isAfter(technician.getEndTime())) {
                conflicts++;
                continue;
            }

            schedule.add(toEntry(sample, technician, equipment, start, end)
            );

            technicianFreeAt = end;
            equipmentFreeAt = end;
        }
        return new BuiltSchedule(schedule, conflicts);
    }

    private ScheduleEntry toEntry(Sample sample, Technician technician, Equipment equipment, LocalTime start, LocalTime end) {
        return ScheduleEntry.builder()
                .sampleId(sample.getId())
                .technicianId(technician.getId())
                .equipmentId(equipment.getId())
                .startTime(start)
                .endTime(end)
                .priority(sample.getPriority())
                .build();
    }

    private Metrics computeMetrics(LabInput input, List<ScheduleEntry> schedule, int conflicts) {
        long totalTime = computeTotalTime(input, schedule);
        int totalAnalysis = input.getSamples().stream().mapToInt(Sample::getAnalysisTime).sum();
        double efficiency = totalTime == 0 ? 0.0 : roundToOneDecimal(100.0 * totalAnalysis / totalTime);
        return new Metrics(totalTime, efficiency, conflicts);
    }

    private long computeTotalTime(LabInput input, List<ScheduleEntry> schedule) {
        LocalTime earliestArrival = input.getSamples().stream()
                .map(Sample::getArrivalTime)
                .min(LocalTime::compareTo)
                .orElse(null);

        LocalTime latestEnd = schedule.stream()
                .map(ScheduleEntry::getEndTime)
                .max(LocalTime::compareTo)
                .orElse(null);

        if (earliestArrival == null || latestEnd == null) return 0;
        return minutesBetween(earliestArrival, latestEnd);
    }

    private double roundToOneDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
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