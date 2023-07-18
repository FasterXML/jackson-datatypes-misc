package tools.jackson.datatype.jsr353;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.type.LogicalType;

import javax.json.JsonPatch;
import javax.json.spi.JsonProvider;

public class JsonPatchDeserializer extends StdDeserializer<JsonPatch>
{
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
    public JsonPatch deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        if (p.currentToken() != JsonToken.START_ARRAY) {
            throw InvalidFormatException.from(p, "JSON patch has to be an array of objects", p.getText(),
                handledType());
        }
        return provider.createPatch(jsonValueDeser._deserializeArray(p, ctxt));
    }

    private final static JsonProvider provider = JsonProvider.provider();
}
