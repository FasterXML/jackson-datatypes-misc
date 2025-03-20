package tools.jackson.datatype.javax.money;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.stream.Stream;
import javax.money.MonetaryAmount;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.type.SimpleType;

import lombok.Value;
import tools.jackson.datatype.javax.money.AmountWriter;
import tools.jackson.datatype.javax.money.DecimalAmountWriter;
import tools.jackson.datatype.javax.money.FieldNames;
import tools.jackson.datatype.javax.money.JavaxMoneyModule;
import tools.jackson.datatype.javax.money.MonetaryAmountFormatFactory;
import tools.jackson.datatype.javax.money.MonetaryAmountSerializer;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static javax.money.Monetary.getDefaultRounding;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public final class MonetaryAmountSerializerTest {

    static Stream<MonetaryAmount> amounts() {
        return Stream.of(
                FastMoney.of(29.95, "EUR"),
                Money.of(29.95, "EUR"),
                RoundedMoney.of(29.95, "EUR", getDefaultRounding()));
    }

    static Stream<MonetaryAmount> hundreds() {
        return Stream.of(
                FastMoney.of(100, "EUR"),
                Money.of(100, "EUR"),
                RoundedMoney.of(100, "EUR", getDefaultRounding()));
    }

    static Stream<MonetaryAmount> fractions() {
        return Stream.of(
                FastMoney.of(0.0001, "EUR"),
                Money.of(0.0001, "EUR"),
                RoundedMoney.of(0.0001, "EUR", getDefaultRounding()));
    }

    private ObjectMapper unit() {
        return builder(module()).build();
    }

    private JsonMapper unit(final JacksonModule module) {
        return builder(module).build();
    }

    private JsonMapper.Builder builder() {
        return builder(module());
    }

    private JsonMapper.Builder builder(final JacksonModule module) {
        return JsonMapper.builder()
                .addModule(module);
    }

    private JavaxMoneyModule module() {
        return new JavaxMoneyModule();
    }

    @ParameterizedTest
    @MethodSource("amounts")
    public void shouldSerialize(final MonetaryAmount amount) throws Exception {
        final ObjectMapper unit = unit();

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    public void shouldSerializeWithoutFormattedValueIfFactoryProducesNull(
            final MonetaryAmount amount) throws Exception {
        final ObjectMapper unit = unit(module().withoutFormatting());

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    public void shouldSerializeWithFormattedGermanValue(final MonetaryAmount amount) throws Exception {
        final ObjectMapper unit = unit(new JavaxMoneyModule().withDefaultFormatting());

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"29,95 EUR\"}";

        final ObjectWriter writer = unit.writer().with(Locale.GERMANY);
        final String actual = writer.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    public void shouldSerializeWithFormattedAmericanValue(final MonetaryAmount amount) throws Exception {
        final ObjectMapper unit = unit(module().withDefaultFormatting());

        final String expected = "{\"amount\":29.95,\"currency\":\"USD\",\"formatted\":\"USD29.95\"}";

        final ObjectWriter writer = unit.writer().with(Locale.US);
        final String actual = writer.writeValueAsString(amount.getFactory().setCurrency("USD").create());

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    public void shouldSerializeWithCustomName(final MonetaryAmount amount) throws Exception {
        final ObjectMapper unit = unit(module().withDefaultFormatting()
                .withAmountFieldName("value")
                .withCurrencyFieldName("unit")
                .withFormattedFieldName("pretty"));

        final String expected = "{\"value\":29.95,\"unit\":\"EUR\",\"pretty\":\"29,95 EUR\"}";

        final ObjectWriter writer = unit.writer().with(Locale.GERMANY);
        final String actual = writer.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    public void shouldSerializeAmountAsDecimal(final MonetaryAmount amount) throws Exception {
        final ObjectMapper unit = unit(module().withDecimalNumbers());

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("hundreds")
    public void shouldSerializeAmountAsDecimalWithDefaultFractionDigits(
            final MonetaryAmount hundred) throws Exception {
        final ObjectMapper unit = unit(module().withDecimalNumbers());

        final String expected = "{\"amount\":100.00,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("fractions")
    public void shouldSerializeAmountAsDecimalWithHigherNumberOfFractionDigits(
            final MonetaryAmount fraction) throws Exception {
        final ObjectMapper unit = unit(module().withDecimalNumbers());

        final String expected = "{\"amount\":0.0001,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(fraction);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("hundreds")
    public void shouldSerializeAmountAsDecimalWithLowerNumberOfFractionDigits(
            final MonetaryAmount hundred) throws Exception {
        final ObjectMapper unit = unit(module().withNumbers(new AmountWriter<BigDecimal>() {
            @Override
            public Class<BigDecimal> getType() {
                return BigDecimal.class;
            }

            @Override
            public BigDecimal write(final MonetaryAmount amount) {
                return amount.getNumber().numberValueExact(BigDecimal.class).stripTrailingZeros();
            }
        }));

        final String expected = "{\"amount\":1E+2,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    public void shouldSerializeAmountAsQuotedDecimal(final MonetaryAmount amount) throws Exception {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());

        final String expected = "{\"amount\":\"29.95\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("hundreds")
    public void shouldSerializeAmountAsQuotedDecimalWithDefaultFractionDigits(
            final MonetaryAmount hundred) throws Exception {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());

        final String expected = "{\"amount\":\"100.00\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("fractions")
    public void shouldSerializeAmountAsQuotedDecimalWithHigherNumberOfFractionDigits(
            final MonetaryAmount fraction) throws Exception {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());

        final String expected = "{\"amount\":\"0.0001\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(fraction);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("hundreds")
    public void shouldSerializeAmountAsQuotedDecimalWithLowerNumberOfFractionDigits(
            final MonetaryAmount hundred) throws Exception {
        final ObjectMapper unit = unit(module().withNumbers(new AmountWriter<String>() {
            @Override
            public Class<String> getType() {
                return String.class;
            }

            @Override
            public String write(final MonetaryAmount amount) {
                return amount.getNumber().numberValueExact(BigDecimal.class).stripTrailingZeros().toPlainString();
            }
        }));

        final String expected = "{\"amount\":\"100\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("hundreds")
    public void shouldSerializeAmountAsQuotedDecimalPlainString(final MonetaryAmount hundred) throws Exception {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());

        final String expected = "{\"amount\":\"100.00\",\"currency\":\"EUR\"}";
        final String actual = unit.writer()
                .with(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN)
                .writeValueAsString(hundred);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    public void shouldWriteNumbersAsStrings(final MonetaryAmount amount) throws Exception {
        final ObjectMapper unit = builder()
                .enable(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS)
                .build();

        final String expected = "{\"amount\":\"29.95\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("hundreds")
    public void shouldWriteNumbersAsPlainStrings(final MonetaryAmount hundred) throws Exception {
        final ObjectMapper unit = builder()
                .enable(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS)
                .enable(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN)
                .build();

        final String expected = "{\"amount\":\"100.00\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual).isEqualTo(expected);
    }

    @Value
    static class Price {
        MonetaryAmount amount;
    }

    @ParameterizedTest
    @MethodSource("amounts")
    public void shouldSerializeWithType(final MonetaryAmount amount) throws Exception {
        final ObjectMapper unit = builder(module())
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build())
                .build();

        final String expected = "{\"amount\":{\"amount\":29.95,\"currency\":\"EUR\"}}";
        final String actual = unit.writeValueAsString(new Price(amount));

        assertThat(actual).isEqualTo(expected);
    }

    @Value
    private static class PriceUnwrapped {
        @JsonUnwrapped
        MonetaryAmount amount;
    }

    @ParameterizedTest
    @MethodSource("amounts")
    public void shouldSerializeWithTypeUnwrapped(final MonetaryAmount amount) throws Exception {
        final ObjectMapper unit = builder(module())
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build())
                .build();

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(new PriceUnwrapped(amount));

        assertThat(actual).isEqualTo(expected);
    }

    @Value
    private static class PriceUnwrappedTransformedNames {
        @JsonUnwrapped(prefix = "Price-", suffix = "-Field")
        MonetaryAmount amount;
    }

    @ParameterizedTest
    @MethodSource("amounts")
    public void shouldSerializeWithTypeUnwrappedAndNamesTransformed(final MonetaryAmount amount) throws Exception {
        final ObjectMapper unit = builder(module())
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build())
                .build();

        final String expected = "{\"Price-amount-Field\":29.95,\"Price-currency-Field\":\"EUR\"}";
        final String actual = unit.writeValueAsString(new PriceUnwrappedTransformedNames(amount));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldHandleNullValueFromExpectObjectFormatInSchemaVisitor() throws Exception {
        final MonetaryAmountSerializer unit = new MonetaryAmountSerializer(FieldNames.defaults(),
                new DecimalAmountWriter(), MonetaryAmountFormatFactory.NONE);

        final JsonFormatVisitorWrapper wrapper = mock(JsonFormatVisitorWrapper.class);
        unit.acceptJsonFormatVisitor(wrapper, SimpleType.constructUnsafe(MonetaryAmount.class));
    }

    /**
     * Fixes a bug that caused the amount field to be written as
     * <code>
     * "amount": {"BigDecimal":12.34}
     * </code>
     *
     * @param amount
     */
    @ParameterizedTest
    @MethodSource("amounts")
    public void shouldSerializeWithWrapRootValue(final MonetaryAmount amount) throws Exception {
        final ObjectMapper unit = builder(module())
                .configure(SerializationFeature.WRAP_ROOT_VALUE, true)
                .build();

        final String expected = "{\"Price\":{\"amount\":{\"amount\":29.95,\"currency\":\"EUR\"}}}";
        final String actual = unit.writeValueAsString(new Price(amount));

        assertThat(actual).isEqualTo(expected);
    }

}
