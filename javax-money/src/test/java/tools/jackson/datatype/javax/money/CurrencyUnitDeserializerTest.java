package tools.jackson.datatype.javax.money;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.CurrencyUnitBuilder;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

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
        final InvalidFormatException e = assertThrows(InvalidFormatException.class, () ->
                unit.readValue("\"FOO\"", CurrencyUnit.class));
        assertThat(e.getMessage()).contains("javax.money.CurrencyUnit", "\"FOO\"",
                "not a valid currency code");
        assertThat(e.getValue()).isEqualTo("FOO");
    }

    @Test
    public void shouldNotDeserializeInvalidCurrencyWithinAmount() {
        assertThrows(InvalidFormatException.class, () ->
                unit.readValue("{\"amount\":1,\"currency\":\"FOO\"}", MonetaryAmount.class));
    }

    // [datatypes-misc#91] Non-String input must fail with Jackson exception, not NPE
    @Test
    public void shouldFailOnNonStringInput() {
        for (String json : new String[] { "12", "true", "{}", "{\"x\":1}", "[]", "[\"EUR\"]" }) {
            final MismatchedInputException e = assertThrows(MismatchedInputException.class,
                    () -> unit.readValue(json, CurrencyUnit.class), json);
            assertThat(e.getMessage()).contains("javax.money.CurrencyUnit");
        }
    }

    @Test
    public void shouldFailOnNonStringInputWithinAmount() {
        for (String currency : new String[] { "1", "{\"x\":1}", "[\"EUR\"]" }) {
            final String json = "{\"amount\":1,\"currency\":" + currency + "}";
            final MismatchedInputException e = assertThrows(MismatchedInputException.class,
                    () -> unit.readValue(json, MonetaryAmount.class), json);
            assertThat(e.getMessage()).contains("javax.money.CurrencyUnit");
        }
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
