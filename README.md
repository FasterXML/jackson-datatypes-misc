## Overview

This is a multi-module umbrella project for various [Jackson](../../../jackson)
datatype modules to support 3rd party libraries.

Currently included are:

* [org.json](json-org/) for JSON model datatypes ([org.json](http://json.org/java)) (included in Android SDK)

## License

All modules are licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

## Build Status

[![Build Status](https://travis-ci.org/FasterXML/jackson-datatypes-misc.svg)](https://travis-ci.org/FasterXML/jackson-datatypes-misc)

## Usage

### Maven dependency

To use module (version 2.x) on Maven-based projects, use dependency like
(for which module(s) you want):

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-json-org</artifactId>
  <version>2.10.0</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

To use the the datatype module(s) with Jackson, simply register it
with the `ObjectMapper` instance:

```java
// import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;

// Jackson 2.x before 2.10:
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JsonOrgModule());

// OR Jackson 2.10 and above
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new JsonOrgModule())
    .build();
```

after which datatype read/write support is available for all normal Jackson operations,
