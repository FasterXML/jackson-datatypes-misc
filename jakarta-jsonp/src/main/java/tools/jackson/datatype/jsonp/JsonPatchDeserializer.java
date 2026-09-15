package tools.jackson.datatype.jsonp;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
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
        // 09-Sep-2026, pjfanning: [datatypes-misc#99] Verify it IS an Array; otherwise
        //    `_deserializeArray()` reads past the end of the document and fails with
        //    a bare NPE on the resulting `null` token
        if (p.currentToken() != JsonToken.START_ARRAY) {
            return (JsonPatch) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
                    "JSON patch has to be an array of objects");
        }
        return provider.createPatch(jsonValueDeser._deserializeArray(p, ctxt));
    }

    private final static JsonProvider provider = JsonProvider.provider();
}
