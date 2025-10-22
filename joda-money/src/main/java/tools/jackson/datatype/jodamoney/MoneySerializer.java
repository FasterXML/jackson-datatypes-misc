package tools.jackson.datatype.jodamoney;

import org.joda.money.Money;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
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
    public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property) {
        if (property == null) {
            return this;
        }

        AmountRepresentation effectiveRepresentation = _resolveRepresentation(property, ctxt);

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

    private AmountRepresentation _resolveRepresentation(BeanProperty property, SerializationContext ctxt) {
        // Priority 1: @JodaMoney annotation
        JodaMoney jodaMoney = property.getAnnotation(JodaMoney.class);
        if (jodaMoney != null && jodaMoney.amountRepresentation() != AmountRepresentation.DEFAULT) {
            return jodaMoney.amountRepresentation();
        }

        // Priority 2: @JsonFormat mapping
        JsonFormat.Value format = property.findPropertyFormat(ctxt.getConfig(), Money.class);
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
            final JsonGenerator g, final SerializationContext ctxt)
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
            final JsonGenerator g, final SerializationContext ctxt,
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
            final JsonGenerator g, final SerializationContext ctxt)
                    throws JacksonException
    {
        ctxt.defaultSerializeProperty("amount", amountConverter.fromMoney(value), g);
        ctxt.defaultSerializeProperty("currency", value.getCurrencyUnit(), g);
    }
}
