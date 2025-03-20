package com.fasterxml.jackson.datatype.moneta;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.moneta.MonetaMoneyModule;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import org.junit.jupiter.api.Test;

import javax.money.CurrencyUnit;

import static org.assertj.core.api.Assertions.assertThat;

public final class CurrencyUnitSchemaSerializerTest {

    private final ObjectMapper unit = JsonMapper.builder().addModule(new MonetaMoneyModule()).build();

    @Test
    public void shouldSerializeJsonSchema() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        JsonNode schemaNode = generator.generateJsonSchema(CurrencyUnit.class);
        assertThat(schemaNode.get("type")).isNotNull();
        assertThat(schemaNode.get("type").asText()).isEqualTo("string");
    }
}
