package tools.jackson.datatype.jsonp;

import java.util.Collections;

import jakarta.json.*;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for binary (byte[]) content embedded in the token stream, which is
 * exposed as Base64 encoded text (see [issue#5]). Verifies Array and Object
 * positions behave the same way.
 */
public class EmbeddedBinaryTest extends TestBase
{
    private final ObjectMapper MAPPER = newMapper();

    // 3 bytes that Base64-encode to "AQID"
    private final static byte[] BINARY = new byte[] { 1, 2, 3 };

    private final static String BINARY_B64 = "AQID";

    @Test
    public void testBinaryAsObjectValue() throws Exception
    {
        JsonValue v = MAPPER.convertValue(Collections.singletonMap("k", BINARY), JsonValue.class);
        assertEquals(JsonValue.ValueType.OBJECT, v.getValueType());
        assertEquals(BINARY_B64, v.asJsonObject().getString("k"));
    }

    @Test
    public void testBinaryAsArrayValue() throws Exception
    {
        JsonValue v = MAPPER.convertValue(Collections.singletonList(BINARY), JsonValue.class);
        assertEquals(JsonValue.ValueType.ARRAY, v.getValueType());
        assertEquals(BINARY_B64, v.asJsonArray().getString(0));
    }

    @Test
    public void testBinaryInArrayWithinObject() throws Exception
    {
        JsonValue v = MAPPER.convertValue(
                Collections.singletonMap("k", Collections.singletonList(BINARY)),
                JsonValue.class);
        assertEquals(BINARY_B64, v.asJsonObject().getJsonArray("k").getString(0));
    }

    @Test
    public void testBinaryInObjectWithinArray() throws Exception
    {
        JsonValue v = MAPPER.convertValue(
                Collections.singletonList(Collections.singletonMap("k", BINARY)),
                JsonValue.class);
        assertEquals(BINARY_B64, v.asJsonArray().getJsonObject(0).getString("k"));
    }

    // Also verify it works when the declared target is the Array type itself
    @Test
    public void testBinaryAsJsonArray() throws Exception
    {
        JsonArray a = MAPPER.convertValue(Collections.singletonList(BINARY), JsonArray.class);
        assertEquals(1, a.size());
        assertEquals(BINARY_B64, a.getString(0));
    }
}
