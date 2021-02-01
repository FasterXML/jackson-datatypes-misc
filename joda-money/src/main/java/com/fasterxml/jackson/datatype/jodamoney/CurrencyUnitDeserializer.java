package com.fasterxml.jackson.datatype.jodamoney;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

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
            final DeserializationContext ctxt)
    {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            final String currencyCode = p.getText();
            try {
                return CurrencyUnit.of(currencyCode);
            } catch (Exception e) {
                return (CurrencyUnit) ctxt.handleWeirdStringValue(handledType(), currencyCode,
                        e.getMessage());
            }
        }
        return (CurrencyUnit) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
                "Expected a `JsonToken.VALUE_STRING`, got `JsonToken.%s`",
                p.currentToken());
    }
}
