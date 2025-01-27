package com.fasterxml.jackson.datatype.money;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import org.javamoney.moneta.Money;
import org.junit.Test;

import javax.money.MonetaryAmount;

import static org.assertj.core.api.Assertions.assertThat;

public final class MonetaryAmountSchemaSerializerTest {

    @Test
    public void shouldSerializeJsonSchema() throws Exception {
        final ObjectMapper unit = unit(module());
        final JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        final JsonNode jsonSchema = generator.generateJsonSchema(MonetaryAmount.class);
        final String actual = unit.writeValueAsString(jsonSchema);
        final String expected = "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\":\"Monetary Amount\"" +
                ",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"amount\":{\"type\":\"number\"}" +
                ",\"currency\":{\"type\":\"string\"},\"formatted\":{\"type\":\"string\"}}" +
                ",\"required\":[\"amount\",\"currency\"]}";

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldSerializeJsonSchemaWithCustomFieldNames() throws Exception {
        final ObjectMapper unit = unit(module().withAmountFieldName("value")
                                         .withCurrencyFieldName("unit")
                                         .withFormattedFieldName("pretty"));
        final JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        final JsonNode jsonSchema = generator.generateJsonSchema(MonetaryAmount.class);
        final String actual = unit.writeValueAsString(jsonSchema);
        final String expected = "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\":\"Monetary Amount\"" +
                ",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"value\":{\"type\":\"number\"}" +
                ",\"unit\":{\"type\":\"string\"},\"pretty\":{\"type\":\"string\"}},\"required\":[\"value\",\"unit\"]}";

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldSerializeJsonSchemaWithQuotedDecimalNumbers() throws Exception {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());
        final JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        final JsonNode jsonSchema = generator.generateJsonSchema(MonetaryAmount.class);
        final String actual = unit.writeValueAsString(jsonSchema);
        final String expected = "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\":\"Monetary Amount\"" +
                ",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"amount\":{\"type\":\"string\"}" +
                ",\"currency\":{\"type\":\"string\"},\"formatted\":{\"type\":\"string\"}},\"required\":[\"amount\",\"currency\"]}";

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public  void shouldSerializeJsonSchemaWithMultipleMonetayAmounts() throws Exception {
        final ObjectMapper unit = unit(module());
        final com.kjetland.jackson.jsonSchema.JsonSchemaGenerator generator =
                new com.kjetland.jackson.jsonSchema.JsonSchemaGenerator(unit);

        final JsonNode jsonSchema = generator.generateJsonSchema(SchemaTestClass.class);

        final String actual = unit.writeValueAsString(jsonSchema);
        final String expected = "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\":\"Schema Test Class\"," +
                "\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"moneyOne\":{\"$ref\":" +
                "\"#/definitions/MonetaryAmount\"},\"moneyTwo\":{\"$ref\":\"#/definitions/MonetaryAmount\"}}," +
                "\"definitions\":{\"MonetaryAmount\":{\"type\":\"object\",\"additionalProperties\":false,\"properties\"" +
                ":{\"amount\":{\"type\":\"number\"},\"currency\":{\"type\":\"string\"},\"formatted\":" +
                "{\"type\":\"string\"}},\"required\":[\"amount\",\"currency\"]}}}";

        assertThat(actual).isEqualTo(expected);
    }

    private ObjectMapper unit(final Module module) {
        return new ObjectMapper().registerModule(module);
    }

    private JavaxMoneyModule module() {
        return new JavaxMoneyModule();
    }

}
