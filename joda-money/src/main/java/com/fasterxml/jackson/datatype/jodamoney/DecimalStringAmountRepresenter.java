package com.fasterxml.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;

public final class DecimalStringAmountRepresenter implements AmountRepresenter<String> {

    private static final DecimalStringAmountRepresenter INSTANCE = new DecimalStringAmountRepresenter();

    public static DecimalStringAmountRepresenter getInstance() {
        return INSTANCE;
    }

    private DecimalStringAmountRepresenter() {
    }

    @Override
    public String write(final Money money) {
        return DecimalNumberAmountRepresenter.getInstance().write(money).toPlainString();
    }

    @Override
    public Money read(final CurrencyUnit currencyUnit, final BigDecimal amount) {
        return Money.of(currencyUnit, amount);
    }
}
