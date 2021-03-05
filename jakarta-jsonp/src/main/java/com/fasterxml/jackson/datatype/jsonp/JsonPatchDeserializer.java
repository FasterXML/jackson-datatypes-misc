package com.fasterxml.jackson.datatype.jsonp;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;

import jakarta.json.Json;
import jakarta.json.JsonPatch;

public class JsonPatchDeserializer extends StdDeserializer<JsonPatch>
{
    private static final long serialVersionUID = 1L;

    protected final JsonValueDeserializer jsonValueDeser;

    public JsonPatchDeserializer(JsonValueDeserializer jsonValueDeser) {
        super(JsonPatch.class);
        this.jsonValueDeser = jsonValueDeser;
    }

    @Override
    public LogicalType logicalType() {
        return jsonValueDeser.logicalType();
    }
    
    @Override
    public JsonPatch deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        return Json.createPatch(jsonValueDeser._deserializeArray(p, ctxt));
    }
}
