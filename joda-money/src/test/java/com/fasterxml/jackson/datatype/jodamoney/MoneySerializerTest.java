package com.fasterxml.jackson.datatype.jodamoney;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public final class MoneySerializerTest extends ModuleTestBase
{
    public void testShouldSerialize() {
        final ObjectMapper mapper = mapperWithModule();
        assertEquals("{\"amount\":19.99,\"currency\":\"EUR\"}",
                mapper.writeValueAsString(Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(19.99))));
    }
}
