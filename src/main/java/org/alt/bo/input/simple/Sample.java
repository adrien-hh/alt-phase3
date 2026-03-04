package org.alt.bo.input.simple;

import java.time.LocalTime;
import lombok.*;
import org.alt.bo.input.Priority;
import org.alt.bo.input.Type;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Sample {
  private String id;
  private Type type;
  private Priority priority;
  private int analysisTime;
  private LocalTime arrivalTime;
  private String patientId;
}
