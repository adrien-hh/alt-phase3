package org.alt.bo.output;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@SuperBuilder
public class MetricsIntermediate extends Metrics {
  private double averageWaitTime;
  private double technicianUtilization;
  private int priorityRespectRate;
  private int parallelEfficiency;
  private int statResponseTime;
}
