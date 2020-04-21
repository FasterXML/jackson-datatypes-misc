package com.fasterxml.jackson.datatype.jsr353;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.json.*;

import java.util.Objects;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonPatchDeserializationTest extends TestBase {

    private static final ObjectMapper MAPPER = newMapper();

    public void testDeserializationAndPatching() throws Exception {
        final String json = "[" +
                "{" +
                "\"op\":\"replace\"," +
                "\"path\":\"/name\"," +
                "\"value\":\"Json\"" +
                "}" +
                "]";

        final JsonPatch jsonPatch = MAPPER.readValue(json, JsonPatch.class);
        final JsonArray jsonPatchAsJsonArray = jsonPatch.toJsonArray();
        assertThat(jsonPatchAsJsonArray.get(0), instanceOf(JsonObject.class));

        final JsonObject firstOperation = jsonPatchAsJsonArray.get(0).asJsonObject();
        assertTrue(firstOperation.containsKey("op"));
        assertThat(firstOperation.get("op"), instanceOf(JsonString.class));
        assertThat(firstOperation.getString("op"), is("replace"));

        assertTrue(firstOperation.containsKey("path"));
        assertThat(firstOperation.get("path"), instanceOf(JsonString.class));
        assertThat(firstOperation.getString("path"), is("/name"));

        assertTrue(firstOperation.containsKey("value"));
        assertThat(firstOperation.get("value"), instanceOf(JsonString.class));
        assertThat(firstOperation.getString("value"), is("Json"));

        assertThat(serializeAsString(jsonPatchAsJsonArray), is(json));

        final Person person = new Person("John", "Smith");
        final JsonStructure personJson = MAPPER.convertValue(person, JsonStructure.class);
        final JsonStructure patchedPersonJson = jsonPatch.apply(personJson);
        final Person patchedPerson = MAPPER.convertValue(patchedPersonJson, Person.class);
        assertThat(patchedPerson, is(new Person("Json", "Smith")));
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
