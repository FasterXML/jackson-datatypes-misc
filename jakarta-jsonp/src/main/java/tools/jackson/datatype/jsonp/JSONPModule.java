package tools.jackson.datatype.jsonp;

import java.util.Collections;

import tools.jackson.databind.*;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.module.SimpleDeserializers;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdDelegatingSerializer;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.MapType;
import tools.jackson.databind.util.Converter;
import tools.jackson.databind.util.StdConverter;
import jakarta.json.*;
import jakarta.json.spi.JsonProvider;

public class JSONPModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    protected final JsonBuilderFactory _builderFactory;

    public JSONPModule() {
        this(JsonProvider.provider());
    }

    @SuppressWarnings("serial")
    public JSONPModule(JsonProvider jsonProvider)
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

            @Override
            public boolean hasDeserializerFor(DeserializationConfig config, Class<?> valueType) {
                return JsonValue.class.isAssignableFrom(valueType) ||
                        JsonPatch.class.isAssignableFrom(valueType) ||
                        JsonMergePatch.class.isAssignableFrom(valueType);
            }
        });
    }

    static class JsonPatchSerializer extends StdDelegatingSerializer
    {
        public JsonPatchSerializer() {
            super(new PatchConverter());
        }

        protected JsonPatchSerializer(Converter<Object,?> converter,
                JavaType delegateType, ValueSerializer<?> delegateSerialize,
                BeanProperty prop) {
            super(converter, delegateType, delegateSerialize, prop);
        }

        @Override
        protected StdDelegatingSerializer withDelegate(Converter<Object,?> converter,
                JavaType delegateType, ValueSerializer<?> delegateSerializer,
                BeanProperty prop) {
            return new JsonPatchSerializer(converter, delegateType, delegateSerializer, prop);
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

    static class JsonMergePatchSerializer extends StdDelegatingSerializer
    {
        public JsonMergePatchSerializer() {
            super(new MergePatchConverter());
        }

        protected JsonMergePatchSerializer(Converter<Object,?> converter,
                JavaType delegateType, ValueSerializer<?> delegateSerialize,
                BeanProperty prop) {
            super(converter, delegateType, delegateSerialize, prop);
        }

        @Override
        protected StdDelegatingSerializer withDelegate(Converter<Object,?> converter,
                JavaType delegateType, ValueSerializer<?> delegateSerializer,
                BeanProperty prop) {
            return new JsonMergePatchSerializer(converter, delegateType, delegateSerializer, prop);
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
