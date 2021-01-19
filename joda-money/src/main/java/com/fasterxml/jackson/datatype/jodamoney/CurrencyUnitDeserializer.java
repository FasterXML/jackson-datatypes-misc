package com.fasterxml.jackson.datatype.jodamoney;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import org.joda.money.CurrencyUnit;

public class CurrencyUnitDeserializer extends StdScalarDeserializer<CurrencyUnit>
{
    public CurrencyUnitDeserializer() {
        super(CurrencyUnit.class);
    }

    @Override
    public CurrencyUnit deserialize(final JsonParser p,
            final DeserializationContext context)
        throws JacksonException
    {
        final String currencyCode = p.getValueAsString();

        // TODO: instead of leaking `IllegalCurrencyException` (an `IllegalArgumentException`)
        //  catch and rethrow as suitable Jackson exception with extra info
        return CurrencyUnit.of(currencyCode);
    }
}
