package org.alt.service;

import org.alt.bo.dto.LabInput;
import org.alt.bo.input.simple.Sample;
import org.alt.bo.output.Metrics;
import org.alt.bo.output.ScheduleEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class MetricsCalculator {

    public Metrics computeMetrics(LabInput input, List<ScheduleEntry> schedule, int conflicts) {
        long totalTime = computeTotalTime(schedule);
        int totalAnalysis = input.getSamples().stream().mapToInt(Sample::getAnalysisTime).sum();
        double efficiency = totalTime == 0 ? 0.0 : roundToOneDecimal(100.0 * totalAnalysis / totalTime);
        return new Metrics(totalTime, efficiency, conflicts);
    }

    public long computeTotalTime(List<ScheduleEntry> schedule) {
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

    public double roundToOneDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public long minutesBetween(LocalTime startTime, LocalTime endTime) {
        return Duration.between(startTime, endTime).toMinutes();
    }
}
