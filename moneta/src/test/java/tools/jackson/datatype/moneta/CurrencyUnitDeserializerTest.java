package tools.jackson.datatype.moneta;

import javax.money.CurrencyUnit;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import org.javamoney.moneta.CurrencyUnitBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CurrencyUnitDeserializerTest {

    private final ObjectMapper unit = JsonMapper.builder().addModule(new MonetaMoneyModule()).build();

    @Test
    public void shouldDeserialize() throws Exception {
        final CurrencyUnit actual = unit.readValue("\"EUR\"", CurrencyUnit.class);
        final CurrencyUnit expected = CurrencyUnitBuilder.of("EUR", "default").build();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldNotDeserializeInvalidCurrency() {
        final InvalidFormatException e = assertThrows(InvalidFormatException.class, () ->
                unit.readValue(a2q("'FOO'"), CurrencyUnit.class));
        assertThat(e.getMessage()).contains("FOO");
    }

    @Test
    public void shouldNotDeserializeFromNumber() {
        assertThrows(MismatchedInputException.class, () ->
                unit.readValue("12", CurrencyUnit.class));
    }

    @Test
    public void shouldNotDeserializeFromObject() {
        assertThrows(MismatchedInputException.class, () ->
                unit.readValue(a2q("{'a':1}"), CurrencyUnit.class));
    }

    @Test
    public void shouldNotDeserializeFromArray() {
        assertThrows(MismatchedInputException.class, () ->
                unit.readValue("[1,2]", CurrencyUnit.class));
    }

    @Test
    public void shouldDeserializeWithTyping() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new MonetaMoneyModule())
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build())
                .build();

        final CurrencyUnit actual = mapper.readValue("\"EUR\"", CurrencyUnit.class);
        final CurrencyUnit expected = CurrencyUnitBuilder.of("EUR", "default").build();

        assertThat(actual).isEqualTo(expected);
    }

    private static String a2q(final String json) {
        return json.replace("'", "\"");
    }
}
