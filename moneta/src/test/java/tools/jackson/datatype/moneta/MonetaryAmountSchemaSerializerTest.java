package tools.jackson.datatype.moneta;

import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.moneta.MonetaMoneyModule;

import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import org.junit.jupiter.api.Test;

import javax.money.MonetaryAmount;

import static org.assertj.core.api.Assertions.assertThat;

public final class MonetaryAmountSchemaSerializerTest
{
    // 19-Mar-2025, tatu: Alas, JsonSchemaGenerator won't work with Jackson 3.x (yet?)

    /*
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
    public void shouldSerializeJsonSchemaWithMultipleMonetayAmounts() throws Exception {
        final ObjectMapper unit = unit(module());
        final JsonSchemaGenerator generator =
                new JsonSchemaGenerator(unit);

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
    */

    ObjectMapper unit(final JacksonModule module) {
        return JsonMapper.builder().addModule(module).build();
    }

    MonetaMoneyModule module() {
        return new MonetaMoneyModule();
    }

}
