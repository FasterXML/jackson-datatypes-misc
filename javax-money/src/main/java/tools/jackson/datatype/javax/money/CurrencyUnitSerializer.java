package tools.jackson.datatype.javax.money;

import javax.money.CurrencyUnit;

import org.apiguardian.api.API;

import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.ser.std.StdScalarSerializer;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(status = MAINTAINED)
public final class CurrencyUnitSerializer extends StdScalarSerializer<CurrencyUnit>
{
    CurrencyUnitSerializer() {
        super(CurrencyUnit.class);
    }

    @Override
    public void serialize(final CurrencyUnit value, final JsonGenerator generator,
            final SerializationContext ctxt)
    {
        generator.writeString(value.getCurrencyCode());
    }

    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType hint) {
        visitor.expectStringFormat(hint);
    }

}
