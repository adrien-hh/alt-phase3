package org.alt.service;

import org.alt.bo.input.simple.Sample;

import java.util.Comparator;
import java.util.List;

public class SampleSorter {

    public <T extends Sample> List<T> sortByPriorityThenArrival(List<T> samples) {
        samples.sort(
                Comparator.comparingInt((T sample) -> sample.getPriority().ordinal()) // STAT → URGENT → ROUTINE
                        .thenComparing(Sample::getArrivalTime) // then chronological order
        );
        return samples;
    }
}
