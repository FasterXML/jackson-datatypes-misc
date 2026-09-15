# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Multi-module Maven umbrella for Jackson datatype modules that add (de)serialization support for 3rd-party libraries. This branch line is **Jackson 3.x** (`tools.jackson.*` packages and group ids, Java 17+); Jackson 2.x lives on the `2.x`/`2.NN` branches (`com.fasterxml.jackson.*`).

| Directory | Artifact | Java package |
| --- | --- | --- |
| `json-org` | `jackson-datatype-json-org` | `tools.jackson.datatype.jsonorg` |
| `jsr-353` | `jackson-datatype-jsr353` (old `javax.json`) | `tools.jackson.datatype.jsr353` |
| `jakarta-jsonp` | `jackson-datatype-jakarta-jsonp` (`jakarta.json`) | `tools.jackson.datatype.jsonp` |
| `joda-money` | `jackson-datatype-joda-money` | `tools.jackson.datatype.jodamoney` |
| `jakarta-mail` | `jackson-datatype-jakarta-mail` | `tools.jackson.datatype.jakarta.mail` |
| `javax-money` | `jackson-datatype-javax-money` (JSR 354 API) | `tools.jackson.datatype.javax.money` |
| `moneta` | `jackson-datatype-moneta` (depends on `javax-money`) | `tools.jackson.datatype.moneta` |

`jsr-353` and `jakarta-jsonp` are near-identical parallel implementations (javax vs jakarta namespace); a fix in one usually needs porting to the other.

## Build & Test

Use the Maven wrapper. The parent is `tools.jackson:jackson-base` SNAPSHOT, resolved from the Sonatype Central snapshots repo declared in `pom.xml`.

```sh
./mvnw clean verify                          # full build + tests (what CI runs, on JDK 17/21/24)
./mvnw -pl json-org test                     # one module
./mvnw -pl moneta -am test                   # module plus modules it depends on (moneta needs javax-money)
./mvnw -pl json-org test -Dtest=SimpleReadTest              # single test class
./mvnw -pl json-org test -Dtest=SimpleReadTest#testMethod   # single test method
```

Tests use JUnit 5; some modules also use AssertJ (and Mockito for the money modules).

## Module structure conventions

Every module follows the same pattern — keep new code consistent with it:

- **Module class** (`JsonOrgModule`, `JSONPModule`, `JavaxMoneyModule`, ...) extends `SimpleModule`, passes `PackageVersion.VERSION` to the constructor, and registers serializers/deserializers.
- **`PackageVersion.java.in`** is a template; `PackageVersion.java` is generated at build time by `maven-replacer-plugin` using `packageVersion.dir`/`packageVersion.package` properties in the module POM. Do not create or edit `PackageVersion.java` by hand.
- **JPMS**: each module has `src/main/java/module-info.java` *and* a separate `src/test/java/module-info.java` (same module name, with test deps added and the package `opens`ed). Adding a new runtime or test dependency usually requires updating the corresponding `module-info.java`.
- **SPI**: `src/main/resources/META-INF/services/tools.jackson.databind.JacksonModule` lists the module class, mirrored by `provides ... with` in `module-info.java`. `ModuleSPIMetadataTest` verifies it; `TestVersions` verifies module name/version.
- Test helper base classes (`ModuleTestBase`, `TestBase`) provide `newMapper()`/`newMapperBuilder()` with the module registered, and `a2q()` (convert single-quoted JSON to double-quoted) for writing JSON in tests.

## Branching and release notes

- Fixes go to the oldest applicable branch and are merged forward: `2.NN` → `2.NN+1` → `2.x` → `3.1` → `3.2` → `3.x` (main 3.x development branch).
- User-visible changes get an entry in `release-notes/VERSION` (3.x) or `release-notes/VERSION-2.x` (2.x) under the unreleased version, formatted as `#NN: (module-name) Description`; contributors are credited in `release-notes/CREDITS` / `CREDITS-2.x`.
