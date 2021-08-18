Datatype module to make [Jackson](../../../jackson)
read and write `InternetAddress`es using the Jakarta Mail API.

NOTE: This module handles the new `jakarta.mail` namespace, not the old
`javax.mail` namespace.

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-jakarta-mail</artifactId>
  <version>2.13.0</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

Like all standard Jackson modules (libraries that implement Module interface),
registration is done as follows (Jackson 2.x up to 2.9)

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JakartaMailModule());
```
OR, the new method added in 2.10 (old method will work with 2.x but not 3.x):


```java
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new JakartaMailModule())
    .build();
```

after which functionality is available for all normal Jackson operations:

```java
InternetAddress addr = mapper.readValue(JSON, InternetAddress.class);
```
