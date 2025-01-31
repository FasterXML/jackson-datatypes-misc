package com.fasterxml.jackson.datatype.jsonp;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.json.JsonMergePatch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonMergePatchSerializationTest extends TestBase
{
    private static final ObjectMapper MAPPER = newMapper();

    @Test
    public void testSimpleSerialization() throws Exception
    {
        // First need a patch so deserialization must work
        final String input = "{" +
                "\"name\":\"Json\"" +
                "}";
        final JsonMergePatch patch1 = MAPPER.readValue(input, JsonMergePatch.class);
        final String output = MAPPER.writeValueAsString(patch1);

        // and read back
        final JsonMergePatch patch2 = MAPPER.readValue(output, JsonMergePatch.class);
        assertEquals(patch1.toJsonValue(), patch2.toJsonValue());
    }
}
