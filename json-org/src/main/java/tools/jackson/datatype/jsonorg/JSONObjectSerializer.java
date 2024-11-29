package tools.jackson.datatype.jsonorg;

import java.util.Iterator;

import tools.jackson.core.*;
import tools.jackson.core.type.WritableTypeId;

import tools.jackson.databind.*;
import tools.jackson.databind.jsontype.TypeSerializer;

import org.json.*;

public class JSONObjectSerializer extends JSONBaseSerializer<JSONObject>
{
    public final static JSONObjectSerializer instance = new JSONObjectSerializer();

    public JSONObjectSerializer()
    {
        super(JSONObject.class);
    }

    @Override
    public boolean isEmpty(SerializationContext provider, JSONObject value) {
        return (value == null) || value.length() == 0;
    }

    @Override
    public void serialize(JSONObject value, JsonGenerator g, SerializationContext ctxt)
        throws JacksonException
    {
        g.writeStartObject(value);
        serializeContents(value, g, ctxt);
        g.writeEndObject();
    }

    @Override
    public void serializeWithType(JSONObject value, JsonGenerator g, SerializationContext ctxt,
            TypeSerializer typeSer)
        throws JacksonException
    {
        g.assignCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        serializeContents(value, g, ctxt);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    
    }

    protected void serializeContents(JSONObject value, JsonGenerator g, SerializationContext ctxt)
        throws JacksonException
    {
        Iterator<?> it = value.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            Object ob = value.opt(key);
            if (ob == null || ob == JSONObject.NULL) {
                // 28-Mar-2019, tatu: Should possibly support filter of empty/null/default values?
                g.writeNullProperty(key);
                continue;
            }
            g.writeName(key);
            Class<?> cls = ob.getClass();
            if (cls == JSONObject.class) {
                serialize((JSONObject) ob, g, ctxt);
            } else if (cls == JSONArray.class) {
                JSONArraySerializer.instance.serialize((JSONArray) ob, g, ctxt);
            } else  if (cls == String.class) {
                g.writeString((String) ob);
            } else  if (cls == Integer.class) {
                g.writeNumber(((Integer) ob).intValue());
            } else  if (cls == Long.class) {
                g.writeNumber(((Long) ob).longValue());
            } else  if (cls == Boolean.class) {
                g.writeBoolean(((Boolean) ob).booleanValue());
            } else  if (cls == Double.class) {
                g.writeNumber(((Double) ob).doubleValue());
            } else if (JSONObject.class.isAssignableFrom(cls)) { // sub-class
                serialize((JSONObject) ob, g, ctxt);
            } else if (JSONArray.class.isAssignableFrom(cls)) { // sub-class
                JSONArraySerializer.instance.serialize((JSONArray) ob, g, ctxt);
            } else {
                ctxt.writeValue(g, ob);
            }
        }
    }
}
