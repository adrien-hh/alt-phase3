package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.alt.bo.dto.LabInput;
import org.alt.bo.dto.LabOutput;
import org.alt.service.LabPlanner;
import org.alt.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LabPlannerTest {

    @Test
    void example1_should_match_expected_output() throws Exception {

        ObjectMapper mapper = JsonUtils.mapper();

        LabInput input = JsonUtils.read("example1-input.json", LabInput.class);

        LabOutput actualOutput = new LabPlanner().planifyLab(input);

        JsonNode expected = JsonUtils.readAsJsonNode("example1-expected.json");
        JsonNode actual = mapper.valueToTree(actualOutput);

        // Canonicalize: reparse serialized form (normalise numbers/structure)
        JsonNode expectedNorm = mapper.readTree(mapper.writeValueAsBytes(expected));
        JsonNode actualNorm = mapper.readTree(mapper.writeValueAsBytes(actual));

        assertEquals(expectedNorm, actualNorm);
    }

    @Test
    void example2_should_match_expected_output() throws Exception {

        ObjectMapper mapper = JsonUtils.mapper();

        LabInput input = JsonUtils.read("example2-input.json", LabInput.class);

        LabOutput actualOutput = new LabPlanner().planifyLab(input);

        JsonNode expected = JsonUtils.readAsJsonNode("example2-expected.json");
        JsonNode actual = mapper.valueToTree(actualOutput);

        // Canonicalize: reparse serialized form (normalise numbers/structure)
        JsonNode expectedNorm = mapper.readTree(mapper.writeValueAsBytes(expected));
        JsonNode actualNorm = mapper.readTree(mapper.writeValueAsBytes(actual));

        assertEquals(expectedNorm, actualNorm);
    }

    @Test
    void example3_should_match_expected_output() throws Exception {

        ObjectMapper mapper = JsonUtils.mapper();

        LabInput input = JsonUtils.read("example3-input.json", LabInput.class);

        LabOutput actualOutput = new LabPlanner().planifyLab(input);

        JsonNode expected = JsonUtils.readAsJsonNode("example3-expected.json");
        JsonNode actual = mapper.valueToTree(actualOutput);

        // Canonicalize: reparse serialized form (normalise numbers/structure)
        JsonNode expectedNorm = mapper.readTree(mapper.writeValueAsBytes(expected));
        JsonNode actualNorm = mapper.readTree(mapper.writeValueAsBytes(actual));

        assertEquals(expectedNorm, actualNorm);
    }
}
