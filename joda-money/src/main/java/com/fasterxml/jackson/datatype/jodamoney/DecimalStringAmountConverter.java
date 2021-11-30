package com.fasterxml.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;

/**
 * An {@link AmountConverter} converting {@link Money} to its amount represented as decimal string
 * (such as {@code "12.34"} for {@code Money.parse("USD 12.34")}), and back to {@code Money} from
 * this representation.
 */
final class DecimalStringAmountConverter implements AmountConverter {

    private static final DecimalStringAmountConverter INSTANCE = new DecimalStringAmountConverter();

    static DecimalStringAmountConverter getInstance() {
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
