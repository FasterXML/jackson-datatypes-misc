package com.fasterxml.jackson.datatype.moneta;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import javax.money.MonetaryRounding;
import javax.money.format.MonetaryFormats;

import org.apiguardian.api.API;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.datatype.javax.money.AmountWriter;
import com.fasterxml.jackson.datatype.javax.money.FieldNames;
import com.fasterxml.jackson.datatype.javax.money.JavaxMoneyModule;
import com.fasterxml.jackson.datatype.javax.money.MonetaryAmountDeserializer;
import com.fasterxml.jackson.datatype.javax.money.MonetaryAmountFactory;
import com.fasterxml.jackson.datatype.javax.money.MonetaryAmountFormatFactory;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * Module that adds support for Moneta types (FastMoney, Money, RoundedMoney) on top of the {@linkplain JavaxMoneyModule}.
 * Contains helper methods like {@link #withFastMoney()}, {@link #withMoney()}, {@link #withRoundedMoney()} and {@link #withRoundedMoney(MonetaryOperator)} that can be used to configure the module.
 */
@API(status = STABLE)
public final class MonetaMoneyModule extends Module
{
    private final JavaxMoneyModule baseModule;
    private final FieldNames names;
    private final MonetaryAmountFactory<FastMoney> fastMoneyFactory;
    private final MonetaryAmountFactory<Money> moneyFactory;
    private final MonetaryAmountFactory<RoundedMoney> roundedMoneyFactory;

    public MonetaMoneyModule() {
        this(new JavaxMoneyModule().withMonetaryAmountFactory(Money::of), FieldNames.defaults(), FastMoney::of, Money::of, RoundedMoney::of);
    }

    private MonetaMoneyModule(final JavaxMoneyModule baseModule, final FieldNames names, final MonetaryAmountFactory<FastMoney> fastMoneyFactory, final MonetaryAmountFactory<Money> moneyFactory, final MonetaryAmountFactory<RoundedMoney> roundedMoneyFactory) {
        this.baseModule = baseModule;
        this.names = names;
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

        this.baseModule.setupModule(context);
        final SimpleDeserializers deserializers = new SimpleDeserializers();

        //Register deserializers for Moneta types
        deserializers.addDeserializer(FastMoney.class, new MonetaryAmountDeserializer<>(fastMoneyFactory, names));
        deserializers.addDeserializer(Money.class, new MonetaryAmountDeserializer<>(moneyFactory, names));
        deserializers.addDeserializer(RoundedMoney.class, new MonetaryAmountDeserializer<>(roundedMoneyFactory, names));

        context.addDeserializers(deserializers);
    }

    public MonetaMoneyModule withDecimalNumbers() {
        return new MonetaMoneyModule(baseModule.withDecimalNumbers(), names, fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public MonetaMoneyModule withQuotedDecimalNumbers() {
        return new MonetaMoneyModule(baseModule.withQuotedDecimalNumbers(), names, fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    @API(status = EXPERIMENTAL)
    public MonetaMoneyModule withNumbers(final AmountWriter<?> writer) {
        return new MonetaMoneyModule(baseModule.withNumbers(writer), names, fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    /**
     * @return new {@link MonetaMoneyModule} using {@link FastMoney}
     * @see FastMoney
     */
    public MonetaMoneyModule withFastMoney() {
        return withMonetaryAmountFactory(fastMoneyFactory);
    }

    /**
     * @return new {@link MonetaMoneyModule} using {@link Money}
     * @see Money
     */
    public MonetaMoneyModule withMoney() {
        return withMonetaryAmountFactory(moneyFactory);
    }

    /**
     * @return new {@link MonetaMoneyModule} using {@link RoundedMoney}
     * @see RoundedMoney
     */
    public MonetaMoneyModule withRoundedMoney() {
        return withMonetaryAmountFactory(roundedMoneyFactory);
    }

    /**
     * @param rounding the rounding operator
     * @return new {@link MonetaMoneyModule} using {@link RoundedMoney} with the given {@link MonetaryRounding}
     * @see RoundedMoney
     */
    public MonetaMoneyModule withRoundedMoney(final MonetaryOperator rounding) {
        final MonetaryAmountFactory<RoundedMoney> factory = (amount, currency) -> RoundedMoney.of(amount, currency, rounding);

        return withMonetaryAmountFactory(factory);
    }


    private <T extends MonetaryAmount> MonetaMoneyModule withMonetaryAmountFactory(final MonetaryAmountFactory<T> amountFactory) {
        return new MonetaMoneyModule(baseModule.withMonetaryAmountFactory(amountFactory), names, fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public MonetaMoneyModule withoutFormatting() {
        return withFormatting(MonetaryAmountFormatFactory.NONE);
    }

    public MonetaMoneyModule withDefaultFormatting() {
        return withFormatting(MonetaryFormats::getAmountFormat);
    }

    public MonetaMoneyModule withFormatting(final MonetaryAmountFormatFactory formatFactory) {
        return new MonetaMoneyModule(baseModule.withFormatting(formatFactory), names, fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public MonetaMoneyModule withAmountFieldName(final String name) {
        return new MonetaMoneyModule(baseModule.withAmountFieldName(name), names.withAmount(name), fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public MonetaMoneyModule withCurrencyFieldName(final String name) {
        return new MonetaMoneyModule(baseModule.withCurrencyFieldName(name), names.withCurrency(name), fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public MonetaMoneyModule withFormattedFieldName(final String name) {
        return new MonetaMoneyModule(baseModule.withFormattedFieldName(name), names.withFormatted(name), fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }


}
