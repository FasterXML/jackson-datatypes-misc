package com.fasterxml.jackson.datatype.jodamoney;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.joda.money.Money;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class MoneySerializer extends JodaMoneySerializerBase<Money>
{
    public MoneySerializer() {
        super(Money.class);
    }

    @Override
    public void serialize(final Money value,
            final JsonGenerator g, final SerializerProvider ctxt)
        throws JacksonException
    {
        g.writeStartObject();
        _writeFields(value, g, ctxt);
        g.writeEndObject();
    }

    // 19-Apr-2020, tatu: Need to override because `Money` instances ARE actually
    //    serialized as JSON Objects, unlike most other Joda types
    @Override
    public void serializeWithType(final Money value,
            final JsonGenerator g, final SerializerProvider ctxt,
            final TypeSerializer typeSer)
        throws JacksonException
    {
        g.assignCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        _writeFields(value, g, ctxt);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }

    private final void _writeFields(final Money value,
            final JsonGenerator g, final SerializerProvider ctxt)
        throws JacksonException
    {
        final BigDecimal decimal = value.getAmount();
        final int decimalPlaces = value.getCurrencyUnit().getDecimalPlaces();
        final int scale = Math.max(decimal.scale(), decimalPlaces);

        g.writeNumberField("amount", decimal.setScale(scale, RoundingMode.UNNECESSARY));
        g.writeFieldName("currency");
        ctxt.writeValue(g, value.getCurrencyUnit());
    }
}
