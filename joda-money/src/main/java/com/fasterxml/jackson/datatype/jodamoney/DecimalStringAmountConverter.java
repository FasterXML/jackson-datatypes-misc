package com.fasterxml.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;

public final class DecimalStringAmountConverter implements AmountConverter<String> {

    private static final DecimalStringAmountConverter INSTANCE = new DecimalStringAmountConverter();

    public static DecimalStringAmountConverter getInstance() {
        return INSTANCE;
    }

    private DecimalStringAmountConverter() {
    }

    @Override
    public String fromMoney(final Money money) {
        return DecimalNumberAmountConverter.getInstance().fromMoney(money).toPlainString();
    }

    @Override
    public Money toMoney(final CurrencyUnit currencyUnit, final BigDecimal amount) {
        return Money.of(currencyUnit, amount);
    }
}
