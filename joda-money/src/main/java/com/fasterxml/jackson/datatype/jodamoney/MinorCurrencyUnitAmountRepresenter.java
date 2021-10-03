package com.fasterxml.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MinorCurrencyUnitAmountRepresenter implements AmountRepresenter<Long> {

    private static final MinorCurrencyUnitAmountRepresenter INSTANCE = new MinorCurrencyUnitAmountRepresenter();

    public static MinorCurrencyUnitAmountRepresenter getInstance() {
        return INSTANCE;
    }

    private MinorCurrencyUnitAmountRepresenter() {
    }

    @Override
    public Long write(final Money money) {
        return money.getAmountMinorLong();
    }

    @Override
    public Money read(final CurrencyUnit currencyUnit, final BigDecimal amount) {
        return Money.of(currencyUnit, amount.movePointLeft(currencyUnit.getDecimalPlaces()), RoundingMode.UNNECESSARY);
    }
}
