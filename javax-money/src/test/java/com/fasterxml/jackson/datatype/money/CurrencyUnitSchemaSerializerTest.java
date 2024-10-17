package com.fasterxml.jackson.datatype.money;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.junit.Test;

import javax.money.CurrencyUnit;

import static org.assertj.core.api.Assertions.assertThat;

public final class CurrencyUnitSchemaSerializerTest {

    private final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();

    @Test
    public void shouldSerializeJsonSchema() throws Exception {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        JsonSchema jsonSchema = generator.generateSchema(CurrencyUnit.class);
        final String actual = unit.writeValueAsString(jsonSchema);
        final String expected = "{\"type\":\"string\"}";

        assertThat(actual).isEqualTo(expected);
    }
}
