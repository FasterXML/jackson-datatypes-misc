package com.fasterxml.jackson.datatype.jsr353;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import javax.json.Json;
import javax.json.JsonPatch;
import java.io.IOException;

public class JsonPatchDeserializer extends StdDeserializer<JsonPatch> {

    protected final JsonValueDeserializer jsonValueDeser;

    public JsonPatchDeserializer(JsonValueDeserializer jsonValueDeser) {
        super(JsonPatch.class);
        this.jsonValueDeser = jsonValueDeser;
    }

    @Override
    public JsonPatch deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return Json.createPatch(jsonValueDeser._deserializeArray(p, ctxt));
    }

}
