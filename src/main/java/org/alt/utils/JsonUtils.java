package org.alt.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;

public final class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private JsonUtils() {}

    public static ObjectMapper mapper() {
        return MAPPER;
    }

    public static <T> T read(String resourceName, Class<T> type) throws IOException {
        try (InputStream is = JsonUtils.class
                .getClassLoader()
                .getResourceAsStream(resourceName)) {

            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourceName);
            }

            return MAPPER.readValue(is, type);
        }
    }

    public static JsonNode readAsJsonNode(String resourceName) throws IOException {
        return read(resourceName, JsonNode.class);
    }
}
