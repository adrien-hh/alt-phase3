package org.alt.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.alt.bo.common.AssignmentIntermediate;
import org.alt.bo.dto.LabInputIntermediate;
import org.alt.bo.dto.LabOutputIntermediate;
import org.alt.bo.input.intermediate.EquipmentIntermediate;
import org.alt.service.intermediate.MetricsCalculatorIntermediate;
import org.alt.bo.input.intermediate.SampleIntermediate;
import org.alt.bo.input.intermediate.TechnicianIntermediate;
import org.alt.bo.output.MetricsIntermediate;
import org.alt.bo.output.ScheduleEntryIntermediate;
import org.alt.service.intermediate.ResourceAssignerIntermediate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Setter
public class LabPlannerIntermediate {

    private final SampleSorter sampleSorter;
    private final ResourceAssignerIntermediate resourceAssigner;
    private final MetricsCalculatorIntermediate metricsCalculator;

    public LabPlannerIntermediate() {
        this.sampleSorter = new SampleSorter();
        this.resourceAssigner = new ResourceAssignerIntermediate();
        this.metricsCalculator = new MetricsCalculatorIntermediate();
    }

    public LabOutputIntermediate planifyLab(LabInputIntermediate input) {
        List<SampleIntermediate> samples = sampleSorter.sortByPriorityThenArrival(input.getSamples());

        List<TechnicianIntermediate> technicians = input.getTechnicians();
        List<EquipmentIntermediate> equipments = input.getEquipments();

        if (technicians.isEmpty() || equipments.isEmpty()) {
            return new LabOutputIntermediate(List.of(), new MetricsIntermediate());
        }

        resourceAssigner.initMapping(equipments);

        BuiltSchedule builtSchedule = buildSchedule(samples, technicians, equipments);

        MetricsIntermediate metrics = metricsCalculator.computeMetrics(input, builtSchedule.entries(), builtSchedule.conflicts());

        return new LabOutputIntermediate(builtSchedule.entries(), metrics);
    }

    private BuiltSchedule buildSchedule(
            List<SampleIntermediate> samples,
            List<TechnicianIntermediate> technicians,
            List<EquipmentIntermediate> equipments
    ) {
        List<ScheduleEntryIntermediate> schedule = new ArrayList<>();
        int conflicts = 0;

        Map<String, LocalTime> technicianFreeAt = technicians.stream()
                .collect(Collectors.toMap(TechnicianIntermediate::getId, TechnicianIntermediate::getStartTime));
        Map<String, LocalTime> equipmentFreeAt = equipments.stream()
                .collect(Collectors.toMap(EquipmentIntermediate::getId, e -> LocalTime.MIN));

        for (SampleIntermediate sample : samples) {

            AssignmentIntermediate bestAssignment = resourceAssigner.findBestAssignment(
                    sample, technicians, equipments, technicianFreeAt, equipmentFreeAt);

            if (bestAssignment == null) {
                conflicts++;
                continue;
            }

            // durée ajustée par le coefficient d'efficacité
            long duration = Math.round(sample.getAnalysisTime() / bestAssignment.technician().getEfficiency());
            LocalTime end = bestAssignment.start().plusMinutes(duration);

            // shift check
            if (end.isAfter(bestAssignment.technician().getEndTime())) {
                conflicts++;
                continue;
            }

            schedule.add(toEntry(sample, bestAssignment.technician(), bestAssignment.equipment(), bestAssignment.start(), end, duration));

            technicianFreeAt.put(bestAssignment.technician().getId(), end);
            // équipement libre après analyse + nettoyage
            equipmentFreeAt.put(bestAssignment.equipment().getId(), end.plusMinutes(bestAssignment.equipment().getCleaningTime()));
        }

        return new BuiltSchedule(schedule, conflicts);
    }

    private ScheduleEntryIntermediate toEntry(
            SampleIntermediate sample,
            TechnicianIntermediate technician,
            EquipmentIntermediate equipment,
            LocalTime start,
            LocalTime end,
            long duration
    ) {
        return ScheduleEntryIntermediate.builder()
                .sampleId(sample.getId())
                .technicianId(technician.getId())
                .equipmentId(equipment.getId())
                .startTime(start)
                .endTime(end)
                .priority(sample.getPriority())
                .duration((int) duration)
                .analysisType(sample.getAnalysisType())
                .efficiency(technician.getEfficiency())
                .build();
    }

    private record BuiltSchedule(List<ScheduleEntryIntermediate> entries, int conflicts) {
    }
}
