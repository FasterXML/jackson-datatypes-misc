package com.fasterxml.jackson.datatype.jsr353;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.json.JsonMergePatch;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonMergePatchDeserializationTest extends TestBase {

    private static final ObjectMapper MAPPER = newMapper();

    public void testDeserializationAndPatching() throws Exception {
        final String json = "{" +
                "\"name\":\"Json\"" +
                "}";

        final JsonMergePatch jsonMergePatch = MAPPER.readValue(json, JsonMergePatch.class);
        final JsonValue jsonPatchAsJsonValue = jsonMergePatch.toJsonValue();
        assertThat(jsonPatchAsJsonValue, instanceOf(JsonObject.class));

        final JsonObject jsonPatchAsJsonObject = jsonPatchAsJsonValue.asJsonObject();
        assertTrue(jsonPatchAsJsonObject.containsKey("name"));
        assertThat(jsonPatchAsJsonObject.get("name"), instanceOf(JsonString.class));
        assertThat(jsonPatchAsJsonObject.getString("name"), is("Json"));

        assertThat(serializeAsString(jsonPatchAsJsonValue), is(json));

        final Person person = new Person("John", "Smith");
        final JsonValue personJson = MAPPER.convertValue(person, JsonValue.class);
        final JsonValue patchedPersonJson = jsonMergePatch.apply(personJson);
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
