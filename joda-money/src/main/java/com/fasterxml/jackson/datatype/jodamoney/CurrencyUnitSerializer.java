package com.fasterxml.jackson.datatype.jodamoney;

import java.io.IOException;

import org.joda.money.CurrencyUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CurrencyUnitSerializer extends JodaMoneySerializerBase<CurrencyUnit>
{
    public CurrencyUnitSerializer() {
        super(CurrencyUnit.class);
    }

    @Override
    public void serialize(final CurrencyUnit currencyUnit,
            final JsonGenerator g,
            final SerializerProvider ctxt) throws IOException {
        g.writeString(currencyUnit.getCode());
    }
}
