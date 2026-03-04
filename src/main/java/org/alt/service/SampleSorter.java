package org.alt.service;

import org.alt.bo.input.simple.Sample;

import java.util.Comparator;
import java.util.List;

public class SampleSorter {

    public List<Sample> sortByPriorityThenArrival(List<Sample> samples) {
        samples.sort(
                Comparator.comparingInt((Sample s) -> s.getPriority().ordinal()) // STAT → URGENT → ROUTINE
                        .thenComparing(Sample::getArrivalTime) // then chronological order
        );
        return samples;
    }
}
