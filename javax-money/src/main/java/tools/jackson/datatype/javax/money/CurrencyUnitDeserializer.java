package tools.jackson.datatype.javax.money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.UnknownCurrencyException;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(status = MAINTAINED)
public final class CurrencyUnitDeserializer extends StdScalarDeserializer<CurrencyUnit>
{
    public CurrencyUnitDeserializer() {
        super(CurrencyUnit.class);
    }

    @Override
    public Object deserializeWithType(final JsonParser parser, final DeserializationContext context,
            final TypeDeserializer deserializer) {

        // effectively assuming no type information at all
        return deserialize(parser, context);
    }

    @Override
    public CurrencyUnit deserialize(final JsonParser parser, final DeserializationContext context)
    {
        // [datatypes-misc#91] Only accept String values: for other tokens
        //    `getValueAsString()` returns `null` (leading to bare NPE) or
        //    coerces scalars (like numbers) into bogus currency codes
        if (!parser.hasToken(JsonToken.VALUE_STRING)) {
            return (CurrencyUnit) context.handleUnexpectedToken(getValueType(context), parser);
        }
        final String currencyCode = parser.getString();
        try {
            return Monetary.getCurrency(currencyCode);
        } catch (UnknownCurrencyException e) {
            // [datatypes-misc#91] Report as regular Jackson exception
            return (CurrencyUnit) context.handleWeirdStringValue(handledType(), currencyCode,
                    "not a valid currency code");
        }
    }
}
