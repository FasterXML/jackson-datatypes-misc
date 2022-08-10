package tools.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;

import tools.jackson.databind.ObjectMapper;

public final class CurrencyUnitSerializerTest extends ModuleTestBase
{
    private final ObjectMapper objectMapper = mapperWithModule();

    public void testShouldSerialize()
    {
        final String expectedCurrencyUnit = "EUR";

        final String actual = objectMapper.writeValueAsString(CurrencyUnit.EUR);

        assertEquals('"' + expectedCurrencyUnit + '"', actual);
    }
}
