package tools.jackson.datatype.jodamoney;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import org.joda.money.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MoneyFormatShapeMapping tests")
public class MoneyFormatShapeMappingTest extends ModuleTestBase {

    @Nested
    @DisplayName("@JsonFormat shape mapping tests")
    class ShapeMappingTests {

        @Test
        @DisplayName("should map STRING to DECIMAL_STRING representation")
        void stringShapeToDecimalString() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            PaymentWithStringShape payment = new PaymentWithStringShape(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"amount\":{\"amount\":\"12.34\""));
        }

        @Test
        @DisplayName("should map NUMBER to DECIMAL_NUMBER representation")
        void numberShapeToDecimalNumber() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            PaymentWithNumberShape payment = new PaymentWithNumberShape(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"amount\":{\"amount\":12.34"));
        }

        @Test
        @DisplayName("should map NUMBER_FLOAT to DECIMAL_NUMBER representation")
        void numberFloatShapeToDecimalNumber() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            PaymentWithNumberFloatShape payment = new PaymentWithNumberFloatShape(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"amount\":{\"amount\":12.34"));
        }

        @Test
        @DisplayName("should map NUMBER_INT to MINOR_CURRENCY_UNIT representation")
        void numberIntShapeToMinorCurrencyUnit() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            PaymentWithNumberIntShape payment = new PaymentWithNumberIntShape(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"amount\":{\"amount\":1234"));
        }

        @Test
        @DisplayName("should ignore unsupported shape and use module default")
        void unsupportedShapeUsesDefault() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule(m -> m.withAmountRepresentation(AmountRepresentation.DECIMAL_STRING));
            PaymentWithObjectShape payment = new PaymentWithObjectShape(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"amount\":{\"amount\":\"12.34\"")); // uses module default
        }
    }

    @Nested
    @DisplayName("Round-trip tests")
    class RoundTripTests {

        @Test
        @DisplayName("should round-trip with STRING shape")
        void roundTripStringShape() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            PaymentWithStringShape original = new PaymentWithStringShape(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(original);
            PaymentWithStringShape deserialized = mapper.readValue(json, PaymentWithStringShape.class);

            // then
            assertEquals(original.amount, deserialized.amount);
        }

        @Test
        @DisplayName("should round-trip with NUMBER_INT shape")
        void roundTripNumberIntShape() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            PaymentWithNumberIntShape original = new PaymentWithNumberIntShape(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(original);
            PaymentWithNumberIntShape deserialized = mapper.readValue(json, PaymentWithNumberIntShape.class);

            // then
            assertEquals(original.amount, deserialized.amount);
        }
    }

    @Nested
    @DisplayName("Multiple shapes tests")
    class MultipleShapesTests {

        @Test
        @DisplayName("should serialize multiple fields with different shapes")
        void multipleFieldsWithDifferentShapes() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            PaymentWithMultipleShapes payment = new PaymentWithMultipleShapes(
                Money.parse("EUR 12.34"),
                Money.parse("EUR 5.67"),
                Money.parse("EUR 100.00")
            );

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"stringAmount\":{\"amount\":\"12.34\""));
            assertTrue(json.contains("\"numberAmount\":{\"amount\":5.67"));
            assertTrue(json.contains("\"intAmount\":{\"amount\":10000"));
        }

        @Test
        @DisplayName("should deserialize multiple fields with different shapes")
        void deserializeMultipleFieldsWithDifferentShapes() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            String json = "{\"stringAmount\":{\"amount\":\"12.34\",\"currency\":\"EUR\"}," +
                         "\"numberAmount\":{\"amount\":5.67,\"currency\":\"EUR\"}," +
                         "\"intAmount\":{\"amount\":10000,\"currency\":\"EUR\"}}";

            // when
            PaymentWithMultipleShapes payment = mapper.readValue(json, PaymentWithMultipleShapes.class);

            // then
            assertEquals(Money.parse("EUR 12.34"), payment.stringAmount);
            assertEquals(Money.parse("EUR 5.67"), payment.numberAmount);
            assertEquals(Money.parse("EUR 100.00"), payment.intAmount);
        }
    }

    // Test POJOs

    static class PaymentWithStringShape {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public Money amount;

        @JsonCreator
        public PaymentWithStringShape(@JsonProperty("amount") Money amount) {
            this.amount = amount;
        }
    }

    static class PaymentWithNumberShape {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public Money amount;

        @JsonCreator
        public PaymentWithNumberShape(@JsonProperty("amount") Money amount) {
            this.amount = amount;
        }
    }

    static class PaymentWithNumberFloatShape {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
        public Money amount;

        @JsonCreator
        public PaymentWithNumberFloatShape(@JsonProperty("amount") Money amount) {
            this.amount = amount;
        }
    }

    static class PaymentWithNumberIntShape {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public Money amount;

        @JsonCreator
        public PaymentWithNumberIntShape(@JsonProperty("amount") Money amount) {
            this.amount = amount;
        }
    }

    static class PaymentWithObjectShape {
        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        public Money amount;

        @JsonCreator
        public PaymentWithObjectShape(@JsonProperty("amount") Money amount) {
            this.amount = amount;
        }
    }

    static class PaymentWithMultipleShapes {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public Money stringAmount;

        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public Money numberAmount;

        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public Money intAmount;

        @JsonCreator
        public PaymentWithMultipleShapes(
            @JsonProperty("stringAmount") Money stringAmount,
            @JsonProperty("numberAmount") Money numberAmount,
            @JsonProperty("intAmount") Money intAmount
        ) {
            this.stringAmount = stringAmount;
            this.numberAmount = numberAmount;
            this.intAmount = intAmount;
        }
    }
}
