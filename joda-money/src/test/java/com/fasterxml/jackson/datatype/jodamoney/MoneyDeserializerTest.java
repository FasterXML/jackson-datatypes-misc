package com.fasterxml.jackson.datatype.jodamoney;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

import static com.fasterxml.jackson.datatype.jodamoney.AmountRepresentation.*;

public final class MoneyDeserializerTest extends ModuleTestBase
{

    /*
    /**********************************************************************
    /* Tests, happy path
    /**********************************************************************
     */

    @CsvSource(value = {
        "{'amount':19.99,'currency':'EUR'} | EUR | 19.99",
        "{'amount':19.999,'currency':'KWD'} | KWD | 19.999",
        "{'amount':19,'currency':'JPY'} | JPY | 19",
        "{'amount':19.9,'currency':'EUR'} | EUR | 19.90",
        "{'amount':-19.5,'currency':'EUR'} | EUR | -19.50",
        "{'amount':0,'currency':'EUR'} | EUR | 0",
        "{'amount':'19.99','currency':'EUR'} | EUR | 19.99",
        "{'amount':'19.0','currency':'EUR'} | EUR | 19.00",
        "{'amount':'19','currency':'EUR'} | EUR | 19.00",
        "{'currency':'EUR','amount':'19.50'} | EUR | 19.50",
    }, delimiterString = "|")
    @ParameterizedTest(name = "should deserialize {0} as {1} {2}")
    public void testShouldDeserialize(
        String json,
        String currencyCode,
        BigDecimal amount
    ) throws Exception {

        final ObjectMapper mapper = mapperWithModule();
        final ObjectReader reader = mapper.readerFor(Money.class);

        final Money actual = reader.readValue(json(json));

        assertEquals(Money.of(CurrencyUnit.of(currencyCode), amount), actual);
    }

    @CsvSource(value = {
        "{'amount':19.99,'currency':'EUR'} | EUR | 19.99",
        "{'amount':19.999,'currency':'KWD'} | KWD | 19.999",
        "{'amount':19,'currency':'JPY'} | JPY | 19",
        "{'amount':19.9,'currency':'EUR'} | EUR | 19.90",
        "{'amount':-19.5,'currency':'EUR'} | EUR | -19.50",
        "{'amount':0,'currency':'EUR'} | EUR | 0",
        "{'amount':'19.99','currency':'EUR'} | EUR | 19.99",
        "{'amount':'19.0','currency':'EUR'} | EUR | 19.00",
        "{'amount':'19','currency':'EUR'} | EUR | 19.00",
        "{'currency':'EUR','amount':'19.50'} | EUR | 19.50",
    }, delimiterString = "|")
    @ParameterizedTest(name = "should deserialize {0} as {1} {2}")
    public void testShouldDeserializeDecimalNumberAmount(
        String json,
        String currencyCode,
        BigDecimal amount
    ) throws Exception {

        final ObjectMapper mapper = mapperWithModule(m -> m.withAmountRepresentation(DECIMAL_NUMBER));
        final ObjectReader reader = mapper.readerFor(Money.class);

        final Money actual = reader.readValue(json(json));

        assertEquals(Money.of(CurrencyUnit.of(currencyCode), amount), actual);
    }

    @CsvSource(value = {
        "{'amount':'19.99','currency':'EUR'} | EUR | 19.99",
        "{'amount':'19.999','currency':'KWD'} | KWD | 19.999",
        "{'amount':'19','currency':'JPY'} | JPY | 19",
        "{'amount':'19.9','currency':'EUR'} | EUR | 19.90",
        "{'amount':'-19.5','currency':'EUR'} | EUR | -19.50",
        "{'amount':'0','currency':'EUR'} | EUR | 0",
        "{'amount':'19.0','currency':'EUR'} | EUR | 19.00",
        "{'amount':'19','currency':'EUR'} | EUR | 19.00",
        "{'currency':'EUR','amount':'19.50'} | EUR | 19.50",
    }, delimiterString = "|")
    @ParameterizedTest(name = "should deserialize {0} as {1} {2}")
    public void testShouldDeserializeDecimalStringAmount(
        String json,
        String currencyCode,
        BigDecimal amount
    ) throws Exception {

        final ObjectMapper mapper = mapperWithModule(m -> m.withAmountRepresentation(DECIMAL_STRING));
        final ObjectReader reader = mapper.readerFor(Money.class);

        final Money actual = reader.readValue(json(json));

        assertEquals(Money.of(CurrencyUnit.of(currencyCode), amount), actual);
    }

    @CsvSource(value = {
        "{'amount':1999,'currency':'EUR'} | EUR | 19.99",
        "{'amount':19999,'currency':'KWD'} | KWD | 19.999",
        "{'amount':19,'currency':'JPY'} | JPY | 19",
        "{'amount':1990,'currency':'EUR'} | EUR | 19.90",
        "{'amount':-1950,'currency':'EUR'} | EUR | -19.50",
        "{'amount':0,'currency':'EUR'} | EUR | 0",
        "{'amount':'-1950','currency':'EUR'} | EUR | -19.50",
        "{'amount':'1900.00','currency':'EUR'} | EUR | 19.00",
        "{'currency':'EUR','amount':1950} | EUR | 19.50",
    }, delimiterString = "|")
    @ParameterizedTest(name = "should deserialize {0} as {1} {2}")
    public void testShouldDeserializeAmountInMinorCurrencyUnit(
        String json,
        String currencyCode,
        BigDecimal amount
    ) throws Exception {

        final ObjectMapper mapper = mapperWithModule(m -> m.withAmountRepresentation(MINOR_CURRENCY_UNIT));
        final ObjectReader reader = mapper.readerFor(Money.class);

        final Money actual = reader.readValue(json(json));

        assertEquals(Money.of(CurrencyUnit.of(currencyCode), amount), actual);
    }

    /*
    /**********************************************************************
    /* Tests, fail handling
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();
    private final ObjectReader R = MAPPER.readerFor(Money.class);

    @Test
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

    @Test
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

    @Test
    public void testShouldFailDeserializationWithUnknownProperties() throws Exception
    {
        final ObjectReader r = MAPPER.readerFor(Money.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        final String content = "{\"amount\":5000,\"currency\":\"EUR\",\"unknown\":\"test\"}";

        try {
            final Money amount = r.readValue(content);
            fail("Should not pass but got: "+amount);
        } catch (final UnrecognizedPropertyException e) {
            verifyException(e, "Unrecognized field \"unknown\"");
            verifyException(e, "2 known properties: \"amount\", \"currency\"]");
        }
    }

    @Test
    public void testShouldPerformDeserializationWithUnknownProperties() throws IOException
    {
        final ObjectReader r = MAPPER.readerFor(Money.class)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        final String content = json("{'amount':5000,'currency':'EUR','unknown':'test'}");
        final Money actualAmount = r.readValue(content);
        assertEquals(Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(5000)), actualAmount);
    }
}
