
package com.fasterxml.jackson.datatype.money;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public final class MonetaryAmountDeserializerTest<M extends MonetaryAmount> {

    @SuppressWarnings("unused")
    private static List<Arguments> data() {
        return Arrays.asList(
                arguments((Configurer) module -> module),
                arguments((Configurer) module -> new JavaxMoneyModule().withMonetaryAmountFactory(FastMoney::of)),
                arguments((Configurer) module -> new JavaxMoneyModule().withMonetaryAmountFactory(Money::of)),
                arguments((Configurer) module -> new JavaxMoneyModule().withMonetaryAmountFactory(RoundedMoney::of)),
                arguments((Configurer) module ->
                        new JavaxMoneyModule().withMonetaryAmountFactory((amount, currency) ->
                                RoundedMoney.of(amount, currency, Monetary.getDefaultRounding()))

                )
        );
    }

    private interface Configurer {
        JavaxMoneyModule configure(JavaxMoneyModule module);
    }

    private ObjectMapper unit(final Configurer configurer) {
        return unit(module(configurer));
    }

    private ObjectMapper unit(final Module module) {
        return new ObjectMapper().registerModule(module);
    }

    private JavaxMoneyModule module(final Configurer configurer) {
        return configurer.configure(new JavaxMoneyModule());
    }


    @Test
    public void shouldDeserializeMoneyByDefault() throws IOException {
        final ObjectMapper unit = JsonMapper.builder().addModule(new JavaxMoneyModule()).build();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount).isInstanceOf(Money.class);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserialize(final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo(new BigDecimal("29.95"));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo("EUR");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserializeWithHighNumberOfFractionDigits(
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.9501,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo(new BigDecimal("29.9501"));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo("EUR");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserializeCorrectlyWhenAmountIsAStringValue(
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"currency\":\"EUR\",\"amount\":\"29.95\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo(new BigDecimal("29.95"));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo(("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserializeCorrectlyWhenPropertiesAreInDifferentOrder(
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"currency\":\"EUR\",\"amount\":29.95}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo((new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo(("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserializeWithCustomNames(final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(module(configurer)
                .withAmountFieldName("value")
                .withCurrencyFieldName("unit"));

        final String content = "{\"value\":29.95,\"unit\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo((new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo(("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldIgnoreFormattedValue(final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"30.00 EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo((new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo(("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldUpdateExistingValueUsingTreeTraversingParser(
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

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

    private static class Owner {

        private MonetaryAmount value;

        MonetaryAmount getValue() {
            return value;
        }

        void setValue(final MonetaryAmount value) {
            this.value = value;
        }

    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldFailToDeserializeWithoutAmount(final Configurer configurer) {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"currency\":\"EUR\"}";

        final JsonProcessingException exception = assertThrows(
                JsonProcessingException.class, () -> unit.readValue(content, MonetaryAmount.class));

        assertThat(exception.getMessage()).contains("Missing property: 'amount'");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldFailToDeserializeWithoutCurrency(final Configurer configurer) {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95}";

        final MismatchedInputException exception = assertThrows(
                MismatchedInputException.class, () -> unit.readValue(content, MonetaryAmount.class));

        assertThat(exception.getMessage()).contains("Missing property: 'currency'");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldFailToDeserializeWithAdditionalProperties(
            final Configurer configurer) {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\",\"version\":\"1\"}";

        final JsonProcessingException exception = assertThrows(
                UnrecognizedPropertyException.class, () -> unit.readValue(content, MonetaryAmount.class));

        assertThat(exception.getMessage()).startsWith(
                "Unrecognized field \"version\" (class javax.money.MonetaryAmount), " +
                        "not marked as ignorable (3 known properties: \"amount\", \"currency\", \"formatted\"])");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldNotFailToDeserializeWithAdditionalProperties(
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer).disable(FAIL_ON_UNKNOWN_PROPERTIES);

        final String content = "{\"source\":{\"provider\":\"ECB\",\"date\":\"2016-09-29\"},\"amount\":29.95,\"currency\":\"EUR\",\"version\":\"1\"}";
        unit.readValue(content, MonetaryAmount.class);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserializeWithTypeInformation(final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer)
                .activateDefaultTyping(
                        BasicPolymorphicTypeValidator.builder().build(),
                        ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE,
                        JsonTypeInfo.As.EXISTING_PROPERTY)
                .disable(FAIL_ON_UNKNOWN_PROPERTIES);

        final String content = "{\"type\":\"org.javamoney.moneta.Money\",\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        // type information is ignored?!
        assertThat(amount).isInstanceOf(MonetaryAmount.class);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldDeserializeWithoutTypeInformation(final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer).activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().build());

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount).isInstanceOf(MonetaryAmount.class);
    }

    @Test
    public void shouldDeserializeToAMonetaImplementationWithProvidedFactory() throws JsonProcessingException {

        //Custom FastMoney factory that returns zero
        final ObjectMapper unit = new ObjectMapper().registerModule(new JavaxMoneyModule().withMonetaryAmountFactory((number, currency) -> (FastMoney.zero(currency))));

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo(BigDecimal.ZERO);
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo("EUR");

    }

}
