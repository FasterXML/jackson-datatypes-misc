package com.fasterxml.jackson.datatype.money;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.io.IOException;
import java.math.BigDecimal;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static junitparams.JUnitParamsRunner.$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@RunWith(JUnitParamsRunner.class)
public final class MonetaryAmountDeserializerTest<M extends MonetaryAmount> {

    @SuppressWarnings("unused")
    private Object[] data() {
        return $($(Money.class, (Configurer) module -> module),
                $(FastMoney.class, (Configurer) module -> new JavaxMoneyModule().withFastMoney()),
                $(Money.class, (Configurer) module -> new JavaxMoneyModule().withMoney()),
                $(RoundedMoney.class, (Configurer) module -> new JavaxMoneyModule().withRoundedMoney()),
                $(RoundedMoney.class, (Configurer) module -> module.withRoundedMoney(Monetary.getDefaultRounding())));
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
        final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount).isInstanceOf(Money.class);
    }

    @Test
    @Parameters(method = "data")
    public void shouldDeserializeToCorrectType(final Class<M> type, final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount).isInstanceOf(type);
    }

    @Test
    @Parameters(method = "data")
    public void shouldDeserialize(final Class<M> type, final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo(new BigDecimal("29.95"));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo("EUR");
    }

    @Test
    @Parameters(method = "data")
    public void shouldDeserializeWithHighNumberOfFractionDigits(final Class<M> type,
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.9501,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo(new BigDecimal("29.9501"));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo("EUR");
    }

    @Test
    @Parameters(method = "data")
    public void shouldDeserializeCorrectlyWhenAmountIsAStringValue(final Class<M> type,
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"currency\":\"EUR\",\"amount\":\"29.95\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo(new BigDecimal("29.95"));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo(("EUR"));
    }

    @Test
    @Parameters(method = "data")
    public void shouldDeserializeCorrectlyWhenPropertiesAreInDifferentOrder(final Class<M> type,
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"currency\":\"EUR\",\"amount\":29.95}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo((new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo(("EUR"));
    }

    @Test
    @Parameters(method = "data")
    public void shouldDeserializeWithCustomNames(final Class<M> type, final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(module(configurer)
                .withAmountFieldName("value")
                .withCurrencyFieldName("unit"));

        final String content = "{\"value\":29.95,\"unit\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo((new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo(("EUR"));
    }

    @Test
    @Parameters(method = "data")
    public void shouldIgnoreFormattedValue(final Class<M> type, final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"30.00 EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class)).isEqualTo((new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode()).isEqualTo(("EUR"));
    }

    @Test
    @Parameters(method = "data")
    public void shouldUpdateExistingValueUsingTreeTraversingParser(final Class<M> type,
            final Configurer configurer) throws IOException {
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

    private static class Owner {

        private MonetaryAmount value;

        MonetaryAmount getValue() {
            return value;
        }

        void setValue(final MonetaryAmount value) {
            this.value = value;
        }

    }

    @Test
    @Parameters(method = "data")
    public void shouldFailToDeserializeWithoutAmount(final Class<M> type, final Configurer configurer) {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"currency\":\"EUR\"}";

        final JsonProcessingException exception = assertThrows(
                JsonProcessingException.class, () -> unit.readValue(content, type));

        assertThat(exception.getMessage()).contains("Missing property: 'amount'");
    }

    @Test
    @Parameters(method = "data")
    public void shouldFailToDeserializeWithoutCurrency(final Class<M> type, final Configurer configurer) {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95}";

        final MismatchedInputException exception = assertThrows(
                MismatchedInputException.class, () -> unit.readValue(content, type));

        assertThat(exception.getMessage()).contains("Missing property: 'currency'");
    }

    @Test
    @Parameters(method = "data")
    public void shouldFailToDeserializeWithAdditionalProperties(final Class<M> type,
            final Configurer configurer) {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\",\"version\":\"1\"}";

        final JsonProcessingException exception = assertThrows(
                UnrecognizedPropertyException.class, () -> unit.readValue(content, type));

        assertThat(exception.getMessage()).startsWith(
                "Unrecognized field \"version\" (class javax.money.MonetaryAmount), " +
                        "not marked as ignorable (3 known properties: \"amount\", \"currency\", \"formatted\"])");
    }

    @Test
    @Parameters(method = "data")
    public void shouldNotFailToDeserializeWithAdditionalProperties(final Class<M> type,
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer).disable(FAIL_ON_UNKNOWN_PROPERTIES);

        final String content = "{\"source\":{\"provider\":\"ECB\",\"date\":\"2016-09-29\"},\"amount\":29.95,\"currency\":\"EUR\",\"version\":\"1\"}";
        unit.readValue(content, type);
    }

    @Test
    @Parameters(method = "data")
    public void shouldDeserializeWithTypeInformation(final Class<M> type, final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer)
                .activateDefaultTyping(
                        BasicPolymorphicTypeValidator.builder().build(),
                        ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE,
                        JsonTypeInfo.As.EXISTING_PROPERTY)
                .disable(FAIL_ON_UNKNOWN_PROPERTIES);

        final String content = "{\"type\":\"org.javamoney.moneta.Money\",\"amount\":29.95,\"currency\":\"EUR\"}";
        final M amount = unit.readValue(content, type);

        // type information is ignored?!
        assertThat(amount).isInstanceOf(type);
    }

    @Test
    @Parameters(method = "data")
    public void shouldDeserializeWithoutTypeInformation(final Class<M> type, final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer).activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().build());

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final M amount = unit.readValue(content, type);

        assertThat(amount).isInstanceOf(type);
    }

}
