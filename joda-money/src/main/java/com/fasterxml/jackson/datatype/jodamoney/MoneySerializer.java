package com.fasterxml.jackson.datatype.jodamoney;

import org.joda.money.Money;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;
import tools.jackson.databind.SerializerProvider;
import tools.jackson.databind.jsontype.TypeSerializer;

import static java.util.Objects.requireNonNull;

public class MoneySerializer extends JodaMoneySerializerBase<Money>
{
    private final AmountConverter amountConverter;

    // Kept to maintain backward compatibility with 2.x
    public MoneySerializer() {
        this(DecimalNumberAmountConverter.getInstance());
    }

    MoneySerializer(final AmountConverter amountConverter) {
        super(Money.class);
        this.amountConverter = requireNonNull(amountConverter, "amount converter cannot be null");
    }

    @Override
    public void serialize(final Money value,
            final JsonGenerator g, final SerializerProvider ctxt)
        throws JacksonException
    {
        g.writeStartObject();
        _writeProperties(value, g, ctxt);
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
        _writeProperties(value, g, ctxt);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }

    private final void _writeProperties(final Money value,
            final JsonGenerator g, final SerializerProvider ctxt)
                    throws JacksonException
    {
        ctxt.defaultSerializeProperty("amount", amountConverter.fromMoney(value), g);
        ctxt.defaultSerializeProperty("currency", value.getCurrencyUnit(), g);
    }
}
