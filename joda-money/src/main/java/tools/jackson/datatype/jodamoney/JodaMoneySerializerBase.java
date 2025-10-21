package tools.jackson.datatype.jodamoney;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;

import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

abstract class JodaMoneySerializerBase<T> extends StdSerializer<T>
{
    protected JodaMoneySerializerBase(Class<T> cls) { super(cls); }

    @Override
    public void serializeWithType(T value, JsonGenerator g, SerializationContext ctxt,
            TypeSerializer typeSer)
        throws JacksonException
    {
        g.assignCurrentValue(value);
        // NOTE: we do not actually know the exact shape (or, rather, it varies by settings
        // and so should not claim particular shape) -- but need to make sure NOT to report
        // as `Shape.OBJECT` or `Shape.ARRAY`
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, JsonToken.VALUE_STRING));
        serialize(value, g, ctxt);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }
}
