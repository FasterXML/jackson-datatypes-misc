package com.fasterxml.jackson.datatype.jodamoney;

import java.io.IOException;

import org.joda.money.Money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import static java.util.Objects.requireNonNull;

public class MoneySerializer extends JodaMoneySerializerBase<Money>
{
    private static final long serialVersionUID = 1L;

    private final AmountConverter amountConverter;

    // Kept to maintain backward compatibility with 2.x
    @SuppressWarnings("unused")
    public MoneySerializer() {
        this(DecimalNumberAmountConverter.getInstance());
    }

    MoneySerializer(final AmountConverter amountConverter) {
        super(Money.class);
        this.amountConverter = requireNonNull(amountConverter, "amount converter cannot be null");
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
        g.assignCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        _writeFields(value, g, context);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    private void _writeFields(final Money money,
            final JsonGenerator g,
            final SerializerProvider context)
        throws IOException
    {
        context.defaultSerializeField("amount", amountConverter.fromMoney(money), g);
        context.defaultSerializeField("currency", money.getCurrencyUnit(), g);
    }
}
