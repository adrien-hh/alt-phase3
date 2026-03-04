package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alt.bo.dto.LabInputIntermediate;
import org.alt.bo.dto.LabOutputIntermediate;
import org.alt.service.LabPlannerIntermediate;
import org.alt.utils.JsonUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class LabPlannerIntermediateTest {

    private final ObjectMapper mapper = JsonUtils.mapper();
    private final LabPlannerIntermediate planner = new LabPlannerIntermediate();

    @ParameterizedTest
    @ValueSource(strings = {
            "example4",
            "example5",
//            "example6"
    })
    void examples_should_match_expected_output(String name) throws Exception {

        LabInputIntermediate input = JsonUtils.read(name + "-input.json", LabInputIntermediate.class);
        LabOutputIntermediate actualOutput = planner.planifyLab(input);

        String expected = mapper.writeValueAsString(
                JsonUtils.readAsJsonNode(name + "-expected.json"));
        String actual = mapper.writeValueAsString(
                mapper.valueToTree(actualOutput));

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }
}