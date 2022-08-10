package tools.jackson.datatype.jodamoney;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import static java.util.Objects.requireNonNull;

public class MoneyDeserializer extends StdDeserializer<Money>
{
    private final String F_AMOUNT = "amount";
    private final String F_CURRENCY = "currency";
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
