package com.fasterxml.jackson.datatype.jsr353;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

import javax.json.*;
import javax.json.spi.JsonProvider;
import java.util.Collections;

public class JSR353Module extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    protected final JsonBuilderFactory _builderFactory;

    @SuppressWarnings("serial")
    public JSR353Module() {
        super(PackageVersion.VERSION); //ModuleVersion.instance.version());

        JsonProvider jp = JsonProvider.provider();
        _builderFactory = jp.createBuilderFactory(Collections.<String, Object>emptyMap());
        final JsonValueDeserializer jsonValueDeser = new JsonValueDeserializer(_builderFactory);
        final JsonPatchDeserializer jsonPatchDeser = new JsonPatchDeserializer(jsonValueDeser);
        final JsonMergePatchDeserializer jsonMergePatchDeser = new JsonMergePatchDeserializer(jsonValueDeser);

        addSerializer(JsonValue.class, new JsonValueSerializer());
        setDeserializers(new SimpleDeserializers() {
            @Override
            public JsonDeserializer<?> findBeanDeserializer(
                    JavaType type,
                    DeserializationConfig config,
                    BeanDescription beanDesc
            ) {
                if (JsonValue.class.isAssignableFrom(type.getRawClass())) {
                    return jsonValueDeser;
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
                if (JsonArray.class.isAssignableFrom(type.getRawClass())) {
                    return jsonValueDeser;
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
                if (JsonObject.class.isAssignableFrom(type.getRawClass())) {
                    return jsonValueDeser;
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
