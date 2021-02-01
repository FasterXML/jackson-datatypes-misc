package com.fasterxml.jackson.datatype.jodamoney;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class MoneyDeserializer extends StdDeserializer<Money>
{
    private static final long serialVersionUID = 1L;

    public MoneyDeserializer() {
        super(Money.class);
    }

    @Override
    public LogicalType logicalType() {
        // structured, hence POJO
        return LogicalType.POJO;
    }

    @Override
    public Money deserialize(final JsonParser p, final DeserializationContext ctxt)
        throws IOException
    {
        BigDecimal amount = null;
        CurrencyUnit currencyUnit = null;

        if (p.isExpectedStartObjectToken()) {
            p.nextToken();
        }

        for (; p.currentToken() == JsonToken.FIELD_NAME; p.nextToken()) {
            final String field = p.currentName();

            p.nextToken();

            switch (field) {
            case "amount":
                amount = ctxt.readValue(p, BigDecimal.class);
                break;
            case "currency":
                currencyUnit = ctxt.readValue(p, CurrencyUnit.class);
                break;
            default:
                ctxt.handleUnknownProperty(p, this, handledType(), field);
            }
        }

        return Money.of(currencyUnit, amount);
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws IOException
    {
        // In future could check current token... for now this should be enough:
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }
}
