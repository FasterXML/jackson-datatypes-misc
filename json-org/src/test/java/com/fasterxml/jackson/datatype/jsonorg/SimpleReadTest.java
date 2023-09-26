package com.fasterxml.jackson.datatype.jsonorg;

import com.fasterxml.jackson.databind.*;

import org.json.*;

import java.math.BigDecimal;

public class SimpleReadTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();

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

    public void testDouble() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JsonOrgModule());

        JSONObject val = mapper.readValue("{\"val\":0.5}", JSONObject.class);
        assertEquals(0.5d, val.getDouble("val"));
        assertEquals(new BigDecimal(0.5d), val.get("val"));
    }

    public void testBigInteger() throws Exception
    {
        JSONObject val = MAPPER.readValue("{\"val\":2e308}", JSONObject.class);
        assertEquals(new BigDecimal("2e308").toBigInteger(), val.getBigInteger("val"));
    }

    public void testBigIntegerArray() throws Exception
    {
        JSONArray array = MAPPER.readValue("[2e308]", JSONArray.class);
        assertEquals(1, array.length());
        assertEquals(new BigDecimal("2e308").toBigInteger(), array.getBigInteger(0));
    }

    public void testDouble() throws Exception
    {
        JSONObject val = MAPPER.readValue("{\"val\": 0.5}", JSONObject.class);
        assertEquals(0.5d, val.getDouble("val"));
    }
}
