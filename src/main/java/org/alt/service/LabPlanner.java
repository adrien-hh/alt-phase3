package org.alt.service;

import org.alt.bo.dto.LabInput;
import org.alt.bo.dto.LabOutput;
import org.alt.bo.input.Equipment;
import org.alt.bo.input.Sample;
import org.alt.bo.input.Technician;
import org.alt.bo.output.Metrics;
import org.alt.bo.output.ScheduleEntry;

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

        for (Sample sample : samples) {

            Technician technician = input.getTechnicians().getFirst();
            Equipment equipment = input.getEquipments().getFirst();

            if (technician == null || equipment == null) {
                conflicts++;
                continue;
            }

            LocalTime start = maxStartTime(
                    sample.getArrivalTime(),
                    technician.getStartTime()
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
        }

        // 2) Metrics (cas 1)
        // should get first and last schedule entry by time
        long totalTime = schedule.isEmpty() ? 0
                : minutesBetween(schedule.getFirst().getStartTime(), schedule.getLast().getEndTime());

        int totalAnalysis = input.getSamples().stream().mapToInt(Sample::getAnalysisTime).sum();

        double efficiency = totalTime == 0 ? 0.0 : (100.0 * totalAnalysis / totalTime);

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

    private LocalTime maxStartTime(LocalTime sampleArrival, LocalTime technicianStart) {
        if(technicianStart.isAfter(sampleArrival)) {
            return technicianStart;
        }
        return sampleArrival;
    }
}
