package com.fasterxml.jackson.datatype.jodamoney;

import java.io.IOException;
import java.math.BigDecimal;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public final class MoneyDeserializerTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();
    private final ObjectReader R = MAPPER.readerFor(Money.class);

    /*
    /**********************************************************************
    /* Tests, happy path
    /**********************************************************************
     */
    
    public void testShouldDeserialize() throws IOException
    {

        final String content = "{\"amount\":19.99,\"currency\":\"EUR\"}";
        final Money actualAmount = R.readValue(content);

        assertEquals(Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(19.99)), actualAmount);
        assertEquals(BigDecimal.valueOf(19.99), actualAmount.getAmount());
        assertEquals(actualAmount.getCurrencyUnit().getCode(), "EUR");
    }

    public void testShouldDeserializeWhenAmountIsAStringValue() throws IOException
    {
        final String content = "{\"currency\":\"EUR\",\"amount\":\"19.99\"}";
        final Money actualAmount = R.readValue(content);

        assertEquals(BigDecimal.valueOf(19.99), actualAmount.getAmount());
        assertEquals(actualAmount.getCurrencyUnit().getCode(), "EUR");
    }

    public void testShouldDeserializeWhenOrderIsDifferent() throws IOException
    {
        final String content = "{\"currency\":\"EUR\",\"amount\":19.99}";
        final Money actualAmount = R.readValue(content);

        assertEquals(BigDecimal.valueOf(19.99), actualAmount.getAmount());
        assertEquals(actualAmount.getCurrencyUnit().getCode(), "EUR");
    }

    /*
    /**********************************************************************
    /* Tests, fail handling
    /**********************************************************************
     */

    public void testShouldFailDeserializationWithoutAmount() throws Exception
    {
        final String content = "{\"currency\":\"EUR\"}";

        try {
            final Money amount = R.readValue(content);
            fail("Should not pass but got: "+amount);
        } catch (final MismatchedInputException e) {
            verifyException(e, "Property 'amount' missing from Object value");
        }
    }

    public void testShouldFailDeserializationWithoutCurrency() throws Exception
    {
        final String content = "{\"amount\":5000}";

        try {
            final Money amount = R.readValue(content);
            fail("Should not pass but got: "+amount);
        } catch (final MismatchedInputException e) {
            verifyException(e, "Property 'currency' missing from Object value");
        }
    }

    public void testShouldFailDeserializationWithUnknownProperties()
    {
        final ObjectReader r = MAPPER.readerFor(Money.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        final String content = "{\"amount\":5000,\"currency\":\"EUR\",\"unknown\":\"test\"}";

        try {
            final Money amount = r.readValue(content);
            fail("Should not pass but got: "+amount);
        } catch (final IOException e) {
            verifyException(e, "Unrecognized field \"unknown\"");
            verifyException(e, "2 known properties: \"amount\", \"currency\"]");
        }
    }

    public void testShouldPerformDeserializationWithUnknownProperties() throws IOException
    {
        final ObjectReader r = MAPPER.readerFor(Money.class)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        final String content = a2q("{'amount':5000,'currency':'EUR','unknown':'test'}");
        final Money actualAmount = r.readValue(content);
        assertEquals(Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(5000)), actualAmount);
    }
}
