package org.alt;

import org.alt.bo.dto.LabInput;
import org.alt.bo.dto.LabOutput;
import org.alt.service.LabPlanner;
import org.alt.utils.JsonUtils;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        LabInput input = JsonUtils.read("example1-input.json", LabInput.class);

        LabOutput output = new LabPlanner().planifyLab(input);

        JsonUtils.mapper().writerWithDefaultPrettyPrinter()
                .writeValue(System.out, output);
    }
}
