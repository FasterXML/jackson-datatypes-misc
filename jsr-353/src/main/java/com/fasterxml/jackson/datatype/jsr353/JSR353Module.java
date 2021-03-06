package com.fasterxml.jackson.datatype.jsr353;

import java.util.Collections;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

import javax.json.*;
import javax.json.spi.JsonProvider;

public class JSR353Module extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    protected final JsonBuilderFactory _builderFactory;

    public JSR353Module() {
        this(JsonProvider.provider());
    }

    @SuppressWarnings("serial")
    public JSR353Module(JsonProvider jsonProvider)
    {
        super(PackageVersion.VERSION);

        _builderFactory = jsonProvider.createBuilderFactory(Collections.<String, Object>emptyMap());
        final JsonValueDeserializer jsonValueDeser = new JsonValueDeserializer(JsonValue.class, _builderFactory);
        final JsonPatchDeserializer jsonPatchDeser = new JsonPatchDeserializer(jsonValueDeser);
        final JsonMergePatchDeserializer jsonMergePatchDeser = new JsonMergePatchDeserializer(jsonValueDeser);

        addSerializer(JsonValue.class, new JsonValueSerializer());
        setDeserializers(new SimpleDeserializers() {
            @Override
            public ValueDeserializer<?> findBeanDeserializer(
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
            public ValueDeserializer<?> findCollectionDeserializer(
                    CollectionType type,
                    DeserializationConfig config,
                    BeanDescription beanDesc,
                    TypeDeserializer elementTypeDeserializer,
                    ValueDeserializer<?> elementDeserializer
            ) {
                if (type.hasRawClass(JsonArray.class)) {
                    return new JsonValueDeserializer(type.getRawClass(), _builderFactory);
                }
                return null;
            }

            @Override
            public ValueDeserializer<?> findMapDeserializer(
                    MapType type,
                    DeserializationConfig config,
                    BeanDescription beanDesc,
                    KeyDeserializer keyDeserializer,
                    TypeDeserializer elementTypeDeserializer,
                    ValueDeserializer<?> elementDeserializer
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
}
