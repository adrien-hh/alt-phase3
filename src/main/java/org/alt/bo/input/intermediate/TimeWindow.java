package org.alt.bo.input.intermediate;

import java.time.LocalTime;

public record TimeWindow(LocalTime start, LocalTime end) {
  public boolean overlaps(LocalTime from, LocalTime to) {
    return from.isBefore(end) && to.isAfter(start);
  }
}
