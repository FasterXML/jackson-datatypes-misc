package com.fasterxml.jackson.datatype.jodamoney;

import java.io.IOException;

import org.joda.money.CurrencyUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CurrencyUnitSerializer extends JodaMoneySerializerBase<CurrencyUnit>
{
    private static final long serialVersionUID = 1L;

    public CurrencyUnitSerializer() {
        super(CurrencyUnit.class);
    }

    @Override
    public void serialize(
            final CurrencyUnit currencyUnit,
            final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider
    ) throws IOException {
        jsonGenerator.writeString(currencyUnit.getCode());
    }
}
