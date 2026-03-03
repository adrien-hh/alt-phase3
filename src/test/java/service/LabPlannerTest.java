package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.alt.bo.dto.LabInput;
import org.alt.bo.dto.LabOutput;
import org.alt.service.LabPlanner;
import org.alt.utils.JsonUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LabPlannerTest {

    private final ObjectMapper mapper = JsonUtils.mapper();
    private final LabPlanner planner = new LabPlanner();

    @ParameterizedTest
    @ValueSource(strings = {"example1", "example2", "example3"})
    void examples_should_match_expected_output(String name) throws Exception {

        LabInput input = JsonUtils.read(name + "-input.json", LabInput.class);
        LabOutput actualOutput = planner.planifyLab(input);

        JsonNode expected = JsonUtils.readAsJsonNode(name + "-expected.json");
        JsonNode actual = mapper.valueToTree(actualOutput);

        JsonNode expectedNorm = mapper.readTree(mapper.writeValueAsBytes(expected));
        JsonNode actualNorm = mapper.readTree(mapper.writeValueAsBytes(actual));

        assertEquals(expectedNorm, actualNorm);
    }
}