package com.fasterxml.jackson.datatype.jsr353;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.type.LogicalType;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonPatch;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import java.io.IOException;

public class JsonPatchDeserializer extends StdDeserializer<JsonPatch> {

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
    public JsonPatch deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() != JsonToken.START_ARRAY) {
            throw getException(p, p.getText());
        }
        final JsonArray patch = jsonValueDeser._deserializeArray(p, ctxt);
        for (final JsonValue element : patch.getValuesAs(JsonValue.class)) {
            if (element.getValueType() != ValueType.OBJECT) {
                throw getException(p, element.toString());
            }
        }
        return Json.createPatch(patch);
    }

    private InvalidFormatException getException(final JsonParser p, final String value) {
        return InvalidFormatException.from(p, "JSON patch has to be an array of objects", value, handledType());
    }
}
