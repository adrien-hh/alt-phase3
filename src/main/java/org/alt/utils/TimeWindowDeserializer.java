package org.alt.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.LocalTime;
import org.alt.bo.input.intermediate.TimeWindow;

public class TimeWindowDeserializer extends StdDeserializer<TimeWindow> {

  public TimeWindowDeserializer() {
    super(TimeWindow.class);
  }

  @Override
  public TimeWindow deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String[] parts = p.getText().split("-");
    return new TimeWindow(LocalTime.parse(parts[0]), LocalTime.parse(parts[1]));
  }
}
