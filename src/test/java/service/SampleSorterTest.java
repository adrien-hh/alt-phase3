package service;

import org.alt.bo.input.Priority;
import org.alt.bo.input.simple.Sample;
import org.alt.service.SampleSorter;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SampleSorterTest {

    private final SampleSorter sorter = new SampleSorter();

    private Sample sample(String id, Priority priority, String arrivalTime) {
        Sample s = new Sample();
        s.setId(id);
        s.setPriority(priority);
        s.setArrivalTime(LocalTime.parse(arrivalTime));
        return s;
    }

    @Test
    void stat_should_come_before_urgent_and_routine() {
        List<Sample> samples = List.of(
                sample("S1", Priority.ROUTINE, "09:00"),
                sample("S2", Priority.URGENT, "09:05"),
                sample("S3", Priority.STAT, "09:20")
        );

        List<Sample> sorted = sorter.sortByPriorityThenArrival(new ArrayList<>(samples));

        assertEquals(Priority.STAT, sorted.get(0).getPriority());
        assertEquals(Priority.URGENT, sorted.get(1).getPriority());
        assertEquals(Priority.ROUTINE, sorted.get(2).getPriority());
    }

    @Test
    void same_priority_should_be_sorted_by_arrival_time() {
        List<Sample> samples = List.of(
                sample("S1", Priority.URGENT, "10:00"),
                sample("S2", Priority.URGENT, "09:00"),
                sample("S3", Priority.URGENT, "09:30")
        );

        List<Sample> sorted = sorter.sortByPriorityThenArrival(new ArrayList<>(samples));

        assertEquals("S2", sorted.get(0).getId());
        assertEquals("S3", sorted.get(1).getId());
        assertEquals("S1", sorted.get(2).getId());
    }

    @Test
    void stat_arriving_late_should_still_come_first() {
        List<Sample> samples = List.of(
                sample("S1", Priority.ROUTINE, "08:00"),
                sample("S2", Priority.URGENT, "08:30"),
                sample("S3", Priority.STAT, "14:00")
        );

        List<Sample> sorted = sorter.sortByPriorityThenArrival(new ArrayList<>(samples));

        assertEquals("S3", sorted.get(0).getId());
    }

    @Test
    void empty_list_should_return_empty() {
        List<Sample> sorted = sorter.sortByPriorityThenArrival(new ArrayList<>());
        assertTrue(sorted.isEmpty());
    }

    @Test
    void single_sample_should_return_same() {
        List<Sample> samples = new ArrayList<>(List.of(sample("S1", Priority.STAT, "09:00")));
        List<Sample> sorted = sorter.sortByPriorityThenArrival(samples);
        assertEquals(1, sorted.size());
        assertEquals("S1", sorted.get(0).getId());
    }
}
