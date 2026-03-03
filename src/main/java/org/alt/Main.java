package org.alt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.alt.bo.dto.LabInput;
import org.alt.bo.dto.LabOutput;
import org.alt.service.LabPlanner;

import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);;

        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("example1-input.json");

        LabInput input = mapper.readValue(inputStream, LabInput.class);

        LabPlanner planner = new LabPlanner();
        LabOutput output = planner.planifyLab(input);

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(System.out, output);
    }
}