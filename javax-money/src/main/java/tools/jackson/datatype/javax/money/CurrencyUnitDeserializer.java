package tools.jackson.datatype.javax.money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

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
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            final String currencyCode = parser.getString();
            try {
                return Monetary.getCurrency(currencyCode);
            } catch (Exception e) {
                // 09-Sep-2026, pjfanning: `Monetary` throws `UnknownCurrencyException`,
                //    which is not a `JacksonException`: convert into standard databind
                //    exception (and give `DeserializationProblemHandler`s a chance)
                return (CurrencyUnit) context.handleWeirdStringValue(handledType(), currencyCode,
                        e.getMessage());
            }
        }
        // Anything but String is an error: `getValueAsString()` used to coerce here, and
        // returned `null` for structured values -- leading to a bare NPE from `Monetary`
        return (CurrencyUnit) context.handleUnexpectedToken(getValueType(context),
                parser.currentToken(), parser,
                "Expected a `JsonToken.VALUE_STRING`, got `JsonToken.%s`",
                parser.currentToken());
    }
}
