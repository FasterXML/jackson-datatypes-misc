package com.fasterxml.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;

public interface AmountConverter<T> {

    T fromMoney(Money money);

    Money toMoney(CurrencyUnit currencyUnit, BigDecimal amount);
}