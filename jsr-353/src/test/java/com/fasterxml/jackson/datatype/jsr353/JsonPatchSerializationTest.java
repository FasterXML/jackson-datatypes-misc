package com.fasterxml.jackson.datatype.jsr353;

import javax.json.JsonPatch;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class JsonPatchSerializationTest extends TestBase
{
    private static final ObjectMapper MAPPER = newMapper();

    @Test
    public void testSimpleSerialization() throws Exception
    {
        // First need a patch so deserialization must work
        final String input = "[" +
                "{" +
                "\"op\":\"replace\"," +
                "\"path\":\"/name\"," +
                "\"value\":\"Json\"" +
                "}" +
                "]";
        final JsonPatch jsonPatch = MAPPER.readValue(input, JsonPatch.class);
        final String output = MAPPER.writeValueAsString(jsonPatch);

        // and read back
        final JsonPatch jsonPatch2 = MAPPER.readValue(output, JsonPatch.class);
        assertEquals(jsonPatch, jsonPatch2);
    }
}
