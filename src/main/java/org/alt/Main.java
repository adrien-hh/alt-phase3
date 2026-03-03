package org.alt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alt.bo.dto.LabInput;
import org.alt.bo.dto.LabOutput;
import org.alt.service.LabPlanner;

import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        InputStream inputStream = Main.class
                .getClassLoader()
                .getResourceAsStream("example1-input.json");

        LabInput input = mapper.readValue(inputStream, LabInput.class);

        LabPlanner planner = new LabPlanner();
        planner.planifyLab(input);
        // LabOutput output = planner.planifyLab(input);

        // mapper.writerWithDefaultPrettyPrinter(.writeValue(System.out, output);
    }
}