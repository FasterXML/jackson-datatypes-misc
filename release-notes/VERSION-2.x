Project: jackson-datatypes-misc
Modules:
  jackson-datatype-joda-money
  jackson-datatype-json-org
  jackson-datatype-jsr353
  jackson-datatype-jakarta-jsonp (2.13 alt to -jsr353)
  jackson-datatype-jakarta-mail (2.13)  

------------------------------------------------------------------------
=== Releases ===
------------------------------------------------------------------------

2.16.0-rc1 (20-Oct-2023)

#37: (jsonp/jsr-353): Fix class path scanning on each deserialization
 (contributed by Konstantin V)
- Upgrade `org.json:json`` dependency to 20231013 (from 20230227)

2.15.3 (12-Oct-2023)
2.15.2 (30-May-2023)
2.15.1 (16-May-2023)

No changes since 2.15.0

2.15.0 (23-Apr-2023)

#31/#32/#33: (jsonp/jsr-353) Fix issue with `BigInteger` handling
 (contributed by @pjfanning)
#34: Upgrade `jakarta.json-api` dependency to 2.1.1 (from 2.0.0)
 (contributed by @pjfanning)
#35: Update `org.json` dependency from `20190722` to `20230227`

2.14.3 (05-May-2023)

No changes since 2.14.2

2.14.2 (28-Jan-2023)

#28: (jsonp/jsr-353) Add delegating serializers for `JsonPatch` and `JsonMergePatch`
 (requested by Matt N)

2.14.1 (21-Nov-2022)

No changes since 2.14.0

2.14.0 (05-Nov-2022)

#17: (joda-money) Add configurable amount representations for Joda Money module
 (contributed by Andrzej P)
#19: (jsonp/jsr-353)`JsonValue.NULL` deserialization has different behaviours
  with constructor properties vs public properties
 (contributed by xdrsynapse@github)
#27: (jsonp/jsr-353) Deserializing a JSON Merge Patch fails when the input
  is not a JSON object
 (contributed by Pascal-V-E)

2.13.5 (not yet released)

#27: (jsonp/jsr-353) Deserializing a JSON Merge Patch fails when the input
  is not a JSON object
 (contributed by Pascal-V-E)

2.13.4 (03-Sep-2022)
2.13.3 (14-May-2022)
2.13.2 (06-Mar-2022)
2.13.1 (19-Dec-2021)

No changes since 2.13.0

2.13.0 (30-Sep-2021)

#8: Improve error handling of "joda-money" `MoneyDeserializer`, `CurrencyUnitDeserializer`
#16: Add datatype module for Jakarta Mail
 (contributed by Christopher S)
- Change module names declared to use artifact-id (for json-org, "JsonOrgModule" to
  "jackson-datatype-json-org", for example)

2.12.7 (26-May-2022)
2.12.6 (15-Dec-2021)
2.12.5 (27-Aug-2021)
2.12.4 (06-Jul-2021)

No changes since 2.12.3

2.12.3 (12-Apr-2021)

#12: (jsonp) Add alternate constructor in `JSONPModule`/`JSR353Module` to
  take `JsonProvider`
 (contributed by Thiago H)

2.12.2 (03-Mar-2021 / 04-Mar-2021)

#10: (jsonp) Add Jakarta JSON Processing module (`jackson-datatype-jsonp`)
 (contributed by Thiago H)

2.12.1 (08-Jan-2021)

#7: Jackson JSR353 library is using wrong module name for javax json api
  (reported by Ryan M)

2.12.0 (29-Nov-2020)

- Add Gradle Module Metadata (https://blog.gradle.org/alignment-with-gradle-module-metadata)

2.11.4 (12-Dec-2020)
2.11.3 (02-Oct-2020)
2.11.2 (02-Aug-2020)

No changes since 2.11.1

2.11.1 (25-Jun-2020)

#2: (jsr-353) Input `null` being deserialized as Java null value instead
  of `JsonValue.NULL`

2.11.0 (26-Apr-2020)

* New module, `jackson-datatype-joda-money`
  (contributed by Iurii I)
* (org.json) Upgrade `org.json` dependency to `20190722` (from `20180813`)
