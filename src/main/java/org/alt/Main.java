package org.alt;

import java.io.IOException;
import java.nio.file.Path;
import org.alt.bo.dto.LabInputIntermediate;
import org.alt.bo.dto.LabOutputIntermediate;
import org.alt.service.LabPlannerIntermediate;
import org.alt.utils.JsonUtils;

public class Main {

  private static final String OUTPUT_FILE = "planning.json";

  public static void main(String[] args) throws IOException {

    if (args.length != 1) {
      throw new IllegalArgumentException("Usage: <input.json>");
    }

    String inputFile = args[0];

    LabInputIntermediate input = JsonUtils.read(inputFile, LabInputIntermediate.class);
    LabOutputIntermediate output = new LabPlannerIntermediate().planifyLab(input);

    JsonUtils.mapper()
        .writerWithDefaultPrettyPrinter()
        .writeValue(Path.of(OUTPUT_FILE).toFile(), output);
  }
}
