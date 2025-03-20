package com.fasterxml.jackson.datatype.javax.money;

import javax.money.CurrencyUnit;

import org.junit.jupiter.api.Test;

import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

public final class CurrencyUnitSchemaSerializerTest {

    private final ObjectMapper unit = new ObjectMapper().registerModule(new JavaxMoneyModule());

    @Test
    public void shouldSerializeJsonSchema() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        JsonNode schemaNode = generator.generateJsonSchema(CurrencyUnit.class);
        assertThat(schemaNode.get("type")).isNotNull();
        assertThat(schemaNode.get("type").asText()).isEqualTo("string");
    }
}
