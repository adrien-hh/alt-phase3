package org.alt.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.alt.bo.common.AssignmentIntermediate;
import org.alt.bo.common.LunchState;
import org.alt.bo.dto.LabInputIntermediate;
import org.alt.bo.dto.LabOutputIntermediate;
import org.alt.bo.input.intermediate.EquipmentIntermediate;
import org.alt.bo.input.intermediate.SampleIntermediate;
import org.alt.bo.input.intermediate.TechnicianIntermediate;
import org.alt.bo.output.MetricsIntermediate;
import org.alt.bo.output.ScheduleEntryIntermediate;
import org.alt.service.intermediate.MetricsCalculatorIntermediate;
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

        Map<String, LunchState> lunchStates = technicians.stream()
                .collect(Collectors.toMap(
                        TechnicianIntermediate::getId,
                        t -> LunchState.notTaken()
                ));

        for (SampleIntermediate sample : samples) {

            AssignmentIntermediate bestAssignment = resourceAssigner.findBestAssignment(
                    sample, technicians, equipments, technicianFreeAt, equipmentFreeAt, lunchStates);

            if (bestAssignment == null) {
                conflicts++;
                System.out.println("No assignment found for: " + sample.getId());
                continue;
            }

            // if start in window and break not taken → plan lunch break
            String techId = bestAssignment.technician().getId();
            LunchState lunch = lunchStates.get(techId);
            if (!lunch.taken() && LunchState.isInLunchWindow(bestAssignment.start())) {
                lunchStates.put(techId, LunchState.schedule(bestAssignment.start()));
            }

            long duration = Math.round(sample.getAnalysisTime() / bestAssignment.technician().getEfficiency());
            LocalTime end = bestAssignment.start().plusMinutes(duration);

            if (end.isAfter(bestAssignment.technician().getEndTime())) {
                conflicts++;
                System.out.println("Shift overflow for: " + sample.getId() +
                        " end=" + end + " techEnd=" + bestAssignment.technician().getEndTime());
                continue;
            }

            schedule.add(toEntry(sample, bestAssignment.technician(), bestAssignment.equipment(), bestAssignment.start(), end, duration));

            // Technician update : if end in window and break not taken -> break after
            lunch = lunchStates.get(techId);
            if (!lunch.taken() && LunchState.isInLunchWindow(end)) {
                LunchState scheduled = LunchState.schedule(end);
                lunchStates.put(techId, scheduled);
                technicianFreeAt.put(techId, scheduled.end());
            } else {
                technicianFreeAt.put(techId, end);
            }

            equipmentFreeAt.put(bestAssignment.equipment().getId(),
                    end.plusMinutes(bestAssignment.equipment().getCleaningTime()));
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
