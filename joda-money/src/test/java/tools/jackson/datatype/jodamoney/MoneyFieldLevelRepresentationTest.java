package tools.jackson.datatype.jodamoney;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MoneyFieldLevelRepresentation tests")
public class MoneyFieldLevelRepresentationTest extends ModuleTestBase {

    @Nested
    @DisplayName("@JsonMoney annotation tests")
    class JsonMoneyAnnotationTests {

        @Test
        @DisplayName("should serialize field with DECIMAL_STRING when @JsonMoney specified")
        void fieldWithDecimalString() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            PaymentWithFieldAnnotations payment = new PaymentWithFieldAnnotations(
                Money.parse("EUR 12.34"),
                Money.parse("EUR 5.67"),
                Money.parse("EUR 100.00")
            );

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"amount\":{\"amount\":\"12.34\""));
            assertTrue(json.contains("\"fee\":{\"amount\":567"));
            assertTrue(json.contains("\"total\":{\"amount\":100.00"));
        }

        @Test
        @DisplayName("should deserialize field with DECIMAL_STRING when @JsonMoney specified")
        void deserializeFieldWithDecimalString() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            String json = "{\"amount\":{\"amount\":\"12.34\",\"currency\":\"EUR\"}," +
                         "\"fee\":{\"amount\":567,\"currency\":\"EUR\"}," +
                         "\"total\":{\"amount\":100.00,\"currency\":\"EUR\"}}";

            // when
            PaymentWithFieldAnnotations payment = mapper.readValue(json, PaymentWithFieldAnnotations.class);

            // then
            assertEquals(Money.parse("EUR 12.34"), payment.amount);
            assertEquals(Money.parse("EUR 5.67"), payment.fee);
            assertEquals(Money.parse("EUR 100.00"), payment.total);
        }

        @Test
        @DisplayName("should serialize getter with @JsonMoney annotation")
        void getterWithAnnotation() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            PaymentWithGetterAnnotations payment = new PaymentWithGetterAnnotations(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"amount\":{\"amount\":\"12.34\""));
        }

        @Test
        @DisplayName("should deserialize constructor parameter with @JsonMoney annotation")
        void constructorParameterWithAnnotation() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            String json = "{\"amount\":{\"amount\":\"12.34\",\"currency\":\"EUR\"}}";

            // when
            PaymentWithConstructorAnnotations payment = mapper.readValue(json, PaymentWithConstructorAnnotations.class);

            // then
            assertEquals(Money.parse("EUR 12.34"), payment.getAmount());
        }
    }

    @Nested
    @DisplayName("Mixed configuration tests")
    class MixedConfigurationTests {

        @Test
        @DisplayName("should use field annotation over module default")
        void fieldOverridesModuleDefault() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule(m -> m.withAmountRepresentation(AmountRepresentation.DECIMAL_NUMBER));
            PaymentWithFieldAnnotations payment = new PaymentWithFieldAnnotations(
                Money.parse("EUR 12.34"),
                Money.parse("EUR 5.67"),
                Money.parse("EUR 100.00")
            );

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"amount\":{\"amount\":\"12.34\"")); // override to string
            assertTrue(json.contains("\"fee\":{\"amount\":567")); // override to int
            assertTrue(json.contains("\"total\":{\"amount\":100.00")); // uses module default (number)
        }

        @Test
        @DisplayName("should use @JsonMoney over @JsonFormat when both present")
        void jsonMoneyWinsOverJsonFormat() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            PaymentWithBothAnnotations payment = new PaymentWithBothAnnotations(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"amount\":{\"amount\":\"12.34\"")); // @JsonMoney wins (STRING)
        }

        @Test
        @DisplayName("should round-trip with mixed representations")
        void roundTripWithMixedRepresentations() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule();
            PaymentWithFieldAnnotations original = new PaymentWithFieldAnnotations(
                Money.parse("EUR 12.34"),
                Money.parse("EUR 5.67"),
                Money.parse("EUR 100.00")
            );

            // when
            String json = mapper.writeValueAsString(original);
            PaymentWithFieldAnnotations deserialized = mapper.readValue(json, PaymentWithFieldAnnotations.class);

            // then
            assertEquals(original.amount, deserialized.amount);
            assertEquals(original.fee, deserialized.fee);
            assertEquals(original.total, deserialized.total);
        }
    }

    @Nested
    @DisplayName("DEFAULT representation tests")
    class DefaultRepresentationTests {

        @Test
        @DisplayName("should inherit module default when @JsonMoney(DEFAULT) specified")
        void defaultInheritsModuleConfig() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModule(m -> m.withAmountRepresentation(AmountRepresentation.DECIMAL_STRING));
            PaymentWithDefaultAnnotation payment = new PaymentWithDefaultAnnotation(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"amount\":{\"amount\":\"12.34\"")); // inherits DECIMAL_STRING from module
        }
    }

    @Nested
    @DisplayName("Mix-in annotation tests")
    class MixinAnnotationTests {

        @Test
        @DisplayName("should apply @JsonMoney from mix-in to override representation")
        void jsonMoneyMixinOverridesDefault() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModuleBuilder()
                    .addMixIn(PaymentWithoutAnnotations.class, PaymentMixinWithJsonMoney.class)
                    .build();
            PaymentWithoutAnnotations payment = new PaymentWithoutAnnotations(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"amount\":{\"amount\":\"12.34\"")); // mix-in applies DECIMAL_STRING
        }

        @Test
        @DisplayName("should apply @JsonFormat from mix-in to override representation")
        void jsonFormatMixinOverridesDefault() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModuleBuilder()
                    .addMixIn(PaymentWithoutAnnotations.class, PaymentMixinWithJsonFormat.class)
                    .build();
            PaymentWithoutAnnotations payment = new PaymentWithoutAnnotations(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"amount\":{\"amount\":1234")); // mix-in applies NUMBER_INT -> MINOR_CURRENCY_UNIT
        }

        @Test
        @DisplayName("should round-trip with mix-in annotations")
        void roundTripWithMixin() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModuleBuilder()
                    .addMixIn(PaymentWithoutAnnotations.class, PaymentMixinWithJsonMoney.class)
                    .build();
            PaymentWithoutAnnotations original = new PaymentWithoutAnnotations(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(original);
            PaymentWithoutAnnotations deserialized = mapper.readValue(json, PaymentWithoutAnnotations.class);

            // then
            assertEquals(original.amount, deserialized.amount);
        }

        @Test
        @DisplayName("should apply @JsonMoney mix-in over @JsonFormat mix-in when both present")
        void jsonMoneyMixinWinsOverJsonFormatMixin() throws Exception {
            // setup
            ObjectMapper mapper = mapperWithModuleBuilder()
                    .addMixIn(PaymentWithoutAnnotations.class, PaymentMixinWithBothAnnotations.class)
                    .build();
            PaymentWithoutAnnotations payment = new PaymentWithoutAnnotations(Money.parse("EUR 12.34"));

            // when
            String json = mapper.writeValueAsString(payment);

            // then
            assertTrue(json.contains("\"amount\":{\"amount\":\"12.34\"")); // @JsonMoney (STRING) wins over @JsonFormat (NUMBER_INT)
        }
    }

    // Test POJOs

    static class PaymentWithFieldAnnotations {
        @JsonMoney(amountRepresentation = AmountRepresentation.DECIMAL_STRING)
        public Money amount;

        @JsonMoney(amountRepresentation = AmountRepresentation.MINOR_CURRENCY_UNIT)
        public Money fee;

        public Money total; // No annotation - uses module default

        @JsonCreator
        public PaymentWithFieldAnnotations(
            @JsonProperty("amount") Money amount,
            @JsonProperty("fee") Money fee,
            @JsonProperty("total") Money total
        ) {
            this.amount = amount;
            this.fee = fee;
            this.total = total;
        }
    }

    static class PaymentWithGetterAnnotations {
        private Money amount;

        public PaymentWithGetterAnnotations(Money amount) {
            this.amount = amount;
        }

        @JsonMoney(amountRepresentation = AmountRepresentation.DECIMAL_STRING)
        public Money getAmount() {
            return amount;
        }
    }

    static class PaymentWithConstructorAnnotations {
        private final Money amount;

        @JsonCreator
        public PaymentWithConstructorAnnotations(
            @JsonMoney(amountRepresentation = AmountRepresentation.DECIMAL_STRING)
            @JsonProperty("amount") Money amount
        ) {
            this.amount = amount;
        }

        public Money getAmount() {
            return amount;
        }
    }

    static class PaymentWithBothAnnotations {
        @JsonMoney(amountRepresentation = AmountRepresentation.DECIMAL_STRING)
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public Money amount;

        public PaymentWithBothAnnotations(Money amount) {
            this.amount = amount;
        }
    }

    static class PaymentWithDefaultAnnotation {
        @JsonMoney(amountRepresentation = AmountRepresentation.DEFAULT)
        public Money amount;

        public PaymentWithDefaultAnnotation(Money amount) {
            this.amount = amount;
        }
    }

    // POJOs without annotations for mix-in tests
    static class PaymentWithoutAnnotations {
        public Money amount;

        public PaymentWithoutAnnotations(Money amount) {
            this.amount = amount;
        }

        public PaymentWithoutAnnotations() {
        }
    }

    // Mix-in classes
    abstract static class PaymentMixinWithJsonMoney {
        @JsonMoney(amountRepresentation = AmountRepresentation.DECIMAL_STRING)
        public Money amount;
    }

    abstract static class PaymentMixinWithJsonFormat {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public Money amount;
    }

    abstract static class PaymentMixinWithBothAnnotations {
        @JsonMoney(amountRepresentation = AmountRepresentation.DECIMAL_STRING)
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public Money amount;
    }
}
