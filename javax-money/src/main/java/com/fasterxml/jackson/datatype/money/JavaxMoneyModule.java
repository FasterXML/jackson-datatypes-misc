package com.fasterxml.jackson.datatype.money;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import org.apiguardian.api.API;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import javax.money.MonetaryRounding;
import javax.money.format.MonetaryFormats;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.STABLE;


@API(status = STABLE)
public final class JavaxMoneyModule extends Module {

    private final AmountWriter<?> writer;
    private final FieldNames names;
    private final MonetaryAmountFormatFactory formatFactory;
    private final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory;
    private final MonetaryAmountFactory<FastMoney> fastMoneyFactory;
    private final MonetaryAmountFactory<Money> moneyFactory;
    private final MonetaryAmountFactory<RoundedMoney> roundedMoneyFactory;

    public JavaxMoneyModule() {
        this(new DecimalAmountWriter(), FieldNames.defaults(), MonetaryAmountFormatFactory.NONE,
                Money::of, FastMoney::of, Money::of, RoundedMoney::of);
    }

    private JavaxMoneyModule(final AmountWriter<?> writer,
                             final FieldNames names,
                             final MonetaryAmountFormatFactory formatFactory,
                             final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory,
                             final MonetaryAmountFactory<FastMoney> fastMoneyFactory,
                             final MonetaryAmountFactory<Money> moneyFactory,
                             final MonetaryAmountFactory<RoundedMoney> roundedMoneyFactory) {

        this.writer = writer;
        this.names = names;
        this.formatFactory = formatFactory;
        this.amountFactory = amountFactory;
        this.fastMoneyFactory = fastMoneyFactory;
        this.moneyFactory = moneyFactory;
        this.roundedMoneyFactory = roundedMoneyFactory;
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
        deserializers.addDeserializer(MonetaryAmount.class, new MonetaryAmountDeserializer<>(amountFactory, names));
        // for reading into concrete implementation types
        deserializers.addDeserializer(Money.class, new MonetaryAmountDeserializer<>(moneyFactory, names));
        deserializers.addDeserializer(FastMoney.class, new MonetaryAmountDeserializer<>(fastMoneyFactory, names));
        deserializers.addDeserializer(RoundedMoney.class, new MonetaryAmountDeserializer<>(roundedMoneyFactory, names));
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
        return new JavaxMoneyModule(writer, names, formatFactory, amountFactory,
                fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    /**
     * @see FastMoney
     * @return new {@link JavaxMoneyModule} using {@link FastMoney}
     */
    public JavaxMoneyModule withFastMoney() {
        return withMonetaryAmount(fastMoneyFactory);
    }

    /**
     * @see Money
     * @return new {@link JavaxMoneyModule} using {@link Money}
     */
    public JavaxMoneyModule withMoney() {
        return withMonetaryAmount(moneyFactory);
    }

    /**
     * @see RoundedMoney
     * @return new {@link JavaxMoneyModule} using {@link RoundedMoney}
     */
    public JavaxMoneyModule withRoundedMoney() {
        return withMonetaryAmount(roundedMoneyFactory);
    }

    /**
     * @see RoundedMoney
     * @param rounding the rounding operator
     * @return new {@link JavaxMoneyModule} using {@link RoundedMoney} with the given {@link MonetaryRounding}
     */
    public JavaxMoneyModule withRoundedMoney(final MonetaryOperator rounding) {
        final MonetaryAmountFactory<RoundedMoney> factory = (amount, currency) ->
                RoundedMoney.of(amount, currency, rounding);

        return new JavaxMoneyModule(writer, names, formatFactory, factory,
                fastMoneyFactory, moneyFactory, factory);
    }

    public JavaxMoneyModule withMonetaryAmount(final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory) {
        return new JavaxMoneyModule(writer, names, formatFactory, amountFactory,
                fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public JavaxMoneyModule withoutFormatting() {
        return withFormatting(MonetaryAmountFormatFactory.NONE);
    }

    public JavaxMoneyModule withDefaultFormatting() {
        return withFormatting(MonetaryFormats::getAmountFormat);
    }

    public JavaxMoneyModule withFormatting(final MonetaryAmountFormatFactory formatFactory) {
        return new JavaxMoneyModule(writer, names, formatFactory, amountFactory,
                fastMoneyFactory, moneyFactory, roundedMoneyFactory);
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
        return new JavaxMoneyModule(writer, names, formatFactory, amountFactory,
                fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

}