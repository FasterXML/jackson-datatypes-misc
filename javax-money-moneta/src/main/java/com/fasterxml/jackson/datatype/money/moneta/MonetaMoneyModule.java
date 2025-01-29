package com.fasterxml.jackson.datatype.money.moneta;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.money.AmountWriter;
import com.fasterxml.jackson.datatype.money.JavaxMoneyModule;
import com.fasterxml.jackson.datatype.money.MonetaryAmountFactory;
import com.fasterxml.jackson.datatype.money.MonetaryAmountFormatFactory;
import org.apiguardian.api.API;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;

import javax.money.MonetaryOperator;
import javax.money.MonetaryRounding;
import javax.money.format.MonetaryFormats;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public final class MonetaMoneyModule extends Module {
    private final JavaxMoneyModule baseModule;

    public MonetaMoneyModule() {
        this(new JavaxMoneyModule().withMonetaryAmountFactory(Money.class, Money::of));
    }

    private MonetaMoneyModule(final JavaxMoneyModule baseModule) {
        this.baseModule = baseModule;
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
    }

    public MonetaMoneyModule withDecimalNumbers() {
        return new MonetaMoneyModule(baseModule.withDecimalNumbers());
    }

    public MonetaMoneyModule withQuotedDecimalNumbers() {
        return new MonetaMoneyModule(baseModule.withQuotedDecimalNumbers());
    }

    @API(status = EXPERIMENTAL)
    public MonetaMoneyModule withNumbers(final AmountWriter<?> writer) {
        return new MonetaMoneyModule(baseModule.withNumbers(writer));
    }

    /**
     * @return new {@link MonetaMoneyModule} using {@link FastMoney}
     * @see FastMoney
     */
    public MonetaMoneyModule withFastMoney() {
        return new MonetaMoneyModule(baseModule.withMonetaryAmountFactory(FastMoney.class, FastMoney::of));
    }

    /**
     * @return new {@link MonetaMoneyModule} using {@link Money}
     * @see Money
     */
    public MonetaMoneyModule withMoney() {
        return new MonetaMoneyModule(baseModule.withMonetaryAmountFactory(Money.class, Money::of));
    }

    /**
     * @return new {@link MonetaMoneyModule} using {@link RoundedMoney}
     * @see RoundedMoney
     */
    public MonetaMoneyModule withRoundedMoney() {
        return new MonetaMoneyModule(baseModule.withMonetaryAmountFactory(RoundedMoney.class, RoundedMoney::of));
    }

    /**
     * @param rounding the rounding operator
     * @return new {@link MonetaMoneyModule} using {@link RoundedMoney} with the given {@link MonetaryRounding}
     * @see RoundedMoney
     */
    public MonetaMoneyModule withRoundedMoney(final MonetaryOperator rounding) {
        final MonetaryAmountFactory<RoundedMoney> factory = (amount, currency) ->
                RoundedMoney.of(amount, currency, rounding);

        return new MonetaMoneyModule(baseModule.withMonetaryAmountFactory(RoundedMoney.class, factory));
    }

    public MonetaMoneyModule withoutFormatting() {
        return withFormatting(MonetaryAmountFormatFactory.NONE);
    }

    public MonetaMoneyModule withDefaultFormatting() {
        return withFormatting(MonetaryFormats::getAmountFormat);
    }

    public MonetaMoneyModule withFormatting(final MonetaryAmountFormatFactory formatFactory) {
        return new MonetaMoneyModule(baseModule.withFormatting(formatFactory));
    }

    public MonetaMoneyModule withAmountFieldName(final String name) {
        return new MonetaMoneyModule(baseModule.withAmountFieldName(name));
    }

    public MonetaMoneyModule withCurrencyFieldName(final String name) {
        return new MonetaMoneyModule(baseModule.withCurrencyFieldName(name));
    }

    public MonetaMoneyModule withFormattedFieldName(final String name) {
        return new MonetaMoneyModule(baseModule.withFormattedFieldName(name));
    }


}
