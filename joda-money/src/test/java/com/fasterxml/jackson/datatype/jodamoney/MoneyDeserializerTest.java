package com.fasterxml.jackson.datatype.jodamoney;

import java.io.IOException;
import java.math.BigDecimal;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public final class MoneyDeserializerTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    public void testShouldDeserialize() throws IOException {

        final String content = "{\"amount\":19.99,\"currency\":\"EUR\"}";
        final Money actualAmount = MAPPER.readValue(content, Money.class);

        assertEquals(Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(19.99)), actualAmount);
        assertEquals(BigDecimal.valueOf(19.99), actualAmount.getAmount());
        assertEquals(actualAmount.getCurrencyUnit().getCode(), "EUR");
    }

    public void testShouldDeserializeWhenAmountIsAStringValue() throws IOException {
        final String content = "{\"currency\":\"EUR\",\"amount\":\"19.99\"}";
        final Money actualAmount = MAPPER.readValue(content, Money.class);

        assertEquals(BigDecimal.valueOf(19.99), actualAmount.getAmount());
        assertEquals(actualAmount.getCurrencyUnit().getCode(), "EUR");
    }

    public void testShouldDeserializeWhenOrderIsDifferent() throws IOException {
        final String content = "{\"currency\":\"EUR\",\"amount\":19.99}";
        final Money actualAmount = MAPPER.readValue(content, Money.class);

        assertEquals(BigDecimal.valueOf(19.99), actualAmount.getAmount());
        assertEquals(actualAmount.getCurrencyUnit().getCode(), "EUR");
    }

    public void testShouldFailDeserializationWithoutAmount()
    {
        final String content = "{\"currency\":\"EUR\"}";

        try {
            MAPPER.readValue(content, Money.class);
            fail();
        } catch (final NullPointerException e) {
            verifyException(e, "Amount must not be null");
        } catch (final IOException e) {
            fail("NullPointerException should have been thrown");
        }
    }

    public void testShouldFailDeserializationWithoutCurrency()
    {
        final String content = "{\"amount\":5000}";

        try {
            MAPPER.readValue(content, Money.class);
            fail();
        } catch (final NullPointerException e) {
            verifyException(e, "Currency must not be null");
        } catch (final IOException e) {
            fail("NullPointerException should have been thrown");
        }
    }

    public void testShouldFailDeserializationWithUnknownProperties() {
        final ObjectReader r = MAPPER.readerFor(Money.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        final String content = "{\"amount\":5000,\"currency\":\"EUR\",\"unknown\":\"test\"}";

        try {
            Money result = r.readValue(content);
            fail("Should pass but got: "+result);
        } catch (final IOException e) {
            verifyException(e, "test");
        }
    }

    public void testShouldPerformDeserializationWithUnknownProperties() throws IOException {
        final ObjectReader r = MAPPER.readerFor(Money.class)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        final String content = "{\"amount\":5000,\"currency\":\"EUR\",\"unknown\":\"test\"}";
        final Money actualAmount = r.readValue(content);
        assertEquals(Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(5000)), actualAmount);
    }
}
