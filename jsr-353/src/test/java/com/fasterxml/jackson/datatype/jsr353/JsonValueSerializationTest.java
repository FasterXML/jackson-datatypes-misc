package com.fasterxml.jackson.datatype.jsr353;

import java.math.BigDecimal;

import javax.json.*;

public class JsonValueSerializationTest extends TestBase
{
    public void testSimpleArray() throws Exception
    {
        JsonArray arr = arrayBuilder()
                .add(true)
                .addNull()
                .add(123)
                .add(new BigDecimal("15.25"))
                .build();
        assertEquals("[true,null,123,15.25]", serializeAsString(arr));
    }

    public void testNestedArray() throws Exception
    {
        JsonArray arr = arrayBuilder()
                .add(1)
                .add(arrayBuilder().add(false).add(45).build())
                .add(objectBuilder().add("foo", 13).build())
                .build();
        assertEquals("[1,[false,45],{\"foo\":13}]", serializeAsString(arr));
    }
    
    public void testSimpleObject() throws Exception
    {
        JsonObject ob = objectBuilder()
                .add("a", 123)
                .add("b", "Text")
                .build();
        // not sure if order is guaranteed but:
        assertEquals("{\"a\":123,\"b\":\"Text\"}", serializeAsString(ob));
    }

    public void testNestedObject() throws Exception
    {
        JsonObject ob = objectBuilder()
                .add("array", arrayBuilder().add(1).add(2))
                .add("obj", objectBuilder().add("first", true))
                .build();
        // not sure if order is guaranteed but:
        assertEquals("{\"array\":[1,2],\"obj\":{\"first\":true}}", serializeAsString(ob));
    }
}
