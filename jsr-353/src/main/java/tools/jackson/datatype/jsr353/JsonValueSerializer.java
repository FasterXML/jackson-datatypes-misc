package tools.jackson.datatype.jsr353;

import java.util.Map;

import javax.json.*;

import tools.jackson.core.*;
import tools.jackson.core.type.WritableTypeId;
import tools.jackson.databind.SerializerProvider;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

public class JsonValueSerializer extends StdSerializer<JsonValue>
{
    public JsonValueSerializer() {
        super(JsonValue.class);
    }

    /*
    /**********************************************************
    /* Public API
    /**********************************************************
     */
    
    @Override
    public void serialize(JsonValue value, JsonGenerator g, SerializerProvider provider)
        throws JacksonException
    {
        switch (value.getValueType()) {
        case ARRAY:
            g.writeStartArray();
            serializeArrayContents((JsonArray) value, g, provider);
            g.writeEndArray();
            break;
        case OBJECT:
            g.writeStartObject(value);
            serializeObjectContents((JsonObject) value, g, provider);
            g.writeEndObject();
            break;
        default: // value type of some kind (scalar)
            serializeScalar(value, g, provider);
        }
    }

    @Override
    public void serializeWithType(JsonValue value, JsonGenerator g, SerializerProvider ctxt,
            TypeSerializer typeSer)
        throws JacksonException
    {
        g.assignCurrentValue(value);
        // 25-Jul-2017, tatu: This may look wrong, but since we don't really know impl
        //   classes we need to demote type to generic one, first: and as importantly,
        //   need to claim that we don't really know shape to use (since that can vary
        //   a lot). Safest way (and backwards compatible) is to claim it's scalar...
        //   Not fully correct, but has to work for now.
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, JsonValue.class, JsonToken.VALUE_EMBEDDED_OBJECT));

        serialize(value, g, ctxt);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }

    /*
    /**********************************************************
    /* Internal methods
    /**********************************************************
     */

    protected void serializeScalar(JsonValue value,
            JsonGenerator g, SerializerProvider provider)
        throws JacksonException
    {
        switch (value.getValueType()) {
        case FALSE:
            g.writeBoolean(false);
            break;
        case NULL: // hmmh. explicit nulls... I guess we may get them
            g.writeNull();
            break;
        case NUMBER:
            {
                JsonNumber num = (JsonNumber) value;
                if (num.isIntegral()) {
                    g.writeNumber(num.longValue());
                } else {
                    // 26-Feb-2013, tatu: Apparently no way to know if we need heavy BigDecimal
                    //   or not. Let's err on side of correct-if-slow to avoid losing precision.
                    g.writeNumber(num.bigDecimalValue());
                }
            }
            break;
        case STRING:
            g.writeString(((JsonString) value).getString());
            break;
        case TRUE:
            g.writeBoolean(true);
            break;
        default:
            break;
//        default: // should never happen as array, object should not be called
//            throw new IllegalStateException("Unrecognized scalar JsonValue type: "+value.getClass().getName();
        }        
    }

    protected void serializeArrayContents(JsonArray values,
            JsonGenerator g, SerializerProvider provider)
        throws JacksonException
    {
        if (!values.isEmpty()) {
            for (JsonValue value : values) {
                serialize(value, g, provider);
            }
        }
    }

    protected void serializeObjectContents(JsonObject ob,
            JsonGenerator g, SerializerProvider provider)
        throws JacksonException
    {
        if (!ob.isEmpty()) {
            for (Map.Entry<String, JsonValue> entry : ob.entrySet()) {
                g.writeName(entry.getKey());
                serialize(entry.getValue(), g, provider);
            }
        }
    }
}