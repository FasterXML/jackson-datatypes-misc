package com.fasterxml.jackson.datatype.jodamoney;

import java.io.IOException;

import org.joda.money.Money;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import static java.util.Objects.requireNonNull;

public class MoneySerializer extends JodaMoneySerializerBase<Money>
    implements ContextualSerializer
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
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        if (property == null) {
            return this;
        }

        AmountRepresentation effectiveRepresentation = _resolveRepresentation(property, prov);

        if (effectiveRepresentation == null || effectiveRepresentation == AmountRepresentation.DEFAULT) {
            // Keep current converter (module-level default)
            return this;
        }

        AmountConverter newConverter = _getConverterForRepresentation(effectiveRepresentation);
        if (newConverter == this.amountConverter) {
            return this;
        }

        return new MoneySerializer(newConverter);
    }

    private AmountRepresentation _resolveRepresentation(BeanProperty property, SerializerProvider prov) {
        // Priority 1: @JsonMoney annotation
        JsonMoney jsonMoney = property.getAnnotation(JsonMoney.class);
        if (jsonMoney != null && jsonMoney.amountRepresentation() != AmountRepresentation.DEFAULT) {
            return jsonMoney.amountRepresentation();
        }

        // Priority 2: @JsonFormat mapping
        JsonFormat.Value format = property.findPropertyFormat(prov.getConfig(), Money.class);
        if (format != null && format.getShape() != JsonFormat.Shape.ANY) {
            AmountRepresentation mapped = _mapShapeToRepresentation(format.getShape());
            if (mapped != null) {
                return mapped;
            }
        }

        // Priority 3 & 4: Module default or built-in default (already in amountConverter)
        return null;
    }

    private AmountRepresentation _mapShapeToRepresentation(JsonFormat.Shape shape) {
        switch (shape) {
            case STRING:
                return AmountRepresentation.DECIMAL_STRING;
            case NUMBER:
            case NUMBER_FLOAT:
                return AmountRepresentation.DECIMAL_NUMBER;
            case NUMBER_INT:
                return AmountRepresentation.MINOR_CURRENCY_UNIT;
            default:
                return null; // Ignore other shapes
        }
    }

    private AmountConverter _getConverterForRepresentation(AmountRepresentation representation) {
        switch (representation) {
            case DECIMAL_NUMBER:
                return DecimalNumberAmountConverter.getInstance();
            case DECIMAL_STRING:
                return DecimalStringAmountConverter.getInstance();
            case MINOR_CURRENCY_UNIT:
                return MinorCurrencyUnitAmountConverter.getInstance();
            default:
                return this.amountConverter;
        }
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
