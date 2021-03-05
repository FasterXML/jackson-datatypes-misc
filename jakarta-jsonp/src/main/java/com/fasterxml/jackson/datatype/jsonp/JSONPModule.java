package com.fasterxml.jackson.datatype.jsonp;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jakarta.jsonp.PackageVersion;

import jakarta.json.*;
import jakarta.json.spi.JsonProvider;
import java.util.Collections;

public class JSONPModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    protected final JsonBuilderFactory _builderFactory;

    @SuppressWarnings("serial")
    public JSONPModule() {
        super(PackageVersion.VERSION); //ModuleVersion.instance.version());

        final JsonProvider jp = JsonProvider.provider();
        _builderFactory = jp.createBuilderFactory(Collections.<String, Object>emptyMap());
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
