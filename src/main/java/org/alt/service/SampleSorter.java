package org.alt.service;

import org.alt.bo.input.simple.Sample;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SampleSorter {

    public <T extends Sample> List<T> sortByPriorityThenArrival(List<T> samples) {
        return samples.stream()
                .sorted(Comparator.comparingInt((T s) -> s.getPriority().ordinal())
                        .thenComparing(Sample::getArrivalTime))
                .collect(Collectors.toList());
    }
}
