Datatype module to make [Jackson](../../../jackson)
recognize `JsonValue` types of JSON API defined in Jakarta JSON Processing specification, so that
you can read JSON as `JsonValue`s and write `JsonValue`s as JSON as part of normal
Jackson processing.

NOTE: this is for "new" version of JSON-P api under Java package `jakarta.json`,
NOT the "older" one under `javax.json`.

In both cases the main reason for use is interoperability, as well as to take advantage
of powerful data-binding features Jackson provides.
Another benefit is the
performance: Jackson implementation is often significantly faster for reading and writing
JSON content than Eclipse's Compatible Implementation.

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-jsonp</artifactId>
  <version>2.12.2</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

Also unless you already include a dependency to a Jakarta JSON Processing implementation (JDK does not ship
with one at least with JDK 8 and prior), you may need to include one.
Implementations include:

* [Eclipse Jakarta JSON Processing](https://eclipse-ee4j.github.io/jsonp/)
* [Leadpony Joy](https://leadpony.github.io/joy/)

Reference implementation dependency would be:

```xml
<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>jakarta.json</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows (Jackson 2.x up to 2.9)

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JSONPModule());
```
OR, the new method added in 2.10 (old method will work with 2.x but not 3.x):


```java
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new JSONPModule())
    .build();
```

after which functionality is available for all normal Jackson operations:
you can read JSON as `JsonValue` (or its subtypes), `JsonValues` as JSON, like:

```java
JsonObject ob = mapper.readValue(JSON, JsonObject.class);
mapper.writeValue(new File("stuff.json"), ob);
```
