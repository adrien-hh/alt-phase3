package org.alt.service.intermediate;

import org.alt.bo.dto.LabInputIntermediate;
import org.alt.bo.input.Priority;
import org.alt.bo.input.intermediate.SampleIntermediate;
import org.alt.bo.input.intermediate.TechnicianIntermediate;
import org.alt.bo.output.MetricsIntermediate;
import org.alt.bo.output.ScheduleEntry;
import org.alt.bo.output.ScheduleEntryIntermediate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class MetricsCalculatorIntermediate {

    public MetricsIntermediate computeMetrics(LabInputIntermediate input, List<ScheduleEntryIntermediate> schedule, int conflicts) {
        long totalTime = computeTotalTime(schedule);
        double efficiency = computeEfficiency(input.getTechnicians(), schedule, totalTime);
        double averageWaitTime = computeAverageWaitTime(input.getSamples(), schedule);
        double technicianUtilization = computeTechnicianUtilization(input.getTechnicians(), schedule);
        int priorityRespectRate = computePriorityRespectRate(schedule);
        int parallelEfficiency = computeParallelEfficiency(schedule);

        return MetricsIntermediate.builder()
                .totalTime(totalTime)
                .efficiency(efficiency)
                .conflicts(conflicts)
                .averageWaitTime(averageWaitTime)
                .technicianUtilization(technicianUtilization)
                .priorityRespectRate(priorityRespectRate)
                .parallelEfficiency(parallelEfficiency)
                .build();
    }

    private long computeTotalTime(List<ScheduleEntryIntermediate> schedule) {
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
        return Duration.between(earliestStart, latestEnd).toMinutes();
    }

    private double computeEfficiency(
            List<TechnicianIntermediate> technicians,
            List<ScheduleEntryIntermediate> schedule,
            long totalTime
    ) {
        if (totalTime == 0) {
            return 0.0;
        }

        double totalOccupied = technicians.stream()
                .mapToLong(technician -> schedule.stream()
                        .filter(scheduleEntry -> scheduleEntry.getTechnicianId().equals(technician.getId()))
                        .mapToLong(ScheduleEntryIntermediate::getDuration)
                        .sum())
                .sum();
        return roundToOneDecimal(totalOccupied / technicians.size() / totalTime * 100);
    }

    private double computeTechnicianUtilization(
            List<TechnicianIntermediate> technicians,
            List<ScheduleEntryIntermediate> schedule
    ) {
        return roundToOneDecimal(technicians.stream()
                .mapToDouble(t -> {
                    long worked = schedule.stream()
                            .filter(e -> e.getTechnicianId().equals(t.getId()))
                            .mapToLong(ScheduleEntryIntermediate::getDuration)
                            .sum();
                    long shift = Duration.between(t.getStartTime(), t.getEndTime()).toMinutes();
                    return shift == 0 ? 0.0 : 100.0 * worked / shift;
                })
                .average()
                .orElse(0.0));
    }

    private double computeAverageWaitTime(
            List<SampleIntermediate> samples,
            List<ScheduleEntryIntermediate> schedule
    ) {
        return roundToOneDecimal(schedule.stream()
                .mapToLong(entry -> {
                    SampleIntermediate sample = samples.stream()
                            .filter(s -> s.getId().equals(entry.getSampleId()))
                            .findFirst().orElse(null);
                    if (sample == null) return 0;
                    return Duration.between(sample.getArrivalTime(), entry.getStartTime()).toMinutes();
                })
                .average()
                .orElse(0.0));
    }

    private int computePriorityRespectRate(List<ScheduleEntryIntermediate> schedule) {
        for (int i = 0; i < schedule.size(); i++) {
            for (int j = i + 1; j < schedule.size(); j++) {
                Priority pi = schedule.get(i).getPriority();
                Priority pj = schedule.get(j).getPriority();
                LocalTime si = schedule.get(i).getStartTime();
                LocalTime sj = schedule.get(j).getStartTime();
                if (pj.ordinal() < pi.ordinal() && sj.isBefore(si)) {
                    return 0;
                }
            }
        }
        return 100;
    }

    private int computeParallelEfficiency(List<ScheduleEntryIntermediate> schedule) {
        return (int) schedule.stream()
                .filter(e1 -> schedule.stream()
                        .anyMatch(e2 -> !e2.equals(e1)
                                && e2.getStartTime().isBefore(e1.getEndTime())
                                && e2.getEndTime().isAfter(e1.getStartTime())))
                .count();
    }

    private double roundToOneDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
