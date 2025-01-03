package tools.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.SerializationContext;

public class CurrencyUnitSerializer extends JodaMoneySerializerBase<CurrencyUnit>
{
    public CurrencyUnitSerializer() {
        super(CurrencyUnit.class);
    }

    @Override
    public void serialize(final CurrencyUnit currencyUnit,
            final JsonGenerator g, final SerializationContext ctxt)
        throws JacksonException
    {
        g.writeString(currencyUnit.getCode());
    }
}