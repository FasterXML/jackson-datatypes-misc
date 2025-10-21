package tools.jackson.datatype.jsonp;

import tools.jackson.databind.ObjectMapper;

import jakarta.json.JsonArray;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class JsonMergePatchDeserializationTest extends TestBase {

    private static final ObjectMapper MAPPER = newMapper();

    @Test
    public void testObjectDeserializationAndPatching() throws Exception {
        final String json = "{" +
                "\"name\":\"Json\"" +
                "}";

        final JsonMergePatch jsonMergePatch = MAPPER.readValue(json, JsonMergePatch.class);
        final JsonValue jsonPatchAsJsonValue = jsonMergePatch.toJsonValue();
        assertInstanceOf(JsonObject.class, jsonPatchAsJsonValue);

        final JsonObject jsonPatchAsJsonObject = jsonPatchAsJsonValue.asJsonObject();
        assertTrue(jsonPatchAsJsonObject.containsKey("name"));
        assertInstanceOf(JsonString.class, jsonPatchAsJsonObject.get("name"));
        assertEquals("Json", jsonPatchAsJsonObject.getString("name"));


        assertEquals(json, serializeAsString(jsonPatchAsJsonValue));

        final Person person = new Person("John", "Smith");
        final JsonValue personJson = MAPPER.convertValue(person, JsonValue.class);
        final JsonValue patchedPersonJson = jsonMergePatch.apply(personJson);
        final Person patchedPerson = MAPPER.convertValue(patchedPersonJson, Person.class);
        assertEquals(new Person("Json", "Smith"), patchedPerson);
    }

    @Test
    public void testArrayDeserializationAndPatching() throws Exception {
        final String json = "[" +
            "\"name\",\"Json\"" +
            "]";

        final JsonMergePatch jsonMergePatch = MAPPER.readValue(json, JsonMergePatch.class);
        final JsonValue jsonPatchAsJsonValue = jsonMergePatch.toJsonValue();
        assertThat(jsonPatchAsJsonValue).isInstanceOf(JsonArray.class);


        final JsonArray jsonPatchAsJsonArray = jsonPatchAsJsonValue.asJsonArray();
        assertThat(jsonPatchAsJsonArray.size()).isEqualTo(2);
        assertThat(jsonPatchAsJsonArray.get(0)).isInstanceOf(JsonString.class);
        assertThat(((JsonString) jsonPatchAsJsonArray.get(0)).getString()).isEqualTo("name");
        assertThat(jsonPatchAsJsonArray.get(1)).isInstanceOf(JsonString.class);
        assertThat(((JsonString) jsonPatchAsJsonArray.get(1)).getString()).isEqualTo("Json");

        assertThat(serializeAsString(jsonPatchAsJsonValue)).isEqualTo(json);

        final Person person = new Person("John", "Smith");
        final JsonValue personJson = MAPPER.convertValue(person, JsonValue.class);
        final JsonValue patchedPersonJson = jsonMergePatch.apply(personJson);
        assertThat(patchedPersonJson).isInstanceOf(JsonArray.class);
    }

    @Test
    public void testScalarDeserializationAndPatching() throws Exception {
        final String json = "\"name\"";

        final JsonMergePatch jsonMergePatch = MAPPER.readValue(json, JsonMergePatch.class);
        final JsonValue jsonPatchAsJsonValue = jsonMergePatch.toJsonValue();
        assertThat(jsonPatchAsJsonValue).isInstanceOf(JsonString.class);

        assertThat(serializeAsString(jsonPatchAsJsonValue)).isEqualTo(json);

        final Person person = new Person("John", "Smith");
        final JsonValue personJson = MAPPER.convertValue(person, JsonValue.class);
        final JsonValue patchedPersonJson = jsonMergePatch.apply(personJson);
        assertThat(patchedPersonJson).isInstanceOf(JsonString.class);
        assertThat(((JsonString) patchedPersonJson).getString()).isEqualTo("name");
    }

    static class Person {
        private String name;
        private String lastName;

        public Person() {
        }

        public Person(String name, String lastName) {
            this.name = name;
            this.lastName = lastName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, lastName);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return Objects.equals(name, person.name) &&
                    Objects.equals(lastName, person.lastName);
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", lastName='" + lastName + '\'' +
                    '}';
        }
    }

}
