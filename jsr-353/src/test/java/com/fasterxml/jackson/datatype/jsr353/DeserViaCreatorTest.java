package com.fasterxml.jackson.datatype.jsr353;

import javax.json.JsonObject;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class DeserViaCreatorTest extends TestBase
{
    static class Pojo {
        String text;
        JsonObject object;

        @JsonCreator
        public Pojo(@JsonProperty("s") String s, @JsonProperty("o") JsonObject o) {
            text = s;
            object = o;
        }
    }

    @Test
    public void testCreatorDeser() throws Exception
    {
        final ObjectMapper mapper = sharedMapper();
        Pojo p = mapper.readerFor(Pojo.class)
            .readValue( "{\"s\": \"String\", \"o\": { \"a\": 1, \"b\": \"2\" } }");
        assertNotNull(p);
        assertNotNull(p.object);

        p = mapper.readerFor(Pojo.class).readValue("{\"s\": \"String\"}");
        assertNotNull(p);
        // has to remain `null` as JsonValue.NULL is not JsonObject
        assertNull(p.object);
    }
}
