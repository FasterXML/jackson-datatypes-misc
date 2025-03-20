package tools.jackson.datatype.javax.money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import tools.jackson.core.JsonParser;
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
        final String currencyCode = parser.getValueAsString();
        return Monetary.getCurrency(currencyCode);
    }
}
