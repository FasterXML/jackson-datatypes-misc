package com.fasterxml.jackson.datatype.money;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import org.apiguardian.api.API;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryFormats;
import java.util.Objects;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.STABLE;


@API(status = STABLE)
public final class JavaxMoneyModule extends Module {

    private final AmountWriter<?> writer;
    private final FieldNames names;
    private final MonetaryAmountFormatFactory formatFactory;
    private final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory;
    private final Class implementationClass;

    public JavaxMoneyModule() {
        this(new DecimalAmountWriter(), FieldNames.defaults(), MonetaryAmountFormatFactory.NONE, null, null);
    }

    private <T extends MonetaryAmount> JavaxMoneyModule(final AmountWriter<?> writer,
                                                        final FieldNames names,
                                                        final MonetaryAmountFormatFactory formatFactory, Class<T> implementationClass,
                                                        final MonetaryAmountFactory<T> amountFactory) {

        this.writer = writer;
        this.names = names;
        this.formatFactory = formatFactory;
        this.implementationClass = implementationClass;
        this.amountFactory = amountFactory;
    }

    @Override
    public String getModuleName() {
        return JavaxMoneyModule.class.getSimpleName();
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public void setupModule(final SetupContext context) {
        final SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(CurrencyUnit.class, new CurrencyUnitSerializer());
        serializers.addSerializer(MonetaryAmount.class, new MonetaryAmountSerializer(names, writer, formatFactory));
        context.addSerializers(serializers);

        final SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(CurrencyUnit.class, new CurrencyUnitDeserializer());
        if (Objects.nonNull(this.amountFactory) && Objects.nonNull(this.implementationClass)) {
            deserializers.addDeserializer(MonetaryAmount.class, new MonetaryAmountDeserializer<>(amountFactory, names));
            // for reading into concrete implementation types
            deserializers.addDeserializer(implementationClass, new MonetaryAmountDeserializer<>(amountFactory, names));
        }
        context.addDeserializers(deserializers);
    }

    public JavaxMoneyModule withDecimalNumbers() {
        return withNumbers(new DecimalAmountWriter());
    }

    public JavaxMoneyModule withQuotedDecimalNumbers() {
        return withNumbers(new QuotedDecimalAmountWriter());
    }

    @API(status = EXPERIMENTAL)
    public JavaxMoneyModule withNumbers(final AmountWriter<?> writer) {
        return new JavaxMoneyModule(writer, names, formatFactory, implementationClass, amountFactory);
    }

    public <T extends MonetaryAmount> JavaxMoneyModule withMonetaryAmountFactory(final Class<T> implementationClass, final MonetaryAmountFactory<T> amountFactory) {
        return new JavaxMoneyModule(writer, names, formatFactory, implementationClass, amountFactory);
    }

    public JavaxMoneyModule withoutFormatting() {
        return withFormatting(MonetaryAmountFormatFactory.NONE);
    }

    public JavaxMoneyModule withDefaultFormatting() {
        return withFormatting(MonetaryFormats::getAmountFormat);
    }

    public JavaxMoneyModule withFormatting(final MonetaryAmountFormatFactory formatFactory) {
        return new JavaxMoneyModule(writer, names, formatFactory, implementationClass, amountFactory);
    }

    public JavaxMoneyModule withAmountFieldName(final String name) {
        return withFieldNames(names.withAmount(name));
    }

    public JavaxMoneyModule withCurrencyFieldName(final String name) {
        return withFieldNames(names.withCurrency(name));
    }

    public JavaxMoneyModule withFormattedFieldName(final String name) {
        return withFieldNames(names.withFormatted(name));
    }

    private JavaxMoneyModule withFieldNames(final FieldNames names) {
        return new JavaxMoneyModule(writer, names, formatFactory, implementationClass, amountFactory);
    }

}