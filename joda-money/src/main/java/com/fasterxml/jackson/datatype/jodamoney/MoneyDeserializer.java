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

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class MoneyDeserializer extends StdDeserializer<Money>
{
    public MoneyDeserializer() {
        super(Money.class);
    }

    @Override
    public Money deserialize(final JsonParser jsonParser,
            final DeserializationContext context) throws IOException
    {
        BigDecimal amount = null;
        CurrencyUnit currencyUnit = null;
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            final String field = jsonParser.currentName();

            jsonParser.nextToken();

            if ("amount".equals(field)) {
                amount = context.readValue(jsonParser, BigDecimal.class);
            } else if ("currency".equals(field)) {
                currencyUnit = context.readValue(jsonParser, CurrencyUnit.class);
            } else if (context.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                throw UnrecognizedPropertyException.from(jsonParser, Money.class, field,
                        Collections.<Object>singletonList("amount, currency")
                );
            } else {
                jsonParser.skipChildren();
            }
        }

        return Money.of(currencyUnit, amount);
    }
}
