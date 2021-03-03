Project: jackson-datatypes-misc
Modules:
  jackson-datatype-joda-money
  jackson-datatype-json-org
  jackson-datatype-jsr353

------------------------------------------------------------------------
=== Releases ===
------------------------------------------------------------------------

2.13.0 (not yet released)

#8: Improve error handling of "joda-money" `MoneyDeserializer`, `CurrencyUnitDeserializer`

2.12.2 (03-Mar-2021)

#6: Add `jakarta` classifier version of `jackson-datatype-jsr353` to work
  with new Jakarta-based JSON-P
 (reported by Filip K, fix contributed by Florian W)

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
