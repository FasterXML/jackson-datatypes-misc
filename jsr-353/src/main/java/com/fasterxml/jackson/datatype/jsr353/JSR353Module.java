package com.fasterxml.jackson.datatype.jsr353;

import javax.json.*;
import javax.json.spi.JsonProvider;
import java.util.Collections;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.StdConverter;

public class JSR353Module extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    protected final JsonBuilderFactory _builderFactory;

    public JSR353Module() {
        this(JsonProvider.provider());
    }

    /**
     * @since 2.13
     */
    @SuppressWarnings("serial")
    public JSR353Module(JsonProvider jsonProvider)
    {
        super(PackageVersion.VERSION);

        _builderFactory = jsonProvider.createBuilderFactory(Collections.<String, Object>emptyMap());
        final JsonValueDeserializer jsonValueDeser = new JsonValueDeserializer(JsonValue.class, _builderFactory);
        final JsonPatchDeserializer jsonPatchDeser = new JsonPatchDeserializer(jsonValueDeser);
        final JsonMergePatchDeserializer jsonMergePatchDeser = new JsonMergePatchDeserializer(jsonValueDeser);

        addSerializer(JsonValue.class, new JsonValueSerializer());
        // Since 2.14.2
        addSerializer(JsonPatch.class, new JsonPatchSerializer());
        addSerializer(JsonMergePatch.class, new JsonMergePatchSerializer());

        setDeserializers(new SimpleDeserializers() {
            @Override
            public JsonDeserializer<?> findBeanDeserializer(
                    JavaType type,
                    DeserializationConfig config,
                    BeanDescription beanDesc
            ) {
                if (type.isTypeOrSubTypeOf(JsonValue.class)) {
                    if (type.hasRawClass(JsonValue.class)) {
                        return jsonValueDeser;
                    }
                    return new JsonValueDeserializer(type.getRawClass(), _builderFactory);
                }
                if (JsonPatch.class.isAssignableFrom(type.getRawClass())) {
                    return jsonPatchDeser;
                }
                if (JsonMergePatch.class.isAssignableFrom(type.getRawClass())) {
                    return jsonMergePatchDeser;
                }
                return null;
            }

            @Override
            public JsonDeserializer<?> findCollectionDeserializer(
                    CollectionType type,
                    DeserializationConfig config,
                    BeanDescription beanDesc,
                    TypeDeserializer elementTypeDeserializer,
                    JsonDeserializer<?> elementDeserializer
            ) {
                if (type.hasRawClass(JsonArray.class)) {
                    return new JsonValueDeserializer(type.getRawClass(), _builderFactory);
                }
                return null;
            }

            @Override
            public JsonDeserializer<?> findMapDeserializer(
                    MapType type,
                    DeserializationConfig config,
                    BeanDescription beanDesc,
                    KeyDeserializer keyDeserializer,
                    TypeDeserializer elementTypeDeserializer,
                    JsonDeserializer<?> elementDeserializer
            ) {
                if (type.hasRawClass(JsonObject.class)) {
                    return new JsonValueDeserializer(type.getRawClass(), _builderFactory);
                }
                return null;
            }

            @Override // since 2.11
            public boolean hasDeserializerFor(DeserializationConfig config, Class<?> valueType) {
                return JsonValue.class.isAssignableFrom(valueType) ||
                        JsonPatch.class.isAssignableFrom(valueType) ||
                        JsonMergePatch.class.isAssignableFrom(valueType);
            }
        });
    }

    /**
     * @since 2.14.2
     */
    static class JsonPatchSerializer extends StdDelegatingSerializer
    {
        private static final long serialVersionUID = 1L;

        public JsonPatchSerializer() {
            super(new PatchConverter());
        }

        protected JsonPatchSerializer(Converter<Object,?> converter,
                JavaType delegateType, JsonSerializer<?> delegateSerialize) {
            super(converter, delegateType, delegateSerialize);
        }

        @Override
        protected StdDelegatingSerializer withDelegate(Converter<Object,?> converter,
                JavaType delegateType, JsonSerializer<?> delegateSerializer) {
            return new JsonPatchSerializer(converter, delegateType, delegateSerializer);
        }

        static class PatchConverter
            extends StdConverter<JsonPatch,JsonArray>
        {
            @Override
            public JsonArray convert(JsonPatch value) {
                return value.toJsonArray();
            }
        }
    }

    /**
     * @since 2.14.2
     */
    static class JsonMergePatchSerializer extends StdDelegatingSerializer
    {
        private static final long serialVersionUID = 1L;

        public JsonMergePatchSerializer() {
            super(new MergePatchConverter());
        }

        protected JsonMergePatchSerializer(Converter<Object,?> converter,
                JavaType delegateType, JsonSerializer<?> delegateSerialize) {
            super(converter, delegateType, delegateSerialize);
        }

        @Override
        protected StdDelegatingSerializer withDelegate(Converter<Object,?> converter,
                JavaType delegateType, JsonSerializer<?> delegateSerializer) {
            return new JsonMergePatchSerializer(converter, delegateType, delegateSerializer);
        }

        static class MergePatchConverter
            extends StdConverter<JsonMergePatch, JsonValue>
        {
            @Override
            public JsonValue convert(JsonMergePatch value) {
                return value.toJsonValue();
            }
        }
    }
}
