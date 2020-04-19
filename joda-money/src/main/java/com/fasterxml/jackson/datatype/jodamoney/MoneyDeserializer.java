package com.fasterxml.jackson.datatype.jodamoney;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class MoneyDeserializer extends StdDeserializer<Money>
{
    public MoneyDeserializer() {
        super(Money.class);
    }

    @Override
    public Money deserialize(final JsonParser p,
            final DeserializationContext context) throws IOException
    {
        BigDecimal amount = null;
        CurrencyUnit currencyUnit = null;

        if (p.isExpectedStartObjectToken()) {
            p.nextToken();
        }
        
        for (; p.currentToken() == JsonToken.FIELD_NAME; p.nextToken()) {
            final String field = p.currentName();

            p.nextToken();

            if ("amount".equals(field)) {
                amount = context.readValue(p, BigDecimal.class);
            } else if ("currency".equals(field)) {
                currencyUnit = context.readValue(p, CurrencyUnit.class);
            } else if (context.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                throw UnrecognizedPropertyException.from(p, Money.class, field,
                        Collections.<Object>singletonList("amount, currency")
                );
            } else {
                p.skipChildren();
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
