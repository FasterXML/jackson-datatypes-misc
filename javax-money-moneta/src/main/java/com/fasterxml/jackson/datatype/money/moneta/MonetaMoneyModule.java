package com.fasterxml.jackson.datatype.money.moneta;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.datatype.money.AmountWriter;
import com.fasterxml.jackson.datatype.money.CurrencyUnitDeserializer;
import com.fasterxml.jackson.datatype.money.FieldNames;
import com.fasterxml.jackson.datatype.money.JavaxMoneyModule;
import com.fasterxml.jackson.datatype.money.MonetaryAmountDeserializer;
import com.fasterxml.jackson.datatype.money.MonetaryAmountFactory;
import com.fasterxml.jackson.datatype.money.MonetaryAmountFormatFactory;
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
public final class MonetaMoneyModule extends Module {

    private final JavaxMoneyModule baseModule;
    private final FieldNames names;
    private final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory;
    private final MonetaryAmountFactory<FastMoney> fastMoneyFactory;
    private final MonetaryAmountFactory<Money> moneyFactory;
    private final MonetaryAmountFactory<RoundedMoney> roundedMoneyFactory;

    public MonetaMoneyModule() {
        this(new JavaxMoneyModule(), FieldNames.defaults(),
                Money::of, FastMoney::of, Money::of, RoundedMoney::of);
    }

    private MonetaMoneyModule(final JavaxMoneyModule baseModule, final FieldNames names,
                              final MonetaryAmountFactory<? extends MonetaryAmount> amountFactory,
                              final MonetaryAmountFactory<FastMoney> fastMoneyFactory,
                              final MonetaryAmountFactory<Money> moneyFactory,
                              final MonetaryAmountFactory<RoundedMoney> roundedMoneyFactory) {
        this.baseModule = baseModule;
        this.names = names;
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

        this.baseModule.setupModule(context);

        final SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(CurrencyUnit.class, new CurrencyUnitDeserializer());
        deserializers.addDeserializer(MonetaryAmount.class, new MonetaryAmountDeserializer<>(amountFactory, names));
        // for reading into concrete implementation types
        deserializers.addDeserializer(Money.class, new MonetaryAmountDeserializer<>(moneyFactory, names));
        deserializers.addDeserializer(FastMoney.class, new MonetaryAmountDeserializer<>(fastMoneyFactory, names));
        deserializers.addDeserializer(RoundedMoney.class, new MonetaryAmountDeserializer<>(roundedMoneyFactory, names));
        context.addDeserializers(deserializers);
    }

    public MonetaMoneyModule withDecimalNumbers() {
        return new MonetaMoneyModule(baseModule.withDecimalNumbers(), names, amountFactory, fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public MonetaMoneyModule withQuotedDecimalNumbers() {
        return new MonetaMoneyModule(baseModule.withQuotedDecimalNumbers(), names, amountFactory, fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    @API(status = EXPERIMENTAL)
    public MonetaMoneyModule withNumbers(final AmountWriter<?> writer) {
        return new MonetaMoneyModule(baseModule.withNumbers(writer), names, amountFactory, fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    /**
     * @return new {@link MonetaMoneyModule} using {@link FastMoney}
     * @see FastMoney
     */
    public MonetaMoneyModule withFastMoney() {
        return withMonetaryAmountFactory(FastMoney.class, fastMoneyFactory);
    }

    /**
     * @return new {@link MonetaMoneyModule} using {@link Money}
     * @see Money
     */
    public MonetaMoneyModule withMoney() {
        return withMonetaryAmountFactory(Money.class, moneyFactory);
    }

    /**
     * @return new {@link MonetaMoneyModule} using {@link RoundedMoney}
     * @see RoundedMoney
     */
    public MonetaMoneyModule withRoundedMoney() {
        return withMonetaryAmountFactory(RoundedMoney.class, roundedMoneyFactory);
    }

    /**
     * @param rounding the rounding operator
     * @return new {@link MonetaMoneyModule} using {@link RoundedMoney} with the given {@link MonetaryRounding}
     * @see RoundedMoney
     */
    public MonetaMoneyModule withRoundedMoney(final MonetaryOperator rounding) {
        final MonetaryAmountFactory<RoundedMoney> factory = (amount, currency) ->
                RoundedMoney.of(amount, currency, rounding);

        return withMonetaryAmountFactory(RoundedMoney.class, factory);
    }


    private <T extends MonetaryAmount> MonetaMoneyModule withMonetaryAmountFactory(final Class<T> implementationClass, final MonetaryAmountFactory<T> amountFactory) {
        return new MonetaMoneyModule(baseModule.withMonetaryAmountFactory(implementationClass, amountFactory), names, amountFactory,
                fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public MonetaMoneyModule withoutFormatting() {
        return withFormatting(MonetaryAmountFormatFactory.NONE);
    }

    public MonetaMoneyModule withDefaultFormatting() {
        return withFormatting(MonetaryFormats::getAmountFormat);
    }

    public MonetaMoneyModule withFormatting(final MonetaryAmountFormatFactory formatFactory) {
        return new MonetaMoneyModule(baseModule.withFormatting(formatFactory), names, amountFactory, fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public MonetaMoneyModule withAmountFieldName(final String name) {
        return new MonetaMoneyModule(baseModule.withAmountFieldName(name), names.withAmount(name), amountFactory, fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public MonetaMoneyModule withCurrencyFieldName(final String name) {
        return new MonetaMoneyModule(baseModule.withCurrencyFieldName(name), names.withCurrency(name), amountFactory, fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }

    public MonetaMoneyModule withFormattedFieldName(final String name) {
        return new MonetaMoneyModule(baseModule.withFormattedFieldName(name), names.withFormatted(name), amountFactory, fastMoneyFactory, moneyFactory, roundedMoneyFactory);
    }


}
