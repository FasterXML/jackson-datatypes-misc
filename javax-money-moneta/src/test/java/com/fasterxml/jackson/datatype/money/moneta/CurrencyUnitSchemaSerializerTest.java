package com.fasterxml.jackson.datatype.money.moneta;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import org.junit.Test;

import javax.money.CurrencyUnit;

import static org.assertj.core.api.Assertions.assertThat;

public final class CurrencyUnitSchemaSerializerTest {

    private final ObjectMapper unit = new ObjectMapper().registerModule(new MonetaMoneyModule());

    @Test
    public void shouldSerializeJsonSchema() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        JsonNode schemaNode = generator.generateJsonSchema(CurrencyUnit.class);
        assertThat(schemaNode.get("type")).isNotNull();
        assertThat(schemaNode.get("type").asText()).isEqualTo("string");
    }
}
