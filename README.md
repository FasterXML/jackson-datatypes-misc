## Overview

This is a multi-module umbrella project for various [Jackson](../../../jackson)
datatype modules to support 3rd party libraries.

Currently included are:

* [joda-money](joda-money/) for [Joda-Money](https://www.joda.org/joda-money/) datatypes
* [jsr-353](jsr-353/) for [JSR-353](https://www.jcp.org/en/jsr/detail?id=353) (aka JSON-P) datatypes
* [org.json](json-org/) for ([org.json](http://json.org/java)) JSON model datatypes (included in Android SDK)

Note that this repo was created for Jackson 2.11: prior to this, individual datatype
modules had their own repositories.

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
  <version>2.11.0</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

To use the datatype module(s) with Jackson, simply register it
with the `ObjectMapper` instance:

```java
// import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;

// Jackson 2.x before 2.10:
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JsonOrgModule());
mapper.registerModule(new JSR353Module());
mapper.registerModule(new JodaMoneyModule());

// OR Jackson 2.10 and above
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new JsonOrgModule())
    .addModule(new JSR353Module())
    .addModule(new JodaMoneyModule())
    .build();
```

after which datatype read/write support is available for all normal Jackson operations,
