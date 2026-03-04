package org.alt.service;

import org.alt.bo.dto.LabInput;
import org.alt.bo.dto.LabOutput;
import org.alt.bo.input.Equipment;
import org.alt.bo.input.Sample;
import org.alt.bo.input.Speciality;
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
import java.util.Map;
import java.util.stream.Collectors;

public class LabPlanner {
    public LabOutput planifyLab(LabInput input) {
        List<Sample> samples = sortByPriorityThenArrival(input.getSamples());

        List<Technician> technicians = input.getTechnicians();
        List<Equipment> equipments = input.getEquipments();

        if (technicians.isEmpty() || equipments.isEmpty()) {
            return new LabOutput(List.of(), new Metrics(0, 0.0, 1));
        }

        BuiltSchedule builtSchedule = buildSchedule(samples, technicians, equipments);

        Metrics metrics = computeMetrics(input, builtSchedule.scheduleEntries(), builtSchedule.conflicts());

        return new LabOutput(builtSchedule.scheduleEntries(), metrics);
    }

    private BuiltSchedule buildSchedule(List<Sample> samples, List<Technician> technicians, List<Equipment> equipments) {
        List<ScheduleEntry> schedule = new ArrayList<>();
        int conflicts = 0;

        Map<String, LocalTime> technicianFreeAt = technicians.stream()
                .collect(Collectors.toMap(Technician::getId, Technician::getStartTime));
        Map<String, LocalTime> equipmentFreeAt = equipments.stream()
                .collect(Collectors.toMap(Equipment::getId, e -> LocalTime.MIN));

        for (Sample sample : samples) {

            Assignment bestAssignment = findBestAssignment(sample, technicians, equipments, technicianFreeAt, equipmentFreeAt);

            if (bestAssignment == null) {
                conflicts++;
                continue;
            }

            LocalTime end = bestAssignment.start().plusMinutes(sample.getAnalysisTime());

            // shift check
            if (end.isAfter(bestAssignment.technician().getEndTime())) {
                conflicts++;
                continue;
            }

            schedule.add(toEntry(sample, bestAssignment.technician(), bestAssignment.equipment(), bestAssignment.start(), end));

            technicianFreeAt.put(bestAssignment.technician().getId(), end);
            equipmentFreeAt.put(bestAssignment.equipment().getId(), end);
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

    private Assignment findBestAssignment(
            Sample sample,
            List<Technician> technicians,
            List<Equipment> equipments,
            Map<String, LocalTime> technicianFreeAt,
            Map<String, LocalTime> equipmentFreeAt
    ) {
        Assignment best = null;

        for (Technician t : technicians) {
            if (!isTechCompatible(t, sample)) continue;

            for (Equipment e : equipments) {
                if (!isEquipCompatible(e, sample)) continue;

                LocalTime start = maxStartTime(
                        sample.getArrivalTime(),
                        t.getStartTime(),
                        technicianFreeAt.get(t.getId()),
                        equipmentFreeAt.get(e.getId())
                );

                if (best == null || start.isBefore(best.start())) {
                    best = new Assignment(t, e, start);
                }
            }
        }
        return best;
    }

    private boolean isEquipCompatible(Equipment equipment, Sample sample) {
        return equipment.isAvailable() && equipment.getType() == sample.getType();
    }

    private boolean isTechCompatible(Technician technician, Sample sample) {
        return technician.getSpeciality().supports(sample.getType());
    }

    private Metrics computeMetrics(LabInput input, List<ScheduleEntry> schedule, int conflicts) {
        long totalTime = computeTotalTime(schedule);
        int totalAnalysis = input.getSamples().stream().mapToInt(Sample::getAnalysisTime).sum();
        double efficiency = totalTime == 0 ? 0.0 : roundToOneDecimal(100.0 * totalAnalysis / totalTime);
        return new Metrics(totalTime, efficiency, conflicts);
    }

    private long computeTotalTime(List<ScheduleEntry> schedule) {
        LocalTime earliestStart = schedule.stream()
                .map(ScheduleEntry::getStartTime)
                .min(LocalTime::compareTo)
                .orElse(null);

        LocalTime latestEnd = schedule.stream()
                .map(ScheduleEntry::getEndTime)
                .max(LocalTime::compareTo)
                .orElse(null);

        if (earliestStart == null) {
            return 0;
        }
        return minutesBetween(earliestStart, latestEnd);
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

    private record BuiltSchedule(List<ScheduleEntry> scheduleEntries, int conflicts) {
    }

    private record Assignment(Technician technician, Equipment equipment, LocalTime start) {
    }
}