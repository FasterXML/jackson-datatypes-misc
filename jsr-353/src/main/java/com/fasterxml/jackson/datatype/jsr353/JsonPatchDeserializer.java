package com.fasterxml.jackson.datatype.jsr353;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.type.LogicalType;

import javax.json.JsonPatch;
import javax.json.spi.JsonProvider;
import java.io.IOException;

public class JsonPatchDeserializer extends StdDeserializer<JsonPatch> {
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
    public JsonPatch deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() != JsonToken.START_ARRAY) {
            throw InvalidFormatException.from(p, "JSON patch has to be an array of objects", p.getText(),
                handledType());
        }
        return provider.createPatch(jsonValueDeser._deserializeArray(p, ctxt));
    }

    private final static JsonProvider provider = JsonProvider.provider();
}
