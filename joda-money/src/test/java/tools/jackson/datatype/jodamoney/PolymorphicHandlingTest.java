package tools.jackson.datatype.jodamoney;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import tools.jackson.databind.*;

import static org.junit.jupiter.api.Assertions.*;

public class PolymorphicHandlingTest extends ModuleTestBase
{
    @Test
    public void testPolymorphicMoney() throws Exception {
        ObjectMapper mapper = polyMapperFor(Money.class);
        final Money input = Money.of(CurrencyUnit.CAD, BigDecimal.valueOf(17.25));

        String json = mapper.writeValueAsString(input);

        Money result = mapper.readValue(json, Money.class);
        assertEquals(input, result);
        assertEquals(CurrencyUnit.CAD, result.getCurrencyUnit());
        assertEquals(17, result.getAmountMajorInt());
        assertEquals(1725, result.getAmountMinorInt());
    }

    @Test
    public void testPolymorphicCurrency() throws Exception {
        ObjectMapper mapper = polyMapperFor(CurrencyUnit.class);
        String json = mapper.writeValueAsString(CurrencyUnit.EUR);
        
        assertEquals(CurrencyUnit.EUR,
                mapper.readValue(json, CurrencyUnit.class));

    }
    
    private ObjectMapper polyMapperFor(Class<?> target) {
        return mapperWithModuleBuilder()
                .addMixIn(target, ForceJsonTypeInfo.class)
                .build();
    }
}
