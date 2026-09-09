package tools.jackson.datatype.jsonorg;

import tools.jackson.core.*;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.util.ClassUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectDeserializer extends StdDeserializer<JSONObject>
{
    public final static JSONObjectDeserializer instance = new JSONObjectDeserializer();

    public JSONObjectDeserializer()
    {
        super(JSONObject.class);
    }

    @Override
    public LogicalType logicalType() {
        return LogicalType.Untyped;
    }

    @Override
    public JSONObject deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        } else if (t != JsonToken.PROPERTY_NAME) {
            // 09-Sep-2026, pjfanning: [datatypes-misc#90] Need to verify it IS an Object (like
            //    `JSONArrayDeserializer` does for Arrays);
            //    otherwise we would quietly return an empty JSONObject and leave the parser
            //    pointing in the middle of the (non-Object) value
            return (JSONObject) ctxt.handleUnexpectedToken(getValueType(ctxt), t, p,
                    "Unexpected token (%s), expected START_OBJECT for %s value",
                    t, ClassUtil.nameOf(handledType()));
        }
        JSONObject ob = new JSONObject();
        for (; t == JsonToken.PROPERTY_NAME; t = p.nextToken()) {
            String fieldName = p.currentName();
            t = p.nextToken();
            try {
                switch (t) {
                case START_ARRAY:
                    ob.put(fieldName, JSONArrayDeserializer.instance.deserialize(p, ctxt));
                    continue;
                case START_OBJECT:
                    ob.put(fieldName, deserialize(p, ctxt));
                    continue;
                case VALUE_STRING:
                    ob.put(fieldName, p.getString());
                    continue;
                case VALUE_NULL:
                    ob.put(fieldName, JSONObject.NULL);
                    continue;
                case VALUE_TRUE:
                    ob.put(fieldName, Boolean.TRUE);
                    continue;
                case VALUE_FALSE:
                    ob.put(fieldName, Boolean.FALSE);
                    continue;
                case VALUE_NUMBER_INT:
                    ob.put(fieldName, p.getNumberValue());
                    continue;
                case VALUE_NUMBER_FLOAT:
                    ob.put(fieldName, p.getDecimalValue());
                    continue;
                case VALUE_EMBEDDED_OBJECT:
                    ob.put(fieldName, p.getEmbeddedObject());
                    continue;
                default:
                }
            } catch (JSONException e) {
                throw ctxt.instantiationException(handledType(), e);
            }
            return (JSONObject) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
        }
        return ob;
    }
}
