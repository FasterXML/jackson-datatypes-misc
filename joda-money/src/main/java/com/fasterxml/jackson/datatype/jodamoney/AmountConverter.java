package com.fasterxml.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;

/**
 * Common interface for amount conversion strategies used by {@link Money} (de)serializer.
 * Allows conversion of {@code Money} to implementation-specific representation of its amount,
 * and back to {@code Money}.
 */
interface AmountConverter {

    Object fromMoney(Money money);

    Money toMoney(CurrencyUnit currencyUnit, BigDecimal amount);
}
