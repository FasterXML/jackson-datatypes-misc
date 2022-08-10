package tools.jackson.datatype.jodamoney;

import static tools.jackson.datatype.jodamoney.AmountRepresentation.*;

import java.math.BigDecimal;

import tools.jackson.databind.ObjectMapper;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public final class MoneySerializerTest extends ModuleTestBase
{
    @Test
    @Parameters({
        "EUR, 19.99, 19.99",
        "KWD, 19.999, 19.999",
        "JPY, 19, 19",
        "EUR, 19.9, 19.90",
        "EUR, -19.5, -19.50",
        "EUR, 0, 0.00",
    })
    @TestCaseName("should serialize {0} {1} with amount representation {2}")
    public void testShouldSerialize(
        String currencyCode,
        BigDecimal amount,
        String amountInJson
    ) throws Exception {
        final ObjectMapper mapper = mapperWithModule();

        assertEquals(json("{'amount':%s,'currency':'%s'}", amountInJson, currencyCode),
            mapper.writeValueAsString(Money.of(CurrencyUnit.of(currencyCode), amount)));
    }

    @Test
    @Parameters({
        "EUR, 19.99, 19.99",
        "KWD, 19.999, 19.999",
        "JPY, 19, 19",
        "EUR, 19.9, 19.90",
        "EUR, -19.5, -19.50",
        "EUR, 0, 0.00",
    })
    @TestCaseName("should serialize {0} {1} with amount representation {2}")
    public void testShouldSerializeAmountAsDecimalNumber(
        String currencyCode,
        BigDecimal amount,
        String amountInJson
    ) throws Exception {

        final ObjectMapper mapper = mapperWithModule(m -> m.withAmountRepresentation(DECIMAL_NUMBER));

        assertEquals(json("{'amount':%s,'currency':'%s'}", amountInJson, currencyCode),
            mapper.writeValueAsString(Money.of(CurrencyUnit.of(currencyCode), amount)));
    }

    @Test
    @Parameters({
        "EUR, 19.99, 19.99",
        "KWD, 19.999, 19.999",
        "JPY, 19, 19",
        "EUR, 19.9, 19.90",
        "EUR, -19.5, -19.50",
        "EUR, 0, 0.00",
    })
    @TestCaseName("should serialize {0} {1} with amount representation {2}")
    public void testShouldSerializeAmountAsDecimalString(
        String currencyCode,
        BigDecimal amount,
        String amountInJson
    ) throws Exception {

        final ObjectMapper mapper = mapperWithModule(m -> m.withAmountRepresentation(DECIMAL_STRING));

        assertEquals(json("{'amount':'%s','currency':'%s'}", amountInJson, currencyCode),
            mapper.writeValueAsString(Money.of(CurrencyUnit.of(currencyCode), amount)));
    }

    @Test
    @Parameters({
        "EUR, 19.99, 1999",
        "KWD, 19.999, 19999",
        "JPY, 19, 19",
        "EUR, 19.9, 1990",
        "EUR, -19.5, -1950",
        "EUR, 0, 0",
    })
    @TestCaseName("should serialize {0} {1} with amount representation {2}")
    public void testShouldSerializeAmountInMinorCurrencyUnit(
        String currencyCode,
        BigDecimal amount,
        String amountInJson
    ) throws Exception {

        final ObjectMapper mapper = mapperWithModule(m -> m.withAmountRepresentation(MINOR_CURRENCY_UNIT));

        assertEquals(json("{'amount':%s,'currency':'%s'}", amountInJson, currencyCode),
            mapper.writeValueAsString(Money.of(CurrencyUnit.of(currencyCode), amount)));
    }
}
