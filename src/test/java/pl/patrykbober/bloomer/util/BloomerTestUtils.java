package pl.patrykbober.bloomer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BloomerTestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

}
