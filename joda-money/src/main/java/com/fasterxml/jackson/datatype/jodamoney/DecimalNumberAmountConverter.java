package com.fasterxml.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

final class DecimalNumberAmountConverter implements AmountConverter {

    private static final DecimalNumberAmountConverter INSTANCE = new DecimalNumberAmountConverter();

    static DecimalNumberAmountConverter getInstance() {
        return INSTANCE;
    }

    private DecimalNumberAmountConverter() {
    }

    @Override
    public BigDecimal fromMoney(final Money money) {
        final BigDecimal decimal = money.getAmount();
        final int decimalPlaces = money.getCurrencyUnit().getDecimalPlaces();
        final int scale = Math.max(decimal.scale(), decimalPlaces);
        return decimal.setScale(scale, RoundingMode.UNNECESSARY);
    }

    @Override
    public Money toMoney(final CurrencyUnit currencyUnit, final BigDecimal amount) {
        return Money.of(currencyUnit, amount);
    }
}
