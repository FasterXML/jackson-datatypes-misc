package com.fasterxml.jackson.datatype.jodamoney;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.joda.money.Money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class MoneySerializer extends JodaMoneySerializerBase<Money>
{
    private static final long serialVersionUID = 1L;

    public MoneySerializer() {
        super(Money.class);
    }

    @Override
    public void serialize(final Money money,
            final JsonGenerator gen,
            final SerializerProvider context) throws IOException
    {
        final BigDecimal decimal = money.getAmount();
        final int decimalPlaces = money.getCurrencyUnit().getDecimalPlaces();
        final int scale = Math.max(decimal.scale(), decimalPlaces);

        gen.writeStartObject();
        gen.writeNumberField("amount", decimal.setScale(scale, RoundingMode.UNNECESSARY));
        gen.writeFieldName("currency");
        context.defaultSerializeValue(money.getCurrencyUnit(), gen);
        gen.writeEndObject();
    }
}
