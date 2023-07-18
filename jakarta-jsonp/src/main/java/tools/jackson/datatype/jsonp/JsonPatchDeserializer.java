package tools.jackson.datatype.jsonp;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.type.LogicalType;

import jakarta.json.JsonPatch;
import jakarta.json.spi.JsonProvider;

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
    public JsonPatch deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        return provider.createPatch(jsonValueDeser._deserializeArray(p, ctxt));
    }

    private final static JsonProvider provider = JsonProvider.provider();
}
