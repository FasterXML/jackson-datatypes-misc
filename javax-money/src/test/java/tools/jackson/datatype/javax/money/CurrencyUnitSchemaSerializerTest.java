package tools.jackson.datatype.javax.money;

import javax.money.CurrencyUnit;

import org.junit.jupiter.api.Test;

import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

import tools.jackson.datatype.javax.money.JavaxMoneyModule;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

public final class CurrencyUnitSchemaSerializerTest {

    private final ObjectMapper unit = JsonMapper.builder()
            .addModule(new JavaxMoneyModule())
            .build();

    @Test
    public void shouldSerializeJsonSchema() {
        // 19-Mar-2025, tatu: Alas, JsonSchemaGenerator won't work with Jackson 3.x (yet?)
        /*
        JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        JsonNode schemaNode = generator.generateJsonSchema(CurrencyUnit.class);
        assertThat(schemaNode.get("type")).isNotNull();
        assertThat(schemaNode.get("type").asText()).isEqualTo("string");
        */
    }
}
