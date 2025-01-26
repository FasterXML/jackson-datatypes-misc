package tools.jackson.datatype.jsonorg;

import java.math.BigDecimal;

import tools.jackson.databind.*;

import org.json.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleReadTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testReadObject() throws Exception
    {
        JSONObject ob = MAPPER.readValue("{\"a\":{\"b\":3}, \"c\":[9, -4], \"d\":null, \"e\":true}",
                JSONObject.class);
        assertEquals(4, ob.length());
        JSONObject ob2 = ob.getJSONObject("a");
        assertEquals(1, ob2.length());
        assertEquals(3, ob2.getInt("b"));
        JSONArray array = ob.getJSONArray("c");
        assertEquals(2, array.length());
        assertEquals(9, array.getInt(0));
        assertEquals(-4, array.getInt(1));
        assertTrue(ob.isNull("d"));
        assertTrue(ob.getBoolean("e"));
    }

    @Test
    public void testReadArray() throws Exception
    {
        JSONArray array = MAPPER.readValue("[null, 13, false, 1.25, \"abc\", {\"a\":13}, [ ] ]",
                JSONArray.class);
        assertEquals(7, array.length());
        assertTrue(array.isNull(0));
        assertEquals(13, array.getInt(1));
        assertFalse(array.getBoolean(2));
        assertEquals(Double.valueOf(1.25), array.getDouble(3));
        assertEquals("abc", array.getString(4));
        JSONObject ob = array.getJSONObject(5);
        assertEquals(1, ob.length());
        assertEquals(13, ob.getInt("a"));
        JSONArray array2 = array.getJSONArray(6);
        assertEquals(0, array2.length());
    }

    @Test
    public void testBigInteger() throws Exception
    {
        JSONObject val = MAPPER.readValue("{\"val\":2e308}", JSONObject.class);
        assertEquals(new BigDecimal("2e308").toBigInteger(), val.getBigInteger("val"));
    }

    @Test
    public void testBigIntegerArray() throws Exception
    {
        JSONArray array = MAPPER.readValue("[2e308]", JSONArray.class);
        assertEquals(1, array.length());
        assertEquals(new BigDecimal("2e308").toBigInteger(), array.getBigInteger(0));
    }

    @Test
    public void testDouble() throws Exception
    {
        JSONObject val = MAPPER.readValue("{\"val\": 0.5}", JSONObject.class);
        assertEquals(0.5d, val.getDouble("val"));
    }
}
