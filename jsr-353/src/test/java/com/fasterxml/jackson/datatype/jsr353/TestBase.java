package com.fasterxml.jackson.datatype.jsr353;

import java.io.IOException;

import javax.json.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

public abstract class TestBase extends junit.framework.TestCase
{
    static class NoCheckSubTypeValidator
        extends PolymorphicTypeValidator.Base
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    private final static JSR353Module MODULE = new JSR353Module();

    private final static ObjectMapper SHARED_MAPPER = newMapper();

    protected static ObjectMapper newMapper() {
        return mapperBuilder().build();
    }

    protected static JsonMapper.Builder mapperBuilder() {
        return JsonMapper.builder()
                .addModule(MODULE);
    }

    protected static ObjectMapper sharedMapper() {
        return SHARED_MAPPER;
    }
    
    protected String serializeAsString(JsonValue node) throws IOException {
        return SHARED_MAPPER.writeValueAsString(node);
    }

    protected JsonArrayBuilder arrayBuilder() {
        return MODULE._builderFactory.createArrayBuilder();
    }

    protected JsonObjectBuilder objectBuilder() {
        return MODULE._builderFactory.createObjectBuilder();
    }
}
