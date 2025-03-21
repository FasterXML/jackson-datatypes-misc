package com.fasterxml.jackson.datatype.jsr353;

import javax.json.*;
import javax.json.JsonValue.ValueType;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class JsonValueDeserializationTest extends TestBase
{
    // for [datatype-jsr353#19]
    static class ObjectImpl {
        JsonValue obj1;
        JsonValue obj2;

        @ConstructorProperties({"obj1", "obj2"})
        public ObjectImpl(JsonValue obj1, JsonValue obj2) {
            this.obj1 = obj1;
            this.obj2 = obj2;
        }
    }

    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testSimpleArray() throws Exception
    {
        final String JSON = "[1,true,\"foo\"]";
        JsonValue v = MAPPER.readValue(JSON, JsonValue.class);
        assertTrue(v instanceof JsonArray);
        JsonArray a = (JsonArray) v;
        assertEquals(3, a.size());
        assertTrue(a.get(0) instanceof JsonNumber);
        assertSame(JsonValue.TRUE, a.get(1));
        assertTrue(a.get(2) instanceof JsonString);

        // also, should work with explicit type
        JsonArray array = MAPPER.readValue(JSON, JsonArray.class);
        assertEquals(3, array.size());

        // and round-tripping ought to be ok:
        assertEquals(JSON, serializeAsString(v));
    }

    @Test
    public void testNestedArray() throws Exception
    {
        final String JSON = "[1,[false,45],{\"foo\":13}]";
        JsonValue v = MAPPER.readValue(JSON, JsonValue.class);
        assertTrue(v instanceof JsonArray);
        JsonArray a = (JsonArray) v;
        assertEquals(3, a.size());
        assertTrue(a.get(0) instanceof JsonNumber);
        assertTrue(a.get(1) instanceof JsonArray);
        assertTrue(a.get(2) instanceof JsonObject);

        // and round-tripping ought to be ok:
        assertEquals(JSON, serializeAsString(v));
    }

    @Test
    public void testSimpleObject() throws Exception
    {
        final String JSON = "{\"a\":12.5,\"b\":\"Text\"}";
        JsonValue v = MAPPER.readValue(JSON, JsonValue.class);
        assertTrue(v instanceof JsonObject);
        JsonObject ob = (JsonObject) v;
        assertEquals(2, ob.size());

        assertTrue(ob.get("a") instanceof JsonNumber);
        assertEquals(12.5, ((JsonNumber) ob.get("a")).doubleValue());
        assertTrue(ob.get("b") instanceof JsonString);
        assertEquals("Text", ((JsonString) ob.get("b")).getString());

        // also, should work with explicit type
        ob = MAPPER.readValue(JSON, JsonObject.class);
        assertEquals(2, ob.size());

        // and round-tripping ought to be ok:
        assertEquals(JSON, serializeAsString(v));
    }

    @Test
    public void testNestedObject() throws Exception
    {
        final String JSON = "{\"array\":[1,2],\"obj\":{\"first\":true}}";
        JsonValue v = MAPPER.readValue(JSON, JsonValue.class);
        assertTrue(v instanceof JsonObject);
        JsonObject ob = (JsonObject) v;
        assertEquals(2, ob.size());

        assertTrue(ob.get("array") instanceof JsonArray);
        assertEquals(2, ((JsonArray) ob.get("array")).size());
        assertTrue(ob.get("obj") instanceof JsonObject);
        assertEquals(1, ((JsonObject) ob.get("obj")).size());

        // also, should work with explicit type
        ob = MAPPER.readValue(JSON, JsonObject.class);
        assertEquals(2, ob.size());

        // and round-tripping ought to be ok:
        assertEquals(JSON, serializeAsString(v));
    }

    // for [datatype-jsr353#5]
    @Test
    public void testBinaryNode() throws Exception
    {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("b", new byte[1]);
        JsonValue v = MAPPER.convertValue(root, JsonValue.class);
        assertNotNull(v);
        assertEquals(ValueType.OBJECT, v.getValueType());
        JsonValue v2 = ((javax.json.JsonObject) v).get("b");
        assertNotNull(v2);
        assertEquals(ValueType.STRING, v2.getValueType());
        String str = ((JsonString) v2).getString();
        assertEquals("AA==", str); // single zero byte
    }

    // for [datatype-jsr353#16]
    @Test
    public void testNullNode() throws Exception
    {
        final String serializedNull = MAPPER.writeValueAsString(JsonValue.NULL);
        assertEquals("null", serializedNull);
        final JsonValue deserializedNull = MAPPER.readValue(serializedNull, JsonValue.class);
        assertEquals(JsonValue.NULL, deserializedNull);
    }

    // for [datatype-jsr353#19]
    @Test
    public void testConstructorProperties() throws Exception
    {
        ObjectImpl ob = MAPPER.readValue("{\"obj1\":{}}", ObjectImpl.class);
        assertTrue(ob.obj1 instanceof JsonObject);
        assertNull(ob.obj2);

        ObjectImpl ob2 = MAPPER.readValue("{\"obj2\":null}", ObjectImpl.class);
        assertNull(ob2.obj1);
        assertSame(JsonValue.NULL, ob2.obj2);
    }

    @Test
    public void testBigInteger() throws Exception
    {
        final String JSON = "[2e308]";
        JsonValue v = MAPPER.readValue(JSON, JsonValue.class);
        assertTrue(v instanceof JsonArray);
        JsonArray a = (JsonArray) v;
        assertEquals(1, a.size());
        assertTrue(a.get(0) instanceof JsonNumber);
        assertEquals(new BigDecimal("2e308").toBigInteger(), ((JsonNumber) a.get(0)).bigIntegerValue());


        // also, should work with explicit type
        JsonArray array = MAPPER.readValue(JSON, JsonArray.class);
        assertEquals(1, array.size());

        // and round-tripping ought to be ok:
        assertEquals("[2E+308]", serializeAsString(v));
    }

    @Test
    public void testDouble() throws Exception
    {
        JsonValue val = MAPPER.readValue("{\"val\":0.5}", JsonValue.class);
        JsonObject jsonObject = val.asJsonObject();
        JsonNumber jsonNumber = jsonObject.getJsonNumber("val");
        assertEquals(0.5d, jsonNumber.doubleValue());
        assertEquals(new BigDecimal(0.5d), jsonNumber.numberValue());
    }
}
