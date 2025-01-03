package tools.jackson.datatype.jodamoney;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdScalarDeserializer;

import org.joda.money.CurrencyUnit;

public class CurrencyUnitDeserializer extends StdScalarDeserializer<CurrencyUnit>
{
    public CurrencyUnitDeserializer() {
        super(CurrencyUnit.class);
    }

    @Override
    public CurrencyUnit deserialize(final JsonParser p,
            final DeserializationContext ctxt)
    {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            final String currencyCode = p.getString();
            try {
                return CurrencyUnit.of(currencyCode);
            } catch (Exception e) {
                return (CurrencyUnit) ctxt.handleWeirdStringValue(handledType(), currencyCode,
                        e.getMessage());
            }
        }
        return (CurrencyUnit) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
                "Expected a `JsonToken.VALUE_STRING`, got `JsonToken.%s`",
                p.currentToken());
    }
}
