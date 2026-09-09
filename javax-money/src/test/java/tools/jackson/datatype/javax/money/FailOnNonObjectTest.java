package tools.jackson.datatype.javax.money;

import java.util.List;

import javax.money.MonetaryAmount;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests to verify that non-Object input is reported as a regular
 * {@link MismatchedInputException}, and not as a bare {@link NullPointerException}
 * coming out of {@code MonetaryAmountDeserializer}.
 */
public final class FailOnNonObjectTest {

    private final ObjectMapper unit = JsonMapper.builder()
            .addModule(new JavaxMoneyModule())
            .build();

    @Test
    public void shouldFailOnNumber() {
        final MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> unit.readValue("12", MonetaryAmount.class));
        assertThat(e.getMessage()).contains("javax.money.MonetaryAmount", "from Integer value");
    }

    @Test
    public void shouldFailOnString() {
        final MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> unit.readValue(a2q("'abc'"), MonetaryAmount.class));
        assertThat(e.getMessage()).contains("javax.money.MonetaryAmount", "from String value");
    }

    @Test
    public void shouldFailOnArray() {
        final MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> unit.readValue("[1,2]", MonetaryAmount.class));
        assertThat(e.getMessage()).contains("javax.money.MonetaryAmount", "from Array value");
    }

    @Test
    public void shouldFailOnEmptyArray() {
        assertThrows(MismatchedInputException.class,
                () -> unit.readValue("[]", MonetaryAmount.class));
    }

    @Test
    public void shouldFailOnBooleanWithinList() {
        assertThrows(MismatchedInputException.class,
                () -> unit.readValue(a2q("[{'amount':1,'currency':'EUR'},true]"),
                        new TypeReference<List<MonetaryAmount>>() { }));
    }

    // ... while valid input keeps working
    @Test
    public void shouldStillDeserializeObject() {
        final MonetaryAmount amount = unit.readValue(a2q("{'amount':29.95,'currency':'EUR'}"),
                MonetaryAmount.class);
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo("EUR");
        assertThat(amount.getNumber().doubleValueExact()).isEqualTo(29.95);
    }

    @Test
    public void shouldStillDeserializeNull() {
        assertThat((MonetaryAmount) unit.readValue("null", MonetaryAmount.class)).isNull();
    }

    private static String a2q(final String json) {
        return json.replace("'", "\"");
    }
}
