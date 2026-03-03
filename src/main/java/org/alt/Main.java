package org.alt;

import org.alt.bo.dto.LabInput;
import org.alt.bo.dto.LabOutput;
import org.alt.service.LabPlanner;
import org.alt.utils.JsonUtils;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    private static final String OUTPUT_FILE = "planning.json";

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: <input.json>");
        }

        String inputFile = args[0];

        LabInput input = JsonUtils.read(inputFile, LabInput.class);
        LabOutput output = new LabPlanner().planifyLab(input);

        JsonUtils.mapper()
                .writerWithDefaultPrettyPrinter()
                .writeValue(Path.of(OUTPUT_FILE).toFile(), output);
    }
}
