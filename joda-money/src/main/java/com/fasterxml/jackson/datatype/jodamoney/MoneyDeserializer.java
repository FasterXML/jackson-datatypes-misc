package com.fasterxml.jackson.datatype.jodamoney;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

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

    private final String F_AMOUNT = "amount";
    private final String F_CURRENCY = "currency";

    public MoneyDeserializer() {
        super(Money.class);
    }

    @Override
    public LogicalType logicalType() {
        // structured, hence POJO
        return LogicalType.POJO;
    }

    // Needed for proper exception message later on
    @Override
    public Collection<Object> getKnownPropertyNames() {
        return Arrays.<Object>asList(F_AMOUNT, F_CURRENCY);
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
            case F_AMOUNT:
                amount = ctxt.readValue(p, BigDecimal.class);
                break;
            case F_CURRENCY:
                currencyUnit = ctxt.readValue(p, CurrencyUnit.class);
                break;
            default:
                ctxt.handleUnknownProperty(p, this, handledType(), field);
            }
        }

        // 01-Feb-2021, tatu: [datatypes-misc#8] Verify explicitly
        String missingName;

        if (amount == null) {
            missingName = F_AMOUNT;
        } else if (currencyUnit == null) {
            missingName = F_CURRENCY;
        } else {
            return Money.of(currencyUnit, amount);
        }

        return ctxt.reportPropertyInputMismatch(getValueType(ctxt), missingName,
"Property '%s' missing from Object value", missingName);
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
