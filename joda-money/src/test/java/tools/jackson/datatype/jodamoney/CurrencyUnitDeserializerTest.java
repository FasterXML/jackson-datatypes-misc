package tools.jackson.datatype.jodamoney;

import org.junit.jupiter.api.Test;

import org.joda.money.CurrencyUnit;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;

import static org.junit.jupiter.api.Assertions.*;

public final class CurrencyUnitDeserializerTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testShouldDeserialize() throws Exception
    {
        assertEquals(CurrencyUnit.EUR,
                MAPPER.readValue("\"EUR\"", CurrencyUnit.class));
    }

    @Test
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
