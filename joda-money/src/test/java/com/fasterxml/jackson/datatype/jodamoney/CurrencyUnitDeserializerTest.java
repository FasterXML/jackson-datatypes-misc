package com.fasterxml.jackson.datatype.jodamoney;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import org.joda.money.CurrencyUnit;

public final class CurrencyUnitDeserializerTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    public void testShouldDeserialize() throws Exception
    {
        assertEquals(CurrencyUnit.EUR,
                MAPPER.readValue("\"EUR\"", CurrencyUnit.class));
    }

    public void testShouldNotDeserializeInvalidCurrency() throws Exception
    {
        try {
            MAPPER.readValue("\"UNKNOWN\"", CurrencyUnit.class);
            fail("Should not pass");
        } catch (final InvalidFormatException e) {
            verifyException(e, "Cannot deserialize value of type `org.joda.money.CurrencyUnit");
            verifyException(e, "from String \"UNKNOWN\": Unknown currency");
        }
    }
}
