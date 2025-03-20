package tools.jackson.datatype.javax.money;

import javax.money.CurrencyUnit;
import javax.money.UnknownCurrencyException;

import org.javamoney.moneta.CurrencyUnitBuilder;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import tools.jackson.datatype.javax.money.JavaxMoneyModule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CurrencyUnitDeserializerTest {

    private final ObjectMapper unit = JsonMapper.builder().addModule(new JavaxMoneyModule()).build();

    @Test
    public void shouldDeserialize() throws Exception {
        final CurrencyUnit actual = unit.readValue("\"EUR\"", CurrencyUnit.class);
        final CurrencyUnit expected = CurrencyUnitBuilder.of("EUR", "default").build();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldNotDeserializeInvalidCurrency() {
        assertThrows(UnknownCurrencyException.class, () ->
                unit.readValue("\"FOO\"", CurrencyUnit.class));
    }

    @Test
    public void shouldDeserializeWithTyping() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaxMoneyModule())
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build())
                .build();

        final CurrencyUnit actual = mapper.readValue("\"EUR\"", CurrencyUnit.class);
        final CurrencyUnit expected = CurrencyUnitBuilder.of("EUR", "default").build();

        assertThat(actual).isEqualTo(expected);
    }

}
