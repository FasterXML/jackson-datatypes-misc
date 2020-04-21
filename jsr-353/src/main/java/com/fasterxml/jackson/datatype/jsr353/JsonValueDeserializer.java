package com.fasterxml.jackson.datatype.jsr353;

import java.io.IOException;

import javax.json.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

public class JsonValueDeserializer extends StdDeserializer<JsonValue> {
    private static final long serialVersionUID = 1L;

    protected final JsonBuilderFactory _builderFactory;

    public JsonValueDeserializer(JsonBuilderFactory bf) {
        super(JsonValue.class);
        _builderFactory = bf;
    }

    @Override
    public JsonValue deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException
    {
        switch (p.getCurrentToken()) {
            case START_OBJECT:
                return _deserializeObject(p, ctxt);
            case START_ARRAY:
                return _deserializeArray(p, ctxt);
            default:
                return _deserializeScalar(p, ctxt);
        }
    }

    @Override
    public JsonValue getNullValue(final DeserializationContext ctxt) {
        return JsonValue.NULL;
    }

    @Override
    public Object deserializeWithType(JsonParser p,
            DeserializationContext ctxt, TypeDeserializer typeDeser)
        throws IOException
    {
        // we will always serialize using wrapper-array; approximated by claiming it's scalar
        return typeDeser.deserializeTypedFromScalar(p, ctxt);
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    protected JsonObject _deserializeObject(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        JsonObjectBuilder b = _builderFactory.createObjectBuilder();
        while (p.nextToken() != JsonToken.END_OBJECT) {
            String name = p.getCurrentName();
            JsonToken t = p.nextToken();
            switch (t) {
                case START_ARRAY:
                    b.add(name, _deserializeArray(p, ctxt));
                    break;
                case START_OBJECT:
                    b.add(name, _deserializeObject(p, ctxt));
                    break;
                case VALUE_FALSE:
                    b.add(name, false);
                    break;
                case VALUE_TRUE:
                    b.add(name, true);
                    break;
                case VALUE_NULL:
                    b.addNull(name);
                    break;
                case VALUE_NUMBER_FLOAT:
                    if (p.getNumberType() == NumberType.BIG_DECIMAL) {
                        b.add(name, p.getDecimalValue());
                    } else {
                        b.add(name, p.getDoubleValue());
                    }
                    break;
                case VALUE_NUMBER_INT:
                    // very cumbersome... but has to be done
                    switch (p.getNumberType()) {
                        case LONG:
                            b.add(name, p.getLongValue());
                            break;
                        case INT:
                            b.add(name, p.getIntValue());
                            break;
                        default:
                            b.add(name, p.getBigIntegerValue());
                    }
                    break;
                case VALUE_STRING:
                    b.add(name, p.getText());
                    break;
                case VALUE_EMBEDDED_OBJECT: {
                    // 26-Nov-2014, tatu: As per [issue#5], should be able to support
                    //   binary data as Base64 embedded text
                    Object ob = p.getEmbeddedObject();
                    if (ob instanceof byte[]) {
                        String b64 = ctxt.getBase64Variant().encode((byte[]) ob, false);
                        b.add(name, b64);
                        break;
                    }
                }
                default:
                    return (JsonObject) ctxt.handleUnexpectedToken(JsonObject.class, p);
            }
        }
        return b.build();
    }

    protected JsonArray _deserializeArray(JsonParser p, DeserializationContext ctxt)
            throws IOException
    {
        JsonArrayBuilder b = _builderFactory.createArrayBuilder();
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
            switch (t) {
                case START_ARRAY:
                    b.add(_deserializeArray(p, ctxt));
                    break;
                case START_OBJECT:
                    b.add(_deserializeObject(p, ctxt));
                    break;
                case VALUE_FALSE:
                    b.add(false);
                    break;
                case VALUE_TRUE:
                    b.add(true);
                    break;
                case VALUE_NULL:
                    b.addNull();
                    break;
                case VALUE_NUMBER_FLOAT:
                    if (p.getNumberType() == NumberType.BIG_DECIMAL) {
                        b.add(p.getDecimalValue());
                    } else {
                        b.add(p.getDoubleValue());
                    }
                    break;
                case VALUE_NUMBER_INT:
                    // very cumbersome... but has to be done
                    switch (p.getNumberType()) {
                        case LONG:
                            b.add(p.getLongValue());
                            break;
                        case INT:
                            b.add(p.getIntValue());
                            break;
                        default:
                            b.add(p.getBigIntegerValue());
                    }
                    break;
                case VALUE_STRING:
                    b.add(p.getText());
                    break;
                default:
                    return (JsonArray) ctxt.handleUnexpectedToken(JsonArray.class, p);
            }
        }
        return b.build();
    }

    protected JsonValue _deserializeScalar(JsonParser p, DeserializationContext ctxt)
            throws IOException
    {
        switch (p.getCurrentToken()) {
            case VALUE_EMBEDDED_OBJECT:
                // Not sure what to do with it -- could convert byte[] into Base64 encoded
                // if we wanted to... ?
                return (JsonValue) ctxt.handleUnexpectedToken(JsonValue.class, p);
            case VALUE_FALSE:
                return JsonValue.FALSE;
            case VALUE_TRUE:
                return JsonValue.TRUE;
            case VALUE_NULL:
                return JsonValue.NULL;
            case VALUE_NUMBER_FLOAT:
                // very cumbersome... but has to be done
            {
                JsonArrayBuilder b = _builderFactory.createArrayBuilder();
                if (p.getNumberType() == NumberType.BIG_DECIMAL) {
                    return b.add(p.getDecimalValue()).build().get(0);
                }
                return b.add(p.getDoubleValue()).build().get(0);
            }
            case VALUE_NUMBER_INT:
                // very cumbersome... but has to be done
            {
                JsonArrayBuilder b = _builderFactory.createArrayBuilder();
                switch (p.getNumberType()) {
                    case LONG:
                        return b.add(p.getLongValue()).build().get(0);
                    case INT:
                        return b.add(p.getIntValue()).build().get(0);
                    default:
                        return b.add(p.getBigIntegerValue()).build().get(0);
                }
            }
            case VALUE_STRING:
                return _builderFactory.createArrayBuilder().add(p.getText()).build().get(0);
            default: // errors, should never get here
//        case END_ARRAY:
//        case END_OBJECT:
//        case FIELD_NAME:
//        case NOT_AVAILABLE:
//        case START_ARRAY:
//        case START_OBJECT:
                return (JsonValue) ctxt.handleUnexpectedToken(JsonValue.class, p);
        }
    }
}
