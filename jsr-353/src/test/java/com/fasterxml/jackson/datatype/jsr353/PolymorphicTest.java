package com.fasterxml.jackson.datatype.jsr353;

import javax.json.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PolymorphicTest extends TestBase
{
    static class Wrapper {
        @JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.CLASS)
        public Object w;
        
        public Wrapper() { }
        public Wrapper(Object o) { w = o; }
    }

    private final ObjectMapper MAPPER = newMapper();
    
    public void testObjectAsTyped() throws Exception
    {
        final String INPUT = "{\"array\":[1,2],\"obj\":{\"first\":true}}";
        JsonValue v = MAPPER.readValue(INPUT, JsonValue.class);

        String POLY_JSON = MAPPER.writeValueAsString(new Wrapper(v));

        Wrapper w = MAPPER.readValue(POLY_JSON, Wrapper.class);
        assertTrue(w.w instanceof JsonObject);
        JsonObject ob = (JsonObject) w.w;
        assertEquals(2, ob.size());
        JsonArray arr = ob.getJsonArray("array");
        assertNotNull(arr);
        assertEquals(2, arr.size());
        assertEquals(1, arr.getInt(0));
    }

    public void testArrayAsTyped() throws Exception
    {
        final String INPUT = "[1,{\"a\":true}]";
        JsonValue v = MAPPER.readValue(INPUT, JsonValue.class);

        String POLY_JSON = MAPPER.writeValueAsString(new Wrapper(v));

        Wrapper w = MAPPER.readValue(POLY_JSON, Wrapper.class);
        assertTrue(w.w instanceof JsonArray);
        JsonArray arr = (JsonArray) w.w;
        assertEquals(2, arr.size());
        JsonObject ob = arr.getJsonObject(1);
        assertNotNull(arr);
        assertEquals(1, ob.size());
        assertEquals(JsonValue.TRUE, ob.get("a"));
    }    
}
