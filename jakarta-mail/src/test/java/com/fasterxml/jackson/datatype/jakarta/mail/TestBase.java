package com.fasterxml.jackson.datatype.jakarta.mail;

import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

public abstract class TestBase
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

    private final static JakartaMailModule MODULE = new JakartaMailModule();

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
}
