package tools.jackson.datatype.jodamoney;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import static java.util.Objects.requireNonNull;

public class MoneyDeserializer extends StdDeserializer<Money>
{
    private static final String F_AMOUNT = "amount";
    private static final String F_CURRENCY = "currency";
    private final AmountConverter amountConverter;

    // Kept to maintain backward compatibility with 2.x
    @SuppressWarnings("unused")
    public MoneyDeserializer() {
        this(DecimalNumberAmountConverter.getInstance());
    }

    MoneyDeserializer(final AmountConverter amountConverter) {
        super(Money.class);
        this.amountConverter = requireNonNull(amountConverter, "amount converter cannot be null");
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
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

        return new MoneyDeserializer(newConverter);
    }

    private AmountRepresentation _resolveRepresentation(BeanProperty property, DeserializationContext ctxt) {
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
    public LogicalType logicalType() {
        // structured, hence POJO
        return LogicalType.POJO;
    }

    // Needed for proper exception message later on
    @Override
    public Collection<Object> getKnownPropertyNames() {
        return Arrays.<Object>asList(F_AMOUNT, F_CURRENCY);
    }

    @Override
    public Money deserialize(final JsonParser p, final DeserializationContext ctxt)
        throws JacksonException
    {
        BigDecimal amount = null;
        CurrencyUnit currencyUnit = null;

        if (p.isExpectedStartObjectToken()) {
            p.nextToken();
        }

        for (; p.currentToken() == JsonToken.PROPERTY_NAME; p.nextToken()) {
            final String field = p.currentName();

            p.nextToken();

            switch (field) {
            case F_AMOUNT:
                amount = ctxt.readValue(p, BigDecimal.class);
                break;
            case F_CURRENCY:
                currencyUnit = ctxt.readValue(p, CurrencyUnit.class);
                break;
            default:
                ctxt.handleUnknownProperty(p, this, handledType(), field);
            }
        }

        // 01-Feb-2021, tatu: [datatypes-misc#8] Verify explicitly
        String missingName;

        if (amount == null) {
            missingName = F_AMOUNT;
        } else if (currencyUnit == null) {
            missingName = F_CURRENCY;
        } else {
            return amountConverter.toMoney(currencyUnit, amount);
        }

        return ctxt.reportPropertyInputMismatch(getValueType(ctxt), missingName,
"Property '%s' missing from Object value", missingName);
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws JacksonException
    {
        // In future could check current token... for now this should be enough:
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }
}
