package tools.jackson.datatype.moneta;

import javax.money.CurrencyUnit;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.moneta.MonetaMoneyModule;

import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

import static org.assertj.core.api.Assertions.assertThat;

public final class CurrencyUnitSchemaSerializerTest {

    private final ObjectMapper unit = JsonMapper.builder().addModule(new MonetaMoneyModule()).build();

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
