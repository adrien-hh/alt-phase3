package org.alt.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.alt.bo.common.Assignment;
import org.alt.bo.dto.LabInput;
import org.alt.bo.dto.LabOutput;
import org.alt.bo.input.simple.Equipment;
import org.alt.bo.input.simple.Sample;
import org.alt.bo.input.simple.Technician;
import org.alt.bo.output.Metrics;
import org.alt.bo.output.ScheduleEntry;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Setter
public class LabPlanner {

    private final SampleSorter sampleSorter;
    private final ResourceAssigner resourceAssigner;
    private final MetricsCalculator metricsCalculator;

    public LabPlanner() {
        this.sampleSorter = new SampleSorter();
        this.resourceAssigner = new ResourceAssigner();
        this.metricsCalculator = new MetricsCalculator();
    }

    public LabOutput planifyLab(LabInput input) {
        List<Sample> samples = sampleSorter.sortByPriorityThenArrival(input.getSamples());

        List<Technician> technicians = input.getTechnicians();
        List<Equipment> equipments = input.getEquipments();

        if (technicians.isEmpty() || equipments.isEmpty()) {
            return new LabOutput(List.of(), new Metrics(0, 0.0, 1));
        }

        BuiltSchedule builtSchedule = buildSchedule(samples, technicians, equipments);

        Metrics metrics = metricsCalculator.computeMetrics(input, builtSchedule.scheduleEntries(), builtSchedule.conflicts());

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

            Assignment bestAssignment = resourceAssigner.findBestAssignment(sample, technicians, equipments, technicianFreeAt, equipmentFreeAt);

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

    private record BuiltSchedule(List<ScheduleEntry> scheduleEntries, int conflicts) {
    }
}