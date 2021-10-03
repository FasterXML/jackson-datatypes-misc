package com.fasterxml.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;

public interface AmountRepresenter<T> {

    T write(Money money);

    Money read(CurrencyUnit currencyUnit, BigDecimal amount);
}
