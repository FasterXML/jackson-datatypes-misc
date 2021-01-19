package com.fasterxml.jackson.datatype.jodamoney;

import java.math.BigDecimal;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public final class MoneyDeserializerTest extends ModuleTestBase
{
    public void testShouldDeserialize() {
        final ObjectMapper objectMapper = mapperWithModule();

        final String content = "{\"amount\":19.99,\"currency\":\"EUR\"}";
        final Money actualAmount = objectMapper.readValue(content, Money.class);

        assertEquals(Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(19.99)), actualAmount);
        assertEquals(BigDecimal.valueOf(19.99), actualAmount.getAmount());
        assertEquals(actualAmount.getCurrencyUnit().getCode(), "EUR");
    }

    public void testShouldDeserializeWhenAmountIsAStringValue() {
        final ObjectMapper objectMapper = mapperWithModule();

        final String content = "{\"currency\":\"EUR\",\"amount\":\"19.99\"}";
        final Money actualAmount = objectMapper.readValue(content, Money.class);

        assertEquals(BigDecimal.valueOf(19.99), actualAmount.getAmount());
        assertEquals(actualAmount.getCurrencyUnit().getCode(), "EUR");
    }

    public void testShouldDeserializeWhenOrderIsDifferent() {
        final ObjectMapper objectMapper = mapperWithModule();

        final String content = "{\"currency\":\"EUR\",\"amount\":19.99}";
        final Money actualAmount = objectMapper.readValue(content, Money.class);

        assertEquals(BigDecimal.valueOf(19.99), actualAmount.getAmount());
        assertEquals(actualAmount.getCurrencyUnit().getCode(), "EUR");
    }

    public void testShouldFailDeserializationWithoutAmount() {
        final ObjectMapper objectMapper = mapperWithModule();

        final String content = "{\"currency\":\"EUR\"}";

        try {
            objectMapper.readValue(content, Money.class);
            fail();
        } catch (final NullPointerException e) {
            verifyException(e, "Amount must not be null");
        }
    }

    public void testShouldFailDeserializationWithoutCurrency() {
        final ObjectMapper objectMapper = mapperWithModule();

        final String content = "{\"amount\":5000}";

        try {
            objectMapper.readValue(content, Money.class);
            fail();
        } catch (final NullPointerException e) {
            verifyException(e, "Currency must not be null");
        }
    }

    public void testShouldFailDeserializationWithUnknownProperties() {
        final ObjectMapper objectMapper = mapperWithModuleBuilder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        final String content = "{\"amount\":5000,\"currency\":\"EUR\",\"unknown\":\"test\"}";

        try {
            objectMapper.readValue(content, Money.class);
            fail();
        } catch (final JacksonException e) {
            verifyException(e, "test");
        }
    }

    public void testShouldPerformDeserializationWithUnknownProperties()
    {
        final ObjectMapper objectMapper = mapperWithModuleBuilder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        final String content = "{\"amount\":5000,\"currency\":\"EUR\",\"unknown\":\"test\"}";

        final Money actualAmount = objectMapper.readValue(content, Money.class);

        assertEquals(Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(5000)), actualAmount);
    }
}
