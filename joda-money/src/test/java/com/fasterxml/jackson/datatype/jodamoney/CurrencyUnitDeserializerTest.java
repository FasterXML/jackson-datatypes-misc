package com.fasterxml.jackson.datatype.jodamoney;

import java.io.IOException;

import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class CurrencyUnitDeserializerTest extends ModuleTestBase
{
    private final ObjectMapper objectMapper = mapperWithModule();

    public void testShouldDeserialize() throws IOException {
        final CurrencyUnit actualCurrencyUnit = objectMapper.readValue("\"EUR\"", CurrencyUnit.class);

        assertEquals(CurrencyUnit.EUR, actualCurrencyUnit);
    }

    public void testShouldNotDeserializeInvalidCurrency() {
        try {
            objectMapper.readValue("\"UNKNOWN\"", CurrencyUnit.class);
            fail("Should not pass");
        } catch (final IllegalCurrencyException e) {
            verifyException(e, "Unknown currency 'UNKNOWN'");
        } catch (final IOException e) {
            fail("IllegalCurrencyException should have been thrown");
        }
    }
}
