package tools.jackson.datatype.moneta;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Arrays;
import java.util.List;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;

import tools.jackson.core.JacksonException;

import tools.jackson.databind.*;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.datatype.moneta.MonetaMoneyModule;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public final class MonetaryAmountDeserializerTest<M extends MonetaryAmount> {

    @SuppressWarnings("unused")
    private static List<Arguments> data() {
        return Arrays.asList(
                arguments(Money.class, (Configurer) module -> module),
                arguments(FastMoney.class, (Configurer) module -> new MonetaMoneyModule().withFastMoney()),
                arguments(Money.class, (Configurer) module -> new MonetaMoneyModule().withMoney()),
                arguments(RoundedMoney.class, (Configurer) module -> new MonetaMoneyModule().withRoundedMoney()),
                arguments(RoundedMoney.class, (Configurer) module -> module.withRoundedMoney(Monetary.getDefaultRounding())));
    }

    private ObjectMapper unit(final Configurer configurer) {
        return builder(configurer).build();
    }

    private JsonMapper.Builder builder(final Configurer configurer) {
        return builder(module(configurer));
    }

    private JsonMapper.Builder builder(final JacksonModule module) {
        return JsonMapper.builder().addModule(module);
    }

    private MonetaMoneyModule module(final Configurer configurer) {
        return configurer.configure(new MonetaMoneyModule());
    }

    @Test
    public void shouldDeserializeMoneyByDefault() throws Exception {
        final ObjectMapper unit = JsonMapper.builder().addModule(new MonetaMoneyModule()).build();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount).isInstanceOf(Money.class);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserializeToCorrectType(final Class<M> type, final Configurer configurer) throws Exception {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount).isInstanceOf(type);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserialize(final Class<M> type, final Configurer configurer) throws Exception {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo(new BigDecimal("29.95"));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo("EUR");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserializeWithHighNumberOfFractionDigits(final Class<M> type,
                                                                final Configurer configurer) throws Exception {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.9501,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo(new BigDecimal("29.9501"));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo("EUR");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserializeCorrectlyWhenAmountIsAStringValue(final Class<M> type,
                                                                   final Configurer configurer) throws Exception {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"currency\":\"EUR\",\"amount\":\"29.95\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo(new BigDecimal("29.95"));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo(("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserializeCorrectlyWhenPropertiesAreInDifferentOrder(final Class<M> type,
                                                                            final Configurer configurer) throws Exception {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"currency\":\"EUR\",\"amount\":29.95}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo((new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo(("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserializeWithCustomNames(final Class<M> type, final Configurer configurer) throws Exception {
        final ObjectMapper unit = builder(module(configurer)
                .withAmountFieldName("value")
                .withCurrencyFieldName("unit")).build();

        final String content = "{\"value\":29.95,\"unit\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo((new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo(("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldIgnoreFormattedValue(final Class<M> type, final Configurer configurer) throws Exception {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"30.00 EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo((new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo(("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldUpdateExistingValueUsingTreeTraversingParser(final Class<M> type,
                                                                   final Configurer configurer) throws Exception {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount).isNotNull();

        // we need a json node to get a TreeTraversingParser with codec of type ObjectReader
        final JsonNode ownerNode =
                unit.readTree("{\"value\":{\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"30.00EUR\"}}");

        final Owner owner = new Owner();
        owner.setValue(amount);

        // try to update
        final Owner result = unit.readerForUpdating(owner).readValue(ownerNode);
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo((amount));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldFailToDeserializeWithoutAmount(final Class<M> type, final Configurer configurer) {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"currency\":\"EUR\"}";

        final JacksonException exception = assertThrows(
                JacksonException.class, () -> unit.readValue(content, type));

        assertThat(exception.getMessage()).contains("Missing property: 'amount'");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldFailToDeserializeWithoutCurrency(final Class<M> type, final Configurer configurer) {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95}";

        final MismatchedInputException exception = assertThrows(
                MismatchedInputException.class, () -> unit.readValue(content, type));

        assertThat(exception.getMessage()).contains("Missing property: 'currency'");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldFailToDeserializeWithAdditionalProperties(final Class<M> type,
            final Configurer configurer) {
        final ObjectMapper unit = builder(configurer)
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\",\"version\":\"1\"}";

        final JacksonException exception = assertThrows(
                UnrecognizedPropertyException.class, () -> unit.readValue(content, type));

        assertThat(exception.getMessage()).startsWith(
                "Unrecognized property \"version\" (class javax.money.MonetaryAmount), " +
                        "not marked as ignorable (3 known properties: \"amount\", \"currency\", \"formatted\"])");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldNotFailToDeserializeWithAdditionalProperties(final Class<M> type,
            final Configurer configurer) throws Exception {
        final ObjectMapper unit = builder(configurer)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        final String content = "{\"source\":{\"provider\":\"ECB\",\"date\":\"2016-09-29\"},\"amount\":29.95,\"currency\":\"EUR\",\"version\":\"1\"}";
        unit.readValue(content, type);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserializeWithTypeInformation(final Class<M> type, final Configurer configurer) throws Exception {
        final ObjectMapper unit = builder(configurer)
                .activateDefaultTyping(
                        BasicPolymorphicTypeValidator.builder().build(),
                        DefaultTyping.OBJECT_AND_NON_CONCRETE,
                        JsonTypeInfo.As.EXISTING_PROPERTY)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        final String content = "{\"type\":\"org.javamoney.moneta.Money\",\"amount\":29.95,\"currency\":\"EUR\"}";
        final M amount = unit.readValue(content, type);

        // type information is ignored?!
        assertThat(amount).isInstanceOf(type);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserializeWithoutTypeInformation(final Class<M> type, final Configurer configurer) throws Exception {
        final ObjectMapper unit = builder(configurer).activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().build())
                .build();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final M amount = unit.readValue(content, type);

        assertThat(amount).isInstanceOf(type);
    }

    interface Configurer {
        MonetaMoneyModule configure(MonetaMoneyModule module);
    }

    static class Owner {
        private MonetaryAmount value;

        MonetaryAmount getValue() {
            return value;
        }

        void setValue(final MonetaryAmount value) {
            this.value = value;
        }
    }
}
