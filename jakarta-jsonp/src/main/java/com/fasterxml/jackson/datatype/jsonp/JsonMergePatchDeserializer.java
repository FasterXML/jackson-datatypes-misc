package com.fasterxml.jackson.datatype.jsonp;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;

import jakarta.json.Json;
import jakarta.json.JsonMergePatch;

public class JsonMergePatchDeserializer extends StdDeserializer<JsonMergePatch>
{
    protected final JsonValueDeserializer jsonValueDeser;

    public JsonMergePatchDeserializer(JsonValueDeserializer jsonValueDeser) {
        super(JsonMergePatch.class);
        this.jsonValueDeser = jsonValueDeser;
    }

    @Override
    public LogicalType logicalType() {
        return jsonValueDeser.logicalType();
    }
    
    @Override
    public JsonMergePatch deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        return Json.createMergePatch(jsonValueDeser._deserializeObject(p, ctxt));
    }
}
