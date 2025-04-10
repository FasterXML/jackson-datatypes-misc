package com.fasterxml.jackson.datatype.javax.money;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apiguardian.api.API;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.io.IOException;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(status = MAINTAINED)
public final class CurrencyUnitDeserializer extends StdScalarDeserializer<CurrencyUnit>
{
    private static final long serialVersionUID = 1L;

    public CurrencyUnitDeserializer() {
        super(CurrencyUnit.class);
    }

    @Override
    public Object deserializeWithType(final JsonParser parser, final DeserializationContext context,
            final TypeDeserializer deserializer) throws IOException {

        // effectively assuming no type information at all
        return deserialize(parser, context);
    }

    @Override
    public CurrencyUnit deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final String currencyCode = parser.getValueAsString();
        return Monetary.getCurrency(currencyCode);
    }
}
