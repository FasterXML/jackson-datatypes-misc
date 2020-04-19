package com.fasterxml.jackson.datatype.jodamoney;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.joda.money.Money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class MoneySerializer extends JodaMoneySerializerBase<Money>
{
    private static final long serialVersionUID = 1L;

    public MoneySerializer() {
        super(Money.class);
    }

    @Override
    public void serialize(final Money value,
            final JsonGenerator g,
            final SerializerProvider context)
        throws IOException
    {
        g.writeStartObject();
        _writeFields(value, g, context);
        g.writeEndObject();
    }

    // 19-Apr-2020, tatu: Need to override because `Money` instances ARE actually
    //    serialized as JSON Objects, unlike most other Joda types
    @Override
    public void serializeWithType(Money value, JsonGenerator g,
            SerializerProvider context,
            TypeSerializer typeSer) throws IOException
    {
        g.setCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        _writeFields(value, g, context);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    private final void _writeFields(final Money money,
            final JsonGenerator g,
            final SerializerProvider context)
        throws IOException
    {
        final BigDecimal decimal = money.getAmount();
        final int decimalPlaces = money.getCurrencyUnit().getDecimalPlaces();
        final int scale = Math.max(decimal.scale(), decimalPlaces);
        g.writeNumberField("amount", decimal.setScale(scale, RoundingMode.UNNECESSARY));
        g.writeFieldName("currency");
        context.defaultSerializeValue(money.getCurrencyUnit(), g);
    }
}
