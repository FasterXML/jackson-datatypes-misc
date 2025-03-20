package tools.jackson.datatype.javax.money;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.Nullable;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.databind.util.NameTransformer;

final class MonetaryAmountSerializer extends StdSerializer<MonetaryAmount>
{
    private final FieldNames names;
    private final AmountWriter<?> writer;
    private final MonetaryAmountFormatFactory factory;
    private final boolean isUnwrapping;
    private final NameTransformer nameTransformer;

    MonetaryAmountSerializer(final FieldNames names, final AmountWriter<?> writer,
            final MonetaryAmountFormatFactory factory, boolean isUnwrapping, @Nullable final NameTransformer nameTransformer) {
        super(MonetaryAmount.class);
        this.writer = writer;
        this.factory = factory;
        this.names = names;
        this.isUnwrapping = isUnwrapping;
        this.nameTransformer = nameTransformer;
    }

    MonetaryAmountSerializer(final FieldNames names, final AmountWriter<?> writer,
            final MonetaryAmountFormatFactory factory) {
        this(names, writer, factory, false, null);
    }

    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper wrapper, final JavaType hint)
    {
        @Nullable final JsonObjectFormatVisitor visitor = wrapper.expectObjectFormat(hint);

        if (visitor == null) {
            return;
        }

        final SerializationContext provider = wrapper.getContext();

        visitor.property(names.getAmount(),
                provider.findValueSerializer(writer.getType()),
                provider.constructType(writer.getType()));

        visitor.property(names.getCurrency(),
                provider.findValueSerializer(CurrencyUnit.class),
                provider.constructType(CurrencyUnit.class));

        visitor.optionalProperty(names.getFormatted(),
                provider.findValueSerializer(String.class),
                provider.constructType(String.class));
    }

    @Override
    public void serializeWithType(final MonetaryAmount value, final JsonGenerator generator,
            final SerializationContext provider, final TypeSerializer serializer)
    {
        // effectively assuming no type information at all
        serialize(value, generator, provider);
    }

    @Override
    public void serialize(final MonetaryAmount value, final JsonGenerator json,
            final SerializationContext ctxt)
    {
        final CurrencyUnit currency = value.getCurrency();
        @Nullable final String formatted = format(value, ctxt);

        if (!isUnwrapping) {
            json.writeStartObject();
        }

        {
            ctxt.defaultSerializeProperty(transformName(names.getAmount()), writer.write(value), json);
            ctxt.defaultSerializeProperty(transformName(names.getCurrency()), currency, json);

            if (formatted != null) {
                ctxt.defaultSerializeProperty(transformName(names.getFormatted()), formatted, json);
            }
        }

        if (!isUnwrapping) {
            json.writeEndObject();
        }
    }

    private String transformName(String name) {
        return (nameTransformer != null) ? nameTransformer.transform(name) : name;
    }

    @Nullable
    private String format(final MonetaryAmount value, final SerializationContext ctxt) {
        final Locale locale = ctxt.getConfig().getLocale();
        final MonetaryAmountFormat format = factory.create(locale);
        return format == null ? null : format.format(value);
    }

    @Override
    public boolean isUnwrappingSerializer() {
        return isUnwrapping;
    }

    @Override
    public ValueSerializer<MonetaryAmount> unwrappingSerializer(@Nullable final NameTransformer nameTransformer) {
        return new MonetaryAmountSerializer(names, writer, factory, true, nameTransformer);
    }
}
