package com.fasterxml.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class DecimalNumberAmountRepresenter implements AmountRepresenter<BigDecimal> {

    private static final DecimalNumberAmountRepresenter INSTANCE = new DecimalNumberAmountRepresenter();

    public static DecimalNumberAmountRepresenter getInstance() {
        return INSTANCE;
    }

    private DecimalNumberAmountRepresenter() {
    }

    @Override
    public BigDecimal write(final Money money) {
        final BigDecimal decimal = money.getAmount();
        final int decimalPlaces = money.getCurrencyUnit().getDecimalPlaces();
        final int scale = Math.max(decimal.scale(), decimalPlaces);
        return decimal.setScale(scale, RoundingMode.UNNECESSARY);
    }

    @Override
    public Money read(final CurrencyUnit currencyUnit, final BigDecimal amount) {
        return Money.of(currencyUnit, amount);
    }
}
