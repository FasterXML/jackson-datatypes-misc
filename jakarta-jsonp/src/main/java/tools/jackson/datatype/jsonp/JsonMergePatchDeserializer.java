package tools.jackson.datatype.jsonp;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.type.LogicalType;

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
