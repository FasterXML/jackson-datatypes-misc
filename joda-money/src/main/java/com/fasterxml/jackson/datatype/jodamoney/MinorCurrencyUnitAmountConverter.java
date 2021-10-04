package com.fasterxml.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MinorCurrencyUnitAmountConverter implements AmountConverter<Long> {

    private static final MinorCurrencyUnitAmountConverter INSTANCE = new MinorCurrencyUnitAmountConverter();

    public static MinorCurrencyUnitAmountConverter getInstance() {
        return INSTANCE;
    }

    private MinorCurrencyUnitAmountConverter() {
    }

    @Override
    public Long fromMoney(final Money money) {
        return money.getAmountMinorLong();
    }

    @Override
    public Money toMoney(final CurrencyUnit currencyUnit, final BigDecimal amount) {
        return Money.of(currencyUnit, amount.movePointLeft(currencyUnit.getDecimalPlaces()), RoundingMode.UNNECESSARY);
    }
}
