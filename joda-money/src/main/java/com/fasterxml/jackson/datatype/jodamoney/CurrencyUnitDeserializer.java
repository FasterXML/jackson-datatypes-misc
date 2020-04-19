package com.fasterxml.jackson.datatype.jodamoney;

import com.fasterxml.jackson.core.JsonParser;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import org.joda.money.CurrencyUnit;

import java.io.IOException;

public class CurrencyUnitDeserializer extends StdScalarDeserializer<CurrencyUnit>
{
    public CurrencyUnitDeserializer() {
        super(CurrencyUnit.class);
    }

    @Override
    public CurrencyUnit deserialize(final JsonParser p,
            final DeserializationContext context) throws IOException {
        final String currencyCode = p.getValueAsString();

        // TODO: error handling?
        return CurrencyUnit.of(currencyCode);
    }
}
