package org.alt.bo.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.alt.bo.input.Priority;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@SuperBuilder
public class ScheduleEntry {
  private String sampleId;
  private String technicianId;
  private String equipmentId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime startTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
  private LocalTime endTime;

  private Priority priority;
}
