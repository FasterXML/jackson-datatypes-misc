## Overview

This is a multi-module umbrella project for various [Jackson](../../../jackson)
datatype modules to support 3rd party libraries.

Currently included are:

* [jackson-datatype-jakarta-mail](jakarta-mail/) for Jakarta Mail (ex-Java Mail) (starting with Jackson 2.13)
    * Currently (2.13) just type `jakarta.mail.internet.InternetAddress`
* [jackson-datatype-javax-money](javax-money/) for [JSR 354](https://github.com/JavaMoney/jsr354-api) datatypes (starting with Jackson 2.19)
* [jackson-datatype-moneta](moneta/) for [JavaMoney Moneta RI](https://javamoney.github.io/) datatypes (jsr354 reference implementation) (starting with Jackson 2.19)
* [jackson-datatype-joda-money](joda-money/) for [Joda-Money](https://www.joda.org/joda-money/) datatypes
* JSR-353/JSON-P: 2 variants (starting with Jackson 2.12.2)
    * [jackson-datatype-jsr353](jsr-353/) for older "javax.json" [JSR-353](https://www.jcp.org/en/jsr/detail?id=353) (aka JSON-P) datatypes (package `javax.json`)
    * [jackson-datatype-jakarta-jsonp](jakarta-jsonp/) for newer "Jakarta" JSON-P datatypes (package `jakarta.json`)
* [jackson-datatype-json-org](json-org/) for ([org.json](http://json.org/java)) JSON model datatypes (included in Android SDK, as well as stand-alone Java library)

Note that this repo was created for Jackson 2.11: prior to this, individual datatype
modules had their own repositories.

## License

All modules are licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

## Status

| Type | Status |
| ---- | ------ |
| Build (CI) | [![Build (github)](https://github.com/FasterXML/jackson-datatypes-misc/actions/workflows/main.yml/badge.svg)](https://github.com/FasterXML/jackson-datatypes-misc/actions/workflows/main.yml) |
| Code coverage (2.18) | [![codecov.io](https://codecov.io/github/FasterXML/jackson-datatypes-misc/coverage.svg?branch=2.18)](https://codecov.io/github/FasterXML/jackson-datatypes-misc?branch=2.18) |


## Usage

### Maven dependency

To use module (version 2.x) on Maven-based projects, use dependency like
(for which module(s) you want):

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-json-org</artifactId>
  <version>2.18.3</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

To use the datatype module(s) with Jackson, simply register it
with the `ObjectMapper` instance:

```java
// import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
// ... and so on

// Jackson 2.x before 2.10:
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JsonOrgModule());
mapper.registerModule(new JodaMoneyModule());
// ONE of these (not both):
mapper.registerModule(new JSR353Module()); // old (javax) json-p API
mapper.registerModule(new JSONPModule()); // new (jakarta) json-P API

// OR Jackson 2.10 and above
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new JsonOrgModule())
    .addModule(new JodaMoneyModule())
    // ONE of these (not both):
    .addModule(new JavaxMoneyModule())
    .addModule(new MonetaMoneyModule())
    // ONE of these (not both):
    .addModule(new JSR353Module()) // old (javax) json-p API
    .addModule(new JSONPModule()) // new (jakarta) json-P API
    .build();
```

after which datatype read/write support is available for all normal Jackson operations.
