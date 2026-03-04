package org.alt.bo.input.intermediate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.alt.bo.input.simple.Technician;
import org.alt.utils.TimeWindowDeserializer;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class TechnicianIntermediate extends Technician {

  @JsonProperty("speciality")
  @JsonAlias("specialty")
  private List<SpecialityIntermediate> specialities = new ArrayList<>();

  private double efficiency;

  @JsonDeserialize(using = TimeWindowDeserializer.class)
  private TimeWindow lunchBreak;
}
