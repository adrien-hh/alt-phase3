package org.alt.bo.input.intermediate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.alt.bo.input.simple.Equipment;
import org.alt.utils.TimeWindowDeserializer;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class EquipmentIntermediate extends Equipment {
  @JsonProperty("type")
  private SpecialityIntermediate specialityIntermediate;

  private List<String> compatibleTypes = new ArrayList<>();

  private int capacity;

  @JsonDeserialize(using = TimeWindowDeserializer.class)
  private TimeWindow maintenanceWindow;

  private int cleaningTime;
}
