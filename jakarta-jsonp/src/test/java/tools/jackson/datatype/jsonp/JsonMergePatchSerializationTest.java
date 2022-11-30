package tools.jackson.datatype.jsonp;

import tools.jackson.databind.ObjectMapper;

import jakarta.json.JsonMergePatch;

public class JsonMergePatchSerializationTest extends TestBase
{
    private static final ObjectMapper MAPPER = newMapper();

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
