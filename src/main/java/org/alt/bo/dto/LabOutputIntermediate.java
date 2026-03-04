package org.alt.bo.dto;

import java.util.List;
import lombok.*;
import org.alt.bo.output.MetricsIntermediate;
import org.alt.bo.output.ScheduleEntryIntermediate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class LabOutputIntermediate {
  private List<ScheduleEntryIntermediate> schedule;
  private MetricsIntermediate metrics;
}
