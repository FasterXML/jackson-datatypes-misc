Datatype module to make [Jackson](../../../jackson)
recognize `JsonValue` types of JSON API defined in JSR-353 (JSON-P, "JSON-Processing"), so that
you can read JSON as `JsonValue`s and write `JsonValue`s as JSON as part of normal
Jackson processing.

Note that there is also a related [jackson-javax-json](https://github.com/pgelinas/jackson-javax-json)
module which actually *implements* JSR-353 using Jackson streaming API under the hood.

In both cases the main reason for use is interoperability, as well as to take advantage
of powerful data-binding features Jackson provides.
Another benefit is the
performance: Jackson implementation is often significantly faster for reading and writing
JSON content than Oracle's JSR-353 Reference Implementation.

Starting with version 2.11 supports JSON-P 1.1
(that is, [JSR-374](https://www.jcp.org/en/jsr/detail?id=374)
as well.

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-jsr353</artifactId>
  <version>2.11.0</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

Also unless you already include a dependency to a JSR-353 implementation (JDK does not ship
with one at least with JDK 8 and prior), you may need to include one.
Implementations include:

* [JSR 353 Reference Implementation](https://jsonp.java.net/)
* [Jackson-javax-json](https://github.com/pgelinas/jackson-javax-json)

Reference implementation dependency would be:

```xml
<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>javax.json</artifactId>
    <!-- for versions up to 2.10.x, 1.0.4 or above may be used; 2.11 will require 1.1 or above
    <version>1.1</version>
</dependency>
```

### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows (Jackson 2.x up to 2.9)

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JSR353Module());
```
OR, the new method added in 2.10 (old method will work with 2.x but not 3.x):


```java
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new JSR353Module())
    .build();
```

after which functionality is available for all normal Jackson operations:
you can read JSON as `JsonValue` (or its subtypes), `JsonValues` as JSON, like:

```java
JsonObject ob = mapper.readValue(JSON, JsonObject.class);
mapper.writeValue(new File("stuff.json"), ob);
```
